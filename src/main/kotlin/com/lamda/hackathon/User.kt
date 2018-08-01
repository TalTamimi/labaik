package com.lamda.hackathon

data class User(
        val name: String,
        val lastName: String,
        val nationality: Int = 0,
        val age: Int = 0,
        val Gender: Int = 0,
        val campaignName: String,
        val ImageUrl: Int = 0,
        val phoneNumber: String,
        val latitude: String,
        val longitude: String,
        val travelingWithChild: Boolean = false,
        val travelingWithElder: Boolean = false
        )
