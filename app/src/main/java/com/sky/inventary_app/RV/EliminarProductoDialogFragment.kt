package com.sky.inventary_app.RV

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sky.inventary_app.R
import dbProductos
import java.io.File

class EliminarProductoDialogFragment : DialogFragment() {

    private lateinit var producto: dbProductos
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_eliminar_producto, container, false)

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        val btnConfirmarEliminar: Button = view.findViewById(R.id.btnConfirmarEliminar)
        val btnCancelarEliminar: Button = view.findViewById(R.id.btnCancelarEliminar)

        btnConfirmarEliminar.setOnClickListener {
            db.collection("Productos")
                .whereEqualTo("idProducto", producto.idProducto)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val documentId = document.id
                        val imageUrl = document.getString("imageUrl")

                        // Eliminar el documento de Firestore
                        db.collection("Productos").document(documentId).delete()
                            .addOnSuccessListener {
                                // Eliminar la imagen del Storage si existe
                                imageUrl?.let {
                                    val imageRef = storage.getReferenceFromUrl(it)
                                    imageRef.delete()
                                        .addOnSuccessListener {
                                            // Eliminar la imagen del directorio local
                                            deleteLocalImage(producto.nombreImg)
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(requireContext(), "Error al eliminar la imagen del Storage: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                } ?: run {
                                    // Si no hay imagen en Storage, eliminar la imagen local directamente
                                    deleteLocalImage(producto.nombreImg)
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Error al eliminar el producto: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error al buscar el producto: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        btnCancelarEliminar.setOnClickListener {
            dismiss()
        }

        return view
    }

    private fun deleteLocalImage(nombreImg: String) {
        val storageDir = File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "fotosProductos")
        val imageFile = File(storageDir, nombreImg)
        if (imageFile.exists()) {
            if (imageFile.delete()) {
                //Toast.makeText(requireContext(), "Imagen local eliminada", Toast.LENGTH_LONG).show()
            } else {
                //Toast.makeText(requireContext(), "Error al eliminar la imagen local", Toast.LENGTH_LONG).show()
            }
        } else {
            //Toast.makeText(requireContext(), "La imagen local no existe", Toast.LENGTH_LONG).show()
        }
        restartActivity()
    }

    companion object {
        @JvmStatic
        fun newInstance(producto: dbProductos) = EliminarProductoDialogFragment().apply {
            this.producto = producto
        }
    }

    private fun restartActivity() {
        val intent = Intent(requireActivity(), MostrarProductosActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}
