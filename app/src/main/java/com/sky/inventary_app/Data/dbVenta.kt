package com.sky.inventary_app.Data

import dbProductos
import java.util.Date

data class dbVenta(
    val idVenta: String = "",
    val totalVenta: Double = 0.0,
    val fechaVenta: Date = Date(),
    val nombreComprador: String = "",
    val idVendedor: String = "",
    val productos: List<dbProductos> = listOf()
)
