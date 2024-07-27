package com.sky.inventary_app

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.sky.inventary_app.Data.dbUsuarios
import com.sky.inventary_app.RV.AnadirProductoActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var etUsuario: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnIngresar: Button
    private lateinit var btnRegistrarse: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        //Thread.sleep(2000)

        setTheme(R.style.Base_Theme_Inventary_App)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        db = FirebaseFirestore.getInstance()

        etUsuario = findViewById(R.id.etUsuario)
        etContrasena = findViewById(R.id.etContrasena)
        btnIngresar = findViewById(R.id.btnIngresar)
        btnRegistrarse = findViewById(R.id.btnRegistrarse)

        setClickListeners()
    }

    private fun setClickListeners() {
        btnIngresar.setOnClickListener {
            val nombreUsuario = etUsuario.text.toString().trim()
            val password = etContrasena.text.toString().trim()

            if (nombreUsuario.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            verificarUsuario(nombreUsuario, password)
        }

        btnRegistrarse.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    private fun verificarUsuario(nombreUsuario: String, password: String) {
        val docRef = db.collection("Usuarios").document(nombreUsuario)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val usuario = document.toObject(dbUsuarios::class.java)
                    verificarContrasena(usuario, password, nombreUsuario)
                } else {
                    Toast.makeText(this, "El usuario no existe", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al obtener los datos: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun verificarContrasena(usuario: dbUsuarios?, password: String, nombreUsuario: String) {
        if (usuario != null && usuario.password == password) {
            onLoginSuccess(nombreUsuario)
        } else {
            Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_LONG).show()
        }
    }

    private fun onLoginSuccess(nombreUsuario: String) {
        Toast.makeText(this, "Bienvenido $nombreUsuario", Toast.LENGTH_LONG).show()

        // Guardar el nombre de usuario en SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("nombreUsuario", nombreUsuario)
        editor.apply()

        // Iniciar AnadirProductoActivity
        val intentAnadirProducto = Intent(this, AnadirProductoActivity::class.java)
        startActivity(intentAnadirProducto)

        // Iniciar MenuActivity
        val intentMenu = Intent(this, MenuActivity::class.java)
        startActivity(intentMenu)
    }
}
