package com.sky.inventary_app.Venta

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.sky.inventary_app.Data.dbVenta
import com.sky.inventary_app.R
import java.text.SimpleDateFormat
import java.util.Locale

class VentasAdapter(private val ventasList: List<dbVenta>, private val activity: FragmentActivity) :
    RecyclerView.Adapter<VentasAdapter.VentasViewHolder>() {

    private lateinit var selectedVenta: dbVenta

    class VentasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIdVenta: TextView = itemView.findViewById(R.id.tvIdVenta)
        val tvTotalVenta: TextView = itemView.findViewById(R.id.tvTotalVenta)
        val tvFechaVenta: TextView = itemView.findViewById(R.id.tvFechaVenta)
        val tvIdVendedor: TextView = itemView.findViewById(R.id.tvIdVendedor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VentasViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_venta, parent, false)
        return VentasViewHolder(view)
    }

    override fun onBindViewHolder(holder: VentasViewHolder, position: Int) {
        val venta = ventasList[position]
        holder.tvIdVenta.text = "${venta.idVenta}"
        holder.tvTotalVenta.text = "S/.${String.format("%.2f", venta.totalVenta)}"
        holder.tvFechaVenta.text = "${SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(venta.fechaVenta)}"
        holder.tvIdVendedor.text = "${venta.idVendedor}"

        holder.itemView.setOnClickListener {
            selectedVenta = venta
            notifyItemClick()
        }
    }

    private fun notifyItemClick() {
        val dialog = VentaDetallesDialogFragment2.newInstance(
            selectedVenta.idVenta,
            String.format("%.2f", selectedVenta.totalVenta),
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedVenta.fechaVenta),
            selectedVenta.nombreComprador,
            selectedVenta.idVendedor,
            selectedVenta.productos.joinToString(separator = "\n") { it.toString() }
        )
        dialog.show(activity.supportFragmentManager, "VentaDetallesDialogFragment2")
    }

    override fun getItemCount(): Int {
        return ventasList.size
    }
}
