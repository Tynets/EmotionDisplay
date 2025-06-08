package com.example.myapplication.bluetooth

data class Message(var message: String)

fun Message.toByteArray(): ByteArray {
    return message.toByteArray()
}