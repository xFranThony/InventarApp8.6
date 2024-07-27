package com.sky.inventary_app.RV

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.sky.inventary_app.R
import dbProductos
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class AnadirProductoActivity : AppCompatActivity() {

    private var nombreUsuario: String? = null

    private lateinit var db: FirebaseFirestore
    private lateinit var etNombreProducto: EditText
    private lateinit var etPrecioCompra: EditText
    private lateinit var etPrecioVenta: EditText
    private lateinit var etStock: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var btnElegirImagen: Button
    private lateinit var btnGuardarProducto: Button
    private var imageUri: Uri? = null
    private lateinit var ivProducto: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anadir_producto)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        etNombreProducto = findViewById(R.id.etNombreProducto)
        etPrecioCompra = findViewById(R.id.etPrecioCompra)
        etPrecioVenta = findViewById(R.id.etPrecioVenta)
        etStock = findViewById(R.id.etStock)
        etDescripcion = findViewById(R.id.etDescripcion)
        btnElegirImagen = findViewById(R.id.btnElegirImagen)
        btnGuardarProducto = findViewById(R.id.btnGuardarProducto)
        ivProducto = findViewById(R.id.ivProducto)
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        nombreUsuario = sharedPreferences.getString("nombreUsuario", "desconocido")


        btnElegirImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1)
        }

        btnGuardarProducto.setOnClickListener {
            val nombreProducto = etNombreProducto.text.toString().trim()
            val precioCompra = etPrecioCompra.text.toString().toDoubleOrNull() ?: 0.0
            val precioVenta = etPrecioVenta.text.toString().toDoubleOrNull() ?: 0.0
            val stock = etStock.text.toString().toIntOrNull() ?: 0
            val descripcion = etDescripcion.text.toString().trim()

            if (nombreProducto.isEmpty()) {
                Toast.makeText(this, "El nombre del producto es obligatorio", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val fechaCreacion = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val creadoPor = nombreUsuario ?: "desconocido"

            val producto = dbProductos(
                idProducto = "id$nombreProducto",
                nombreProducto = nombreProducto,
                fechaCreacion = fechaCreacion,
                creadoPor = creadoPor,
                precioCompra = precioCompra,
                precioVenta = precioVenta,
                stock = stock,
                descripcion = descripcion,
                nombreImg = "$nombreProducto.jpg"
            )

            db.collection("Productos").document(nombreProducto).set(producto)
                .addOnSuccessListener {
                    if (imageUri != null) {
                        saveImageToStorage(imageUri!!, nombreProducto)
                    }
                    Toast.makeText(this, "Producto registrado: $nombreProducto", Toast.LENGTH_LONG).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al registrar el producto: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("Firebase", "Error al registrar el producto", e)
                }
        }
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
}
