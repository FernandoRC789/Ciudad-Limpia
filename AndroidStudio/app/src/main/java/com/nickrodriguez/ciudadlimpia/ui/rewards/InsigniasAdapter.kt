package com.nickrodriguez.ciudadlimpia.ui.rewards

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nickrodriguez.ciudadlimpia.R
import com.nickrodriguez.ciudadlimpia.model.Insignia

class InsigniasAdapter(
    private var items: List<Insignia>
) : RecyclerView.Adapter<InsigniasAdapter.InsigniaViewHolder>() {

    // Paleta de fondos circulares que se repite si hay más insignias que colores
    private val backgrounds = intArrayOf(
        R.drawable.bg_circle_orange,
        R.drawable.bg_circle_green,
        R.drawable.bg_circle_blue
    )
    private val icons = intArrayOf(
        R.drawable.ic_trophy,
        R.drawable.ic_broom,
        R.drawable.ic_shield_check
    )

    inner class InsigniaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bgCircle: View = view.findViewById(R.id.viewInsigniaBg)
        val icon: android.widget.ImageView = view.findViewById(R.id.imgInsigniaIcon)
        val nombre: android.widget.TextView = view.findViewById(R.id.tvInsigniaNombre)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): InsigniaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_insignia, parent, false)
        return InsigniaViewHolder(view)
    }

    override fun onBindViewHolder(holder: InsigniaViewHolder, position: Int) {
        val insignia = items[position]
        holder.nombre.text = insignia.nombre
        holder.bgCircle.setBackgroundResource(backgrounds[position % backgrounds.size])
        holder.icon.setImageResource(icons[position % icons.size])
        holder.itemView.alpha = if (insignia.obtenida) 1.0f else 0.4f
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<Insignia>) {
        items = newItems
        notifyDataSetChanged()
    }
}
