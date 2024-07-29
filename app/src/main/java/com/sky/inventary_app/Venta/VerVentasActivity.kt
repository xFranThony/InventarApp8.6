package com.sky.inventary_app.Venta

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.sky.inventary_app.Data.dbVenta
import com.sky.inventary_app.MenuActivity
import com.sky.inventary_app.R

class VerVentasActivity : AppCompatActivity() {

    private lateinit var rvVentas: RecyclerView
    private lateinit var ventasAdapter: VentasAdapter
    private val ventasList = mutableListOf<dbVenta>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_ventas)

        rvVentas = findViewById(R.id.rvVentas)
        rvVentas.layoutManager = LinearLayoutManager(this)

        ventasAdapter = VentasAdapter(ventasList, this)
        rvVentas.adapter = ventasAdapter

        cargarVentas()
    }

    private fun cargarVentas() {
        db.collection("Ventas")
            .get()
            .addOnSuccessListener { result ->
                ventasList.clear()
                for (document in result) {
                    val venta = document.toObject(dbVenta::class.java)
                    ventasList.add(venta)
                }
                ventasAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al cargar las ventas: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MenuActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

}
