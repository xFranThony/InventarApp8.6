package com.sky.inventary_app

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sky.inventary_app.RV.AnadirProductoActivity
import com.sky.inventary_app.RV.MostrarProductosActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val btnAnadirProducto: LinearLayout = findViewById(R.id.layoutAnadirProducto)

        val btnMostrarProductos: LinearLayout = findViewById(R.id.layoutMostrarProductos)

        btnAnadirProducto.setOnClickListener {
            startActivity(Intent(this, AnadirProductoActivity::class.java))
        }

        btnMostrarProductos.setOnClickListener {
            startActivity(Intent(this, MostrarProductosActivity::class.java))
        }
    }
}