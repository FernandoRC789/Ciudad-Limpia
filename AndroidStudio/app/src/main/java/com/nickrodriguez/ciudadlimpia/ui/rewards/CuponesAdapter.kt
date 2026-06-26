package com.nickrodriguez.ciudadlimpia.ui.rewards


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nickrodriguez.ciudadlimpia.R
import com.nickrodriguez.ciudadlimpia.model.Cupon

import java.text.NumberFormat
import java.util.Locale

class CuponesAdapter(
    private var items: List<Cupon>,
    private val puntosDisponibles: () -> Int,
    private val onCuponClick: (Cupon) -> Unit
) : RecyclerView.Adapter<CuponesAdapter.CuponViewHolder>() {

    // Iconos y fondos rotativos para diferenciar visualmente cada tipo de cupón.
    // Si tu Cupon trae un campo "tipo" o "categoria", aquí se puede mapear de forma
    // más precisa en lugar de rotar por posición.
    private val backgrounds = intArrayOf(R.drawable.bg_square_lilac, R.drawable.bg_square_mint, R.drawable.bg_card_white)
    private val icons = intArrayOf(R.drawable.ic_percent, R.drawable.ic_movie, R.drawable.ic_gift_bag)

    inner class CuponViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconBg: View = view.findViewById(R.id.viewCuponIconBg)
        val icon: ImageView = view.findViewById(R.id.imgCuponIcon)
        val nombre: TextView = view.findViewById(R.id.tvCuponNombre)
        val descripcion: TextView = view.findViewById(R.id.tvCuponDescripcion)
        val costo: TextView = view.findViewById(R.id.tvCuponCosto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): CuponViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cupon, parent, false)
        return CuponViewHolder(view)
    }

    override fun onBindViewHolder(holder: CuponViewHolder, position: Int) {
        val cupon = items[position]
        val nf = NumberFormat.getNumberInstance(Locale("es", "PE"))

        holder.nombre.text = cupon.nombre
        holder.descripcion.text = cupon.descripcion
        holder.costo.text = "${nf.format(cupon.costoPuntos)} pts"

        holder.iconBg.setBackgroundResource(backgrounds[position % backgrounds.size])
        holder.icon.setImageResource(icons[position % icons.size])

        // Si no alcanzan los puntos o no hay stock, se atenúa la card para dar feedback visual.
        val puedeCanjear = cupon.activo && cupon.stock > 0 && puntosDisponibles() >= cupon.costoPuntos
        holder.itemView.alpha = if (puedeCanjear) 1.0f else 0.45f
        holder.itemView.isEnabled = puedeCanjear
        holder.itemView.setOnClickListener {
            if (cupon.stock <= 0) return@setOnClickListener
            onCuponClick(cupon)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Cupon>) {
        items = newItems
        notifyDataSetChanged()
    }
}
