package com.sky.inventary_app.Venta

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.example.tuapp.VentaDetallesDialogFragment
import com.google.gson.Gson
import com.sky.inventary_app.R
import dbProductos

class CrearVentaActivity : AppCompatActivity() {

    private lateinit var rvProductos: RecyclerView
    private lateinit var tvTotalVenta: TextView
    private lateinit var btnCancelar: Button
    private lateinit var btnAñadirProducto: Button
    private lateinit var btnFinalizarVenta: Button
    private lateinit var tvEmptyView: TextView
    private lateinit var etNombreCliente: EditText
    private lateinit var layoutCliente: LinearLayout

    private lateinit var productosAdapter: ProductosAdapter
    private val productosList = mutableListOf<dbProductos>()
    private var totalVenta: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_venta)

        rvProductos = findViewById(R.id.rvProductos)
        tvTotalVenta = findViewById(R.id.tvTotalVenta)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnAñadirProducto = findViewById(R.id.btnAñadirProducto)
        btnFinalizarVenta = findViewById(R.id.btnFinalizarVenta)
        tvEmptyView = findViewById(R.id.tvEmptyView)
        etNombreCliente = findViewById(R.id.etNombreCliente)
        layoutCliente=findViewById(R.id.layoutCliente)

        productosAdapter = ProductosAdapter(productosList, this) {
            calcularTotalVenta()
            verificarRecyclerViewVacio()
        }
        rvProductos.layoutManager = LinearLayoutManager(this)
        rvProductos.adapter = productosAdapter

        // Configurar ItemTouchHelper para manejar el deslizamiento
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                productosAdapter.removeItem(position)
                calcularTotalVenta()
                verificarRecyclerViewVacio()
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(rvProductos)

        btnCancelar.setOnClickListener {
            finish()
        }

        btnAñadirProducto.setOnClickListener {
            val intent = Intent(this, AnadirProductoVentaActivity::class.java)
            startActivity(intent)
        }

        btnFinalizarVenta.setOnClickListener {
            finalizarVenta()
        }

        verificarRecyclerViewVacio()
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("ProductosPrefs", MODE_PRIVATE)
        val productoJson = sharedPreferences.getString("producto", null)
        if (productoJson != null) {
            val producto = Gson().fromJson(productoJson, dbProductos::class.java)

            // Verificar si el producto tiene stock 0
            if (producto.stock == 0) {
                Toast.makeText(this, "No se puede añadir un producto con stock 0", Toast.LENGTH_SHORT).show()
                return
            }

            // Verificar si el producto ya está en la lista
            if (productosList.any { it.nombreProducto == producto.nombreProducto }) {
                Toast.makeText(this, "El producto ya está en la lista", Toast.LENGTH_SHORT).show()
                return
            }

            productosList.add(producto)
            productosAdapter.notifyDataSetChanged()
            calcularTotalVenta()
            verificarRecyclerViewVacio()
            sharedPreferences.edit().remove("producto").apply() // Limpiar SharedPreferences
        }
    }

    private fun finalizarVenta() {
        // Guardar los datos de la venta en SharedPreferences
        val sharedPreferences = getSharedPreferences("VentaPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val productosJson = Gson().toJson(productosList)
        editor.putString("productos", productosJson)
        editor.putString("totalVenta", totalVenta.toString())

        // Agregar datos adicionales de la venta
        editor.putString("idVenta", "12345") // Este es un ejemplo, puedes cambiarlo por el ID real

        // Verificar si etNombreCliente está vacío y asignar un valor por defecto si es necesario
        val nombreComprador = if (etNombreCliente.text.isNullOrEmpty()) {
            "Random"
        } else {
            etNombreCliente.text.toString()
        }
        editor.putString("nombreComprador", nombreComprador)
        editor.putString("idVendedor", "Admin") // Este es un ejemplo, puedes cambiarlo por el ID real del vendedor
        editor.apply()

        // Crear una nueva instancia del dialogo con detalles
        val dialog = VentaDetallesDialogFragment.newInstance()
        dialog.show(supportFragmentManager, "VentaDetallesDialogFragment")
    }

    private fun calcularTotalVenta() {
        val sharedPreferences = getSharedPreferences("ProductosPrefs", MODE_PRIVATE)
        totalVenta = productosList.sumOf {
            sharedPreferences.getFloat("total_${it.nombreProducto}", it.precioVenta.toFloat()).toDouble()
        }
        tvTotalVenta.text = "Total: S/.${String.format("%.2f", totalVenta)}"
    }

    private fun verificarRecyclerViewVacio() {
        if (productosList.isEmpty()) {
            rvProductos.visibility = View.GONE
            tvEmptyView.visibility = View.VISIBLE
            layoutCliente.visibility=View.GONE
            tvTotalVenta.visibility=View.GONE
            btnFinalizarVenta.isEnabled = false
        } else {
            rvProductos.visibility = View.VISIBLE
            layoutCliente.visibility=View.VISIBLE
            tvTotalVenta.visibility=View.VISIBLE
            tvEmptyView.visibility = View.GONE
            btnFinalizarVenta.isEnabled = true
        }
    }
}
