package com.example.myapplication.Model


data class Caches(
    val elements: MutableMap<String, GeoCaches>
    )


data class GeoCaches (
    val name: String,
    val location: String
)