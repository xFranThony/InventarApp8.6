package com.sky.inventary_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.sky.inventary_app.Data.dbUsuarios

class RegistroActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        val etUsuario: EditText = findViewById(R.id.etUsuario)
        val etContrasena: EditText = findViewById(R.id.etContrasena)
        val etConfirmarContrasena: EditText = findViewById(R.id.etConfirmarContrasena)
        val btnGuardar: Button = findViewById(R.id.btnGuardar)

        btnGuardar.setOnClickListener {
            val nombreUsuario = etUsuario.text.toString().trim()
            val password = etContrasena.text.toString().trim()
            val confirmarPassword = etConfirmarContrasena.text.toString().trim()

            when {
                nombreUsuario.isEmpty() -> {
                    Toast.makeText(this, "El nombre de usuario es obligatorio", Toast.LENGTH_LONG).show()
                }
                password.isEmpty() -> {
                    Toast.makeText(this, "La contraseña es obligatoria", Toast.LENGTH_LONG).show()
                }
                confirmarPassword.isEmpty() -> {
                    Toast.makeText(this, "Confirmar la contraseña es obligatorio", Toast.LENGTH_LONG).show()
                }
                password.length < 8 -> {
                    Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_LONG).show()
                }
                password != confirmarPassword -> {
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show()
                }
                else -> {
                    db.collection("Usuarios").document(nombreUsuario).get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                Toast.makeText(this, "El nombre de usuario ya existe", Toast.LENGTH_LONG).show()
                            } else {
                                val usuario = dbUsuarios(
                                    nombreUsuario,
                                    password
                                )
                                db.collection("Usuarios").document(nombreUsuario).set(usuario)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "Usuario registrado: $nombreUsuario",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        val intent = Intent(this, LoginActivity::class.java)
                                        startActivity(intent)
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this,
                                            "Error al registrar el usuario: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        Log.e("Firebase", "Error al registrar el usuario", e)
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Error al verificar el usuario: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.e("Firebase", "Error al verificar el usuario", e)
                        }
                }
            }
        }
    }
}
