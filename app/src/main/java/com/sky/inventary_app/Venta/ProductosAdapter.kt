package com.sky.inventary_app.Venta

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.sky.inventary_app.R
import dbProductos
import java.io.File

class ProductosAdapter(
    private val productosList: MutableList<dbProductos>,
    private val context: Context,
    private val onCantidadChanged: () -> Unit
) : RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_producto2, parent, false)
        return ProductoViewHolder(view, context, onCantidadChanged)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productosList[position]
        holder.bind(producto)
    }

    override fun getItemCount(): Int {
        return productosList.size
    }

    fun removeItem(position: Int) {
        productosList.removeAt(position)
        notifyItemRemoved(position)
    }

    class ProductoViewHolder(
        itemView: View,
        private val context: Context,
        private val onCantidadChanged: () -> Unit // Callback para cambios en la cantidad
    ) : RecyclerView.ViewHolder(itemView) {

        private val imgProducto: ImageView = itemView.findViewById(R.id.imgProducto)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvPrecioVenta: TextView = itemView.findViewById(R.id.tvPrecioVenta)
        private val tvStock: TextView = itemView.findViewById(R.id.tvStock)
        private val aumentar: TextView = itemView.findViewById(R.id.aumentar)
        private val disminuir: TextView = itemView.findViewById(R.id.disminuir)
        private val totalProducto: TextView = itemView.findViewById(R.id.totalProducto)

        private lateinit var currentProducto: dbProductos
        private var cantidad: Int = 1

        init {
            aumentar.setOnClickListener {
                if (cantidad < currentProducto.stock) {
                    cantidad++
                    actualizarTotalProducto()
                } else {
                    Toast.makeText(context, "No se puede aumentar más allá del stock disponible", Toast.LENGTH_SHORT).show()
                }
            }

            disminuir.setOnClickListener {
                if (cantidad > 1) {
                    cantidad--
                    actualizarTotalProducto()
                }
            }
        }

        fun bind(producto: dbProductos) {
            currentProducto = producto
            tvNombre.text = producto.nombreProducto.toUpperCase()
            tvPrecioVenta.text = "Precio: S/.${String.format("%.2f", producto.precioVenta)}"
            tvStock.text = "Stock: ${producto.stock} u"

            val storageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "fotosProductos")
            val imageFile = File(storageDir, "${producto.nombreProducto}.jpg")
            if (imageFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                imgProducto.setImageBitmap(bitmap)
            } else {
                imgProducto.setImageResource(R.drawable.ic_launcher_foreground)
            }

            actualizarTotalProducto()
        }

        private fun actualizarTotalProducto() {
            val total = cantidad * currentProducto.precioVenta
            totalProducto.text = "Total: S/.${String.format("%.2f", total)} / ${cantidad} u"

            val sharedPreferences = context.getSharedPreferences("ProductosPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putFloat("total_${currentProducto.nombreProducto}", total.toFloat())
            editor.apply()

            onCantidadChanged()
        }
    }
}
