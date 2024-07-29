package com.sky.inventary_app.RV

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.sky.inventary_app.MenuActivity
import com.sky.inventary_app.R
import dbProductos
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class EditarProductoActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var tvNombreProducto: TextView
    private lateinit var etNuevoNombreProducto: EditText
    private lateinit var tvPrecioVenta: TextView
    private lateinit var etNuevoPrecioVenta: EditText
    private lateinit var tvStock: TextView
    private lateinit var etNuevoStock: EditText
    private lateinit var tvDescripcion: TextView
    private lateinit var etNuevaDescripcion: EditText
    private lateinit var btnGuardarCambios: Button
    private lateinit var btnElegirImagen: Button
    private lateinit var ivProducto: ImageView
    private lateinit var producto: dbProductos
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_producto)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Vincular vistas
        tvNombreProducto = findViewById(R.id.tvNombreProducto)
        etNuevoNombreProducto = findViewById(R.id.etNuevoNombreProducto)
        tvPrecioVenta = findViewById(R.id.tvPrecioVenta)
        etNuevoPrecioVenta = findViewById(R.id.etNuevoPrecioVenta)
        tvStock = findViewById(R.id.tvStock)
        etNuevoStock = findViewById(R.id.etNuevoStock)
        tvDescripcion = findViewById(R.id.tvDescripcion)
        etNuevaDescripcion = findViewById(R.id.etNuevaDescripcion)
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios)
        btnElegirImagen = findViewById(R.id.btnElegirImagen)
        ivProducto = findViewById(R.id.ivProducto)

        // Cargar producto desde SharedPreferences
        loadProductFromSharedPreferences()

        btnElegirImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1)
        }

        btnGuardarCambios.setOnClickListener {
            val nombreProducto = etNuevoNombreProducto.text.toString().trim()
            val precioVenta = etNuevoPrecioVenta.text.toString().toDoubleOrNull() ?: producto.precioVenta
            val stock = etNuevoStock.text.toString().toIntOrNull() ?: producto.stock
            val descripcion = etNuevaDescripcion.text.toString().trim().takeIf { it.isNotEmpty() } ?: producto.descripcion

            val updatedProducto = dbProductos(
                idProducto = producto.idProducto,
                nombreProducto = nombreProducto.takeIf { it.isNotEmpty() } ?: producto.nombreProducto,
                fechaCreacion = producto.fechaCreacion,
                creadoPor = producto.creadoPor,
                precioCompra = producto.precioCompra,
                precioVenta = precioVenta,
                stock = stock,
                descripcion = descripcion,
                nombreImg = "$nombreProducto.jpg"
            )

            db.collection("Productos")
                .whereEqualTo("idProducto", producto.idProducto)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val documentId = documents.documents[0].id
                        db.collection("Productos").document(documentId).set(updatedProducto)
                            .addOnSuccessListener {
                                if (imageUri != null) {
                                    // Nueva imagen seleccionada, eliminar la antigua y guardar la nueva
                                    deleteOldImage(producto.nombreProducto)
                                    saveImageToStorage(imageUri!!, nombreProducto)
                                } else if (nombreProducto.isNotEmpty() && nombreProducto != producto.nombreProducto) {
                                    // Solo se cambió el nombre del producto, renombrar la imagen existente
                                    renameImageFile(producto.nombreProducto, nombreProducto)
                                }

                                Toast.makeText(this, "Producto actualizado: ${updatedProducto.nombreProducto}", Toast.LENGTH_LONG).show()
                                val resultIntent = Intent()
                                setResult(RESULT_OK, resultIntent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al actualizar el producto: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al buscar el producto: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun loadProductFromSharedPreferences() {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val nombreProducto = sharedPreferences.getString("nombreProducto", "") ?: ""
        val precioCompra = sharedPreferences.getFloat("precioCompra", 0f).toDouble()
        val precioVenta = sharedPreferences.getFloat("precioVenta", 0f).toDouble()
        val stock = sharedPreferences.getInt("stock", 0)
        val descripcion = sharedPreferences.getString("descripcion", "") ?: ""
        val fechaCreacion = sharedPreferences.getString("fechaCreacion", "") ?: ""
        val creadoPor = sharedPreferences.getString("creadoPor", "") ?: ""
        val idProducto = sharedPreferences.getString("idProducto", "") ?: ""

        producto = dbProductos(
            idProducto = idProducto,
            nombreProducto = nombreProducto,
            fechaCreacion = fechaCreacion,
            creadoPor = creadoPor,
            precioCompra = precioCompra,
            precioVenta = precioVenta,
            stock = stock,
            descripcion = descripcion,
            nombreImg = "$nombreProducto.jpg"
        )

        tvNombreProducto.text = nombreProducto
        tvPrecioVenta.text = "S/.${String.format("%.2f", producto.precioVenta)}"
        tvStock.text = "${producto.stock} u"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            ivProducto.setImageURI(imageUri)
        }
    }

    private fun saveImageToStorage(imageUri: Uri, nombreProducto: String) {
        val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
        val storageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "fotosProductos")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        val imageFile = File(storageDir, "$nombreProducto.jpg")
        try {
            val outputStream = FileOutputStream(imageFile)
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun deleteOldImage(oldNombreProducto: String) {
        val storageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "fotosProductos")
        val oldImageFile = File(storageDir, "$oldNombreProducto.jpg")
        if (oldImageFile.exists()) {
            oldImageFile.delete()
        }
    }

    private fun renameImageFile(oldNombreProducto: String, newNombreProducto: String) {
        val storageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "fotosProductos")
        val oldImageFile = File(storageDir, "$oldNombreProducto.jpg")
        val newImageFile = File(storageDir, "$newNombreProducto.jpg")
        if (oldImageFile.exists()) {
            oldImageFile.renameTo(newImageFile)
        }
    }

    private fun restartActivity() {
        val intent = Intent(this, MostrarProductosActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar")
            .setMessage("¿Estás seguro de no modficar nada?")
            .setPositiveButton("Sí") { _, _ ->
                super.onBackPressed() // Llama al método original para cerrar la actividad
                startActivity (Intent(this, MostrarProductosActivity::class.java))
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
