package com.example.footballapp

import android.app.backup.BackupAgent

data class UserSignUpData(
    var firstNameData   : String? = null,
    var lastNameData    : String? = null,
    var emailData       : String? = null,
    var age             : String? = null,
    var team            : String? = null
) {
    // do something or nothing
}

