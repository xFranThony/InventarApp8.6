package com.sky.inventary_app

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.sky.inventary_app.RV.AnadirProductoActivity
import com.sky.inventary_app.RV.MostrarProductosActivity
import com.sky.inventary_app.Venta.CrearVentaActivity
import com.sky.inventary_app.Venta.VerVentasActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val btnAnadirProducto: LinearLayout = findViewById(R.id.layoutAnadirProducto)
        val btnCrearVenta: LinearLayout = findViewById(R.id.layoutCrearVenta)
        val btnVerVenta: LinearLayout = findViewById(R.id.layoutVerVentas)
        val btnMostrarProductos: LinearLayout = findViewById(R.id.layoutMostrarProductos)
        val ivCrearVenta: ImageView=findViewById(R.id.ivCrearVenta)
        val ivVerVentas: ImageView=findViewById(R.id.ivVerVentas)
        val ivAnadirProducto: ImageView=findViewById(R.id.ivAnadirProducto)
        val ivMostrarProductos: ImageView=findViewById(R.id.ivMostrarProductos)

        Glide.with(this)
            .asGif()
            .load(R.drawable.compras) // Nombre del archivo GIF en drawable
            .into(ivCrearVenta)

        Glide.with(this)
            .asGif()
            .load(R.drawable.lista) // Nombre del archivo GIF en drawable
            .into(ivVerVentas)

        Glide.with(this)
            .asGif()
            .load(R.drawable.productos) // Nombre del archivo GIF en drawable
            .into(ivAnadirProducto)

        Glide.with(this)
            .asGif()
            .load(R.drawable.lista3) // Nombre del archivo GIF en drawable
            .into(ivMostrarProductos)

        btnCrearVenta.setOnClickListener {
            startActivity(Intent(this, CrearVentaActivity::class.java))
            finish()
        }

        btnVerVenta.setOnClickListener {
            startActivity (Intent(this, VerVentasActivity::class.java))
            finish()
        }


        btnAnadirProducto.setOnClickListener {
            startActivity(Intent(this, AnadirProductoActivity::class.java))
            finish()
        }

        btnMostrarProductos.setOnClickListener {
            startActivity(Intent(this, MostrarProductosActivity::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar")
            .setMessage("¿Salir de InventaryApp?")
            .setPositiveButton("Sí") { _, _ ->
                super.onBackPressed() // Llama al método original para cerrar la actividad
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}