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
import androidx.recyclerview.widget.RecyclerView
import com.sky.inventary_app.R
import dbProductos
import java.io.File

class ProductosDisponiblesAdapter(
    private val productosList: List<dbProductos>,
    private val context: Context,
    private val onClick: (dbProductos) -> Unit
) : RecyclerView.Adapter<ProductosDisponiblesAdapter.ProductoViewHolder>() {

    // Lista para llevar el seguimiento de los productos añadidos
    private val productosAñadidos = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view, context, onClick, productosAñadidos)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productosList[position]
        holder.bind(producto)
    }

    override fun getItemCount(): Int {
        return productosList.size
    }

    class ProductoViewHolder(
        itemView: View,
        private val context: Context,
        private val onClick: (dbProductos) -> Unit,
        private val productosAñadidos: MutableList<String>
    ) : RecyclerView.ViewHolder(itemView) {

        private val imgProducto: ImageView = itemView.findViewById(R.id.imgProducto)
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvPrecioVenta: TextView = itemView.findViewById(R.id.tvPrecioVenta)
        private val tvStock: TextView = itemView.findViewById(R.id.tvStock)

        private lateinit var currentProducto: dbProductos

        init {
            itemView.setOnClickListener {
                if (::currentProducto.isInitialized) {
                    // Verificar si el producto ya está añadido
                    if (!productosAñadidos.contains(currentProducto.nombreProducto) && currentProducto.stock > 0) {
                        productosAñadidos.add(currentProducto.nombreProducto)
                        onClick(currentProducto)
                    } else {
                        // Mostrar un mensaje al usuario (puedes usar un Toast, Snackbar, etc.)
                        // Ejemplo:
                         Toast.makeText(context, "El producto ya está en la lista o no tiene stock", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        fun bind(producto: dbProductos) {
            currentProducto = producto
            tvNombre.text = producto.nombreProducto.toUpperCase()
            tvPrecioVenta.text = "Precio: S/.${String.format("%.2f", producto.precioVenta)}"
            tvStock.text = "Stock: ${producto.stock} u"

            // Cargar imagen desde almacenamiento
            val storageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "fotosProductos")
            val imageFile = File(storageDir, "${producto.nombreProducto}.jpg")
            if (imageFile.exists()) {
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                imgProducto.setImageBitmap(bitmap)
            } else {
                imgProducto.setImageResource(R.drawable.ic_launcher_foreground) // Imagen de marcador de posición
            }
        }
    }
}
