package com.nickrodriguez.ciudadlimpia.ui.rewards

import com.nickrodriguez.ciudadlimpia.R
import com.nickrodriguez.ciudadlimpia.model.RankingItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class RankingAdapter(
    private var items: List<RankingItem>
) : RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {

    inner class RankingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root: View = view
        val posicion: TextView = view.findViewById(R.id.tvRankingPosicion)
        val avatarBg: View = view.findViewById(R.id.viewAvatarBg)
        val avatarImg: ImageView = view.findViewById(R.id.imgRankingAvatar)
        val iniciales: TextView = view.findViewById(R.id.tvRankingIniciales)
        val nombre: TextView = view.findViewById(R.id.tvRankingNombre)
        val titulo: TextView = view.findViewById(R.id.tvRankingTitulo)
        val puntos: TextView = view.findViewById(R.id.tvRankingPuntos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): RankingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ranking, parent, false)
        return RankingViewHolder(view)
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val item = items[position]
        val nf = NumberFormat.getNumberInstance(Locale("es", "PE"))

        holder.posicion.text = item.posicion.toString()
        holder.nombre.text = item.nombreCompleto
        holder.titulo.text = item.nivel
        holder.puntos.text = "${nf.format(item.puntosTotal)} pts"

        if (item.posicion == 1) {
            holder.root.setBackgroundResource(R.drawable.bg_ranking_top1)
        } else {
            holder.root.setBackgroundColor(
                ContextCompat.getColor(holder.root.context, android.R.color.transparent)
            )
        }

        // ❌ avatarUrl ya no existe en tu API
        holder.avatarImg.visibility = View.GONE
        holder.iniciales.visibility = View.VISIBLE
        holder.iniciales.text = obtenerIniciales(item.nombreCompleto)
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<RankingItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    private fun obtenerIniciales(nombreCompleto: String): String {
        val partes = nombreCompleto.trim().split(" ").filter { it.isNotBlank() }
        return when {
            partes.isEmpty() -> "?"
            partes.size == 1 -> partes[0].take(2).uppercase()
            else -> (partes[0].take(1) + partes[1].take(1)).uppercase()
        }
    }
}
