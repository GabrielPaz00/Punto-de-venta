package com.example.pointofsale.viewmodel

class Greeting {
    fun greet(): String {
        val androidVersion = android.os.Build.VERSION.RELEASE
        return "Hello, Android $androidVersion"
    }
}