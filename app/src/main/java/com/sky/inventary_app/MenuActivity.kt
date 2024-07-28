package com.sky.inventary_app

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sky.inventary_app.RV.AnadirProductoActivity
import com.sky.inventary_app.RV.MostrarProductosActivity
import com.sky.inventary_app.Venta.CrearVentaActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val btnAnadirProducto: LinearLayout = findViewById(R.id.layoutAnadirProducto)
        val btnCrearVenta: LinearLayout = findViewById(R.id.layoutCrearVenta)
        val btnVerVenta: LinearLayout = findViewById(R.id.layoutVerVentas)
        val btnMostrarProductos: LinearLayout = findViewById(R.id.layoutMostrarProductos)

        btnCrearVenta.setOnClickListener {
            startActivity(Intent(this, CrearVentaActivity::class.java))
        }

        btnVerVenta.setOnClickListener {
            //startActivity(Intent(this, EliminarProductoActivity::class.java))
            Toast.makeText(this, "Ver venta", Toast.LENGTH_SHORT).show()
        }

        btnAnadirProducto.setOnClickListener {
            startActivity(Intent(this, AnadirProductoActivity::class.java))
        }

        btnMostrarProductos.setOnClickListener {
            startActivity(Intent(this, MostrarProductosActivity::class.java))
        }
    }
}