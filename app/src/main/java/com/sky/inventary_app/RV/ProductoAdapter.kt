package com.sky.inventary_app.RV

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.sky.inventary_app.R
import dbProductos
import java.io.File

class ProductoAdapter(
    private val productoList: ArrayList<dbProductos>,
    private val context: Context,
    private val emptyListView: TextView // Añade esta vista para mostrar el mensaje de lista vacía
) : RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    init {
        verificarListaVacia()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productoList[position]
        holder.tvNombre.text = producto.nombreProducto.toUpperCase()
        holder.tvDescripcion.text = producto.descripcion
        holder.tvPrecioVenta.text = "Precio: S/.${String.format("%.2f", producto.precioVenta)}"
        holder.tvStock.text = "Stock: ${producto.stock} u"

        // Load image from storage
        val storageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "fotosProductos")
        val imageFile = File(storageDir, "${producto.nombreProducto}.jpg")
        if (imageFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            holder.imgProducto.setImageBitmap(bitmap)
        } else {
            holder.imgProducto.setImageResource(R.drawable.ic_launcher_foreground) // Placeholder image
        }

        // Set item click listener
        holder.itemView.setOnClickListener {
            val activity = context as FragmentActivity
            val productoDetalleDialogFragment = ProductoDetalleDialogFragment.newInstance(producto)
            productoDetalleDialogFragment.show(activity.supportFragmentManager, "ProductoDetalleDialogFragment")
        }
    }

    override fun getItemCount(): Int {
        return productoList.size
    }

    // Change visibility to public
    fun verificarListaVacia() {
        emptyListView.visibility = if (productoList.isEmpty()) View.VISIBLE else View.GONE
    }

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProducto: ImageView = itemView.findViewById(R.id.imgProducto)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val tvPrecioVenta: TextView = itemView.findViewById(R.id.tvPrecioVenta)
        val tvStock: TextView = itemView.findViewById(R.id.tvStock)
    }
}
