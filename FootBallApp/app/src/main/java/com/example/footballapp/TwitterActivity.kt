package com.example.footballapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer
import se.akerfeldt.okhttp.signpost.SigningInterceptor

class TwitterActivity : AppCompatActivity() {

    companion object {
        private const val API_KEY = "SQS9fbJYgCsQStBRbcTfpAVtM"
        private const val API_SECRETKEY = "7Yk9PMkdxfofXecY5LV2pUMZVPIGgW4ve3gfUSJCTHxDwDVOmq"
        private const val ACCESS_TOKEN = "1259604474999582721-TrHwThsbUvIXOntjHvspmaVU9a5vdv"
        private const val ACCESS_TOKEN_SECRET = "Vz9dt6kfb7UvWh3gNrwMQPnJjGUWWfjNsJ0hvOKKuM4M3"
        private const val MAX_TWEETS = 100
        private const val RESULT_TYPE = "mixed"
        private const val RESULT_TYPE2 = "recent"
        private val BASE_URL = "https://api.twitter.com/"
    }

    lateinit var progress: ProgressBar
    lateinit var twitterRV: RecyclerView
    lateinit var searchTerm: EditText

    private val TAG = "Twitter"
    private var resultType = true

    private val credentials = Credentials.basic(API_KEY, API_SECRETKEY)
    var token: OAuthToken? = null

    // recycler view
    var searchList = ArrayList<SearchResults>()
    val twitterAdapter = TwitterAdapter(this, searchList)

    // Creating a Retrofit Instance
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(getClient())
        .build()

    // Invoke API Call create API call that will to call the interface
    val twitterApi = retrofit.create(TwitterEndPointSearch::class.java)

    // sensors
    private lateinit var sensorManager: SensorManager
    private var mAccelerometer: Sensor? = null
    var lastUpdate = System.currentTimeMillis()
    private var sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            // Many sensors return 3 values, one for each axis.
            //Android system calls onSensorChanged every time there’s a new sensor event. Its SensorEvent
            // parameter gives a set of array of size three, where each index represents a value of an
            // axes in a coordinate system: event.values[0] represents x, event.values[1] represents y
            // and event.values[2] for z.

            val sensor = event?.sensor ?: return

            when (sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {

                    // Axis of the Accelerometer sample, not normalized yet.
                    val x: Float = event.values[0]
                    val y: Float = event.values[1]
                    val z: Float = event.values[2]

                    Log.d(TAG, "Accelerometer ${event.values.joinToString(" ")}")

                    getAccelerometer(event)
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            //On the other hand, Android system only calls onAccuracyChanged when there’s a change
            // in accuracy. SensorManager contains all the accuracy change constants
            // in SensorManager.SENSOR_STATUS_*.
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_twitter)
        supportActionBar?.hide()

        getToken()

        searchTerm = findViewById(R.id.et_query)
        progress = findViewById(R.id.progressBar)
        progress.visibility = View.INVISIBLE

        //searchTweets()

        // recycler view
        twitterRV = findViewById(R.id.recycler_view_twitter)
        twitterRV.adapter = twitterAdapter
        twitterRV.layoutManager = LinearLayoutManager(this)

        //val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        //twitterRV.addItemDecoration(dividerItemDecoration)

        //sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Accelerometer Sensor
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        registerSensor(mAccelerometer)

        showDialog()
    }

    override fun onResume() {
        super.onResume()
        //sensorManager.registerListener(this,mAccelerometer,SensorManager.SENSOR_DELAY_NORMAL)
        registerSensor(mAccelerometer)
    }

    private fun registerSensor(sensor: Sensor?) {
        if (sensor != null) {
            sensorManager.registerListener(
                sensorEventListener,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun getAccelerometer(event: SensorEvent) {
        val values = event.values
        // Movement
        // Movement
        val x = values[0]
        val y = values[1]
        val z = values[2]

        val accelerationSquareRoot = ((x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH))
        val actualTime = event.timestamp
        if (accelerationSquareRoot >= 2) //
        {
            if (actualTime - lastUpdate < 200) {
                return
            }
            lastUpdate = actualTime
            Toast.makeText(this, "You did it! Tweets refreshed", Toast.LENGTH_SHORT)
                .show()
            searchTweets()
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorEventListener)
    }

    fun searchFromActivity(view: View) {
        searchTweets()
    }

    private fun getClient(): OkHttpClient {

        val consumer = OkHttpOAuthConsumer(API_KEY, API_SECRETKEY)
        consumer.setTokenWithSecret(ACCESS_TOKEN, ACCESS_TOKEN_SECRET)

        val client = OkHttpClient.Builder()
            .addInterceptor(SigningInterceptor(consumer))

        return client.build()
    }

    /**
     * As we are using OAuth, our credentials are different for each call. The postCredentials
     * method needs to post credentials in the Basic scheme to Twitter, which are composed from our
     * consumer key and the secret. As a result, this call returns the bearer token Retrofit
     * deserializes into our OAuthToken class, which we then store in the token field. Any further
     * request can (and have to) now use this token as its credentials for authorization. So does
     * the request to get the user details.
     */


    private fun getToken() {

        val okHttpClient =
            OkHttpClient.Builder().addInterceptor { chain ->
                val originalRequest = chain.request()
                val builder = originalRequest.newBuilder().header(
                    "Authorization", (if (token != null) {
                        token!!.tokenType + token!!.accessToken
                        //Credentials.basic(token!!.tokenType, token!!.accessToken)
                    } else {
                        credentials
                    })
                )
                val newRequest = builder.build()
                chain.proceed(newRequest)
            }.build()

        // Creating a Retrofit Instance
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build()

        // Invoke API Call create API call that will to call the interface
        val twitterApi = retrofit.create(TwitterEndPointSearch::class.java)

        twitterApi.postCredentials("client_credentials").enqueue(object : Callback<OAuthToken> {
            override fun onFailure(call: Call<OAuthToken>, t: Throwable) {
                Log.d(TAG, ": OnFailure token $t")
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<OAuthToken>, response: Response<OAuthToken>) {
                Log.d(TAG, ": OnResponse token  $response")
                val body = response.body()
                if (body == null) {
                    Log.w(TAG, "Valid response was not received")
                    return
                }
                token = response.body()!!
            }

        })

    }

    private fun searchTweets() {
        var search = " "
        var typeResult = " "

        // input validation
        if (searchTerm.text.toString().isNullOrEmpty()) {
            searchTerm.error = "Search term is required!"
            searchTerm.text.clear()
            searchTerm.requestFocus()
            searchTerm.hideKeyboard()
        }

        if ((searchTerm.text.isNullOrEmpty() || searchTerm.text.isNullOrBlank())
        ) {
            // do nothing
        } else {
            progress.visibility = View.VISIBLE
            search = searchTerm.text.toString()
            searchTerm.hideKeyboard()
            if (resultType) {
                typeResult = RESULT_TYPE
                resultType = false
            } else {
                typeResult = RESULT_TYPE2
                resultType = true
            }
            Log.d(TAG, ": result type  $typeResult")

            twitterApi.searchForTweets(search, typeResult, MAX_TWEETS)?.enqueue(object :
                Callback<SearchAPIData> {
                override fun onFailure(call: Call<SearchAPIData>, t: Throwable) {
                    Log.d(TAG, ": OnFailure tweet $t")
                }

                override fun onResponse(
                    call: Call<SearchAPIData>,
                    response: Response<SearchAPIData>
                ) {
                    Log.d(TAG, ": OnResponse tweet $response")
                    val body = response.body()
                    if (body == null) {
                        Log.w(TAG, "Valid response was not received")
                        return
                    }
                    //Log.d(TAG, ": OnFailure tweet $t")
                    val text = body.statuses[0].text
                    Log.d(TAG, "Tweets size: ${body.statuses.size}")
                    Log.d(TAG, "Tweets: $text")


                    searchList.addAll(body.statuses)
                    twitterAdapter.notifyDataSetChanged()
                    progress.visibility = View.INVISIBLE
                }
            })
        }

    }

    //utility---------------------------------------------------------------------------------------
// hide keyboard
    private fun View.hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    fun toMainActivity(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun showDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Pro Tip ")
        dialog.setMessage("To Refresh your search, give your phone a quick shake!")
        // Set an icon, optional
        dialog.setIcon(R.drawable.ic_baseline_check_circle_24)
        dialog.setNeutralButton("Okay") { dialog, which ->
            // code to run when Cancel is pressed
        }
        val dialogBox = dialog.create()
        dialogBox.show()
    }

}