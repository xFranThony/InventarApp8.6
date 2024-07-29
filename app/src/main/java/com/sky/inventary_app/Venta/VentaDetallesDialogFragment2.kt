package com.sky.inventary_app.Venta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.sky.inventary_app.R

class VentaDetallesDialogFragment2 : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_venta_detalles2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener referencias a las vistas
        val tvIdVenta: TextView = view.findViewById(R.id.tvIdVenta)
        val tvTotalVenta: TextView = view.findViewById(R.id.tvTotalVenta)
        val tvFechaVenta: TextView = view.findViewById(R.id.tvFechaVenta)
        val tvNombreComprador: TextView = view.findViewById(R.id.tvNombreComprador)
        val tvIdVendedor: TextView = view.findViewById(R.id.tvIdVendedor)
        val tvProductos: TextView = view.findViewById(R.id.tvProductos)
        val btnClose: Button = view.findViewById(R.id.btnClose)

        // Obtener datos pasados desde el adaptador
        val idVenta = arguments?.getString("idVenta") ?: ""
        val totalVenta = arguments?.getString("totalVenta") ?: ""
        val fechaVenta = arguments?.getString("fechaVenta") ?: ""
        val nombreComprador = arguments?.getString("nombreComprador") ?: ""
        val idVendedor = arguments?.getString("idVendedor") ?: ""
        val productos = arguments?.getString("productos") ?: ""

        // Configurar las vistas con los datos de la venta
        tvIdVenta.text = "ID Venta: $idVenta"
        tvTotalVenta.text = "Total: S/.$totalVenta"
        tvFechaVenta.text = "Fecha: $fechaVenta"
        tvNombreComprador.text = "Nombre del Comprador: $nombreComprador"
        tvIdVendedor.text = "ID Vendedor: $idVendedor"
        tvProductos.text = "Productos:\n$productos"

        // Configurar el botón de cerrar
        btnClose.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        fun newInstance(
            idVenta: String,
            totalVenta: String,
            fechaVenta: String,
            nombreComprador: String,
            idVendedor: String,
            productos: String
        ): VentaDetallesDialogFragment2 {
            val fragment = VentaDetallesDialogFragment2()
            val args = Bundle()
            args.putString("idVenta", idVenta)
            args.putString("totalVenta", totalVenta)
            args.putString("fechaVenta", fechaVenta)
            args.putString("nombreComprador", nombreComprador)
            args.putString("idVendedor", idVendedor)
            args.putString("productos", productos)
            fragment.arguments = args
            return fragment
        }
    }
}
