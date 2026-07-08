package com.example.myapplication.feature.dashboard

data class Order(
    val id: String,
    val customer: String = "",
    val service: String,
    val weight: Double,
    val price: Int,
    val status: String,
    val date: String,
    val paid: Boolean
)

data class NotificationItem(
    val id: Int,
    val text: String,
    val time: String,
    val read: Boolean
)
