package com.sky.inventary_app.RV

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.sky.inventary_app.R
import dbProductos
import java.io.File

class ProductoDetalleDialogFragment : DialogFragment() {

    private lateinit var producto: dbProductos
    private lateinit var db: FirebaseFirestore
    private val EDIT_PRODUCT_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_producto_detalle, container, false)

        val ivProducto: ImageView = view.findViewById(R.id.ivProducto)
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val tvPrecioCompra: TextView = view.findViewById(R.id.tvPrecioCompra)
        val tvPrecioVenta: TextView = view.findViewById(R.id.tvPrecioVenta)
        val tvStock: TextView = view.findViewById(R.id.tvStock)
        val tvFechaCreacion: TextView = view.findViewById(R.id.tvFechaCreacion)
        val tvCreadoPor: TextView = view.findViewById(R.id.tvCreadoPor)
        val btnEditar: ImageButton = view.findViewById(R.id.btnEditar)

        db = FirebaseFirestore.getInstance()

        // Set text data
        setProductData(ivProducto, tvNombre, tvDescripcion, tvPrecioCompra, tvPrecioVenta, tvStock, tvFechaCreacion, tvCreadoPor)

        // Set edit button click listener
        btnEditar.setOnClickListener { view ->
            // Create and show the PopupMenu
            val popupMenu = PopupMenu(requireContext(), view, Gravity.END)
            val inflater: MenuInflater = popupMenu.menuInflater
            inflater.inflate(R.menu.popup_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        // Save product data to SharedPreferences
                        saveProductToSharedPreferences(producto)

                        // Start EditarProductoActivity
                        val intent = Intent(requireContext(), EditarProductoActivity::class.java)
                        startActivityForResult(intent, EDIT_PRODUCT_REQUEST_CODE)
                        dismiss()
                        true
                    }
                    R.id.action_delete -> {
                        val eliminarDialog = EliminarProductoDialogFragment.newInstance(producto)
                        eliminarDialog.show(parentFragmentManager, "EliminarProductoDialogFragment")
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        return view
    }

    private fun setProductData(
        ivProducto: ImageView,
        tvNombre: TextView,
        tvDescripcion: TextView,
        tvPrecioCompra: TextView,
        tvPrecioVenta: TextView,
        tvStock: TextView,
        tvFechaCreacion: TextView,
        tvCreadoPor: TextView
    ) {
        tvNombre.text = producto.nombreProducto.toUpperCase()
        tvDescripcion.text = producto.descripcion
        tvPrecioCompra.text = "Precio Compra: S/.${String.format("%.2f", producto.precioCompra)}"
        tvPrecioVenta.text = "Precio Venta: S/.${String.format("%.2f", producto.precioVenta)}"
        tvStock.text = "Stock: ${producto.stock} u"
        tvFechaCreacion.text = "Fecha Creación: ${producto.fechaCreacion}"
        tvCreadoPor.text = "Creado Por: ${producto.creadoPor}"

        // Load image from storage
        loadProductImage(ivProducto, producto.nombreProducto)
    }

    private fun saveProductToSharedPreferences(producto: dbProductos) {
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("nombreProducto", producto.nombreProducto)
        editor.putFloat("precioCompra", producto.precioCompra.toFloat())
        editor.putFloat("precioVenta", producto.precioVenta.toFloat())
        editor.putInt("stock", producto.stock)
        editor.putString("descripcion", producto.descripcion)
        editor.putString("fechaCreacion", producto.fechaCreacion)
        editor.putString("creadoPor", producto.creadoPor)
        editor.putString("idProducto", producto.idProducto)
        editor.apply()
    }

    private fun loadProductImage(imageView: ImageView, nombreProducto: String) {
        val storageDir = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "fotosProductos")
        val imageFile = File(storageDir, "$nombreProducto.jpg")

        if (imageFile.exists()) {
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            imageView.setImageBitmap(bitmap)
        } else {
            imageView.setImageResource(R.drawable.ic_launcher_foreground) // Placeholder image
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PRODUCT_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            // Reload product data from Firestore to reflect any changes
            db.collection("Productos").document(producto.idProducto).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        producto = document.toObject(dbProductos::class.java)!!
                        view?.let {
                            val ivProducto: ImageView = it.findViewById(R.id.ivProducto)
                            val tvNombre: TextView = it.findViewById(R.id.tvNombre)
                            val tvDescripcion: TextView = it.findViewById(R.id.tvDescripcion)
                            val tvPrecioCompra: TextView = it.findViewById(R.id.tvPrecioCompra)
                            val tvPrecioVenta: TextView = it.findViewById(R.id.tvPrecioVenta)
                            val tvStock: TextView = it.findViewById(R.id.tvStock)
                            val tvFechaCreacion: TextView = it.findViewById(R.id.tvFechaCreacion)
                            val tvCreadoPor: TextView = it.findViewById(R.id.tvCreadoPor)
                            setProductData(ivProducto, tvNombre, tvDescripcion, tvPrecioCompra, tvPrecioVenta, tvStock, tvFechaCreacion, tvCreadoPor)
                        }
                    }
                }
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
            val height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(producto: dbProductos) = ProductoDetalleDialogFragment().apply {
            this.producto = producto
        }
    }
}
