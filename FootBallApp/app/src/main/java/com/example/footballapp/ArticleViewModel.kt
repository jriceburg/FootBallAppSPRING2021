package com.example.footballapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ArticleViewModel : ViewModel() {
    var globalURL = MutableLiveData<String>()
}