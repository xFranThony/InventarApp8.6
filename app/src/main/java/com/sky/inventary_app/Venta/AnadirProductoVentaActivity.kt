package com.sky.inventary_app.Venta

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.sky.inventary_app.R
import dbProductos

class AnadirProductoVentaActivity : AppCompatActivity() {

    private lateinit var rvTodosProductos: RecyclerView
    private lateinit var productosDisponiblesAdapter: ProductosDisponiblesAdapter
    private val todosProductosList = mutableListOf<dbProductos>()
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anadir_producto_venta)

        rvTodosProductos = findViewById(R.id.rvTodosProductos)

        productosDisponiblesAdapter = ProductosDisponiblesAdapter(todosProductosList, this) { producto ->
            val sharedPreferences = getSharedPreferences("ProductosPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val productoJson = Gson().toJson(producto)
            editor.putString("producto", productoJson)
            editor.apply()

            finish()
        }
        rvTodosProductos.layoutManager = LinearLayoutManager(this)
        rvTodosProductos.adapter = productosDisponiblesAdapter

        db = FirebaseFirestore.getInstance()

        cargarProductos()
    }

    private fun cargarProductos() {
        db.collection("Productos").get()
            .addOnSuccessListener { documents ->
                todosProductosList.clear()
                for (document in documents) {
                    val producto = document.toObject(dbProductos::class.java)
                    todosProductosList.add(producto)
                }
                productosDisponiblesAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Manejar errores aquí
            }
    }
}
