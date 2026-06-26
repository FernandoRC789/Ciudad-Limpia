package com.nickrodriguez.ciudadlimpia.adapter.reporte

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nickrodriguez.ciudadlimpia.R

class FotoAdapter(
    private val fotos: MutableList<Uri>,
    private val onEliminar: (Int) -> Unit
) : RecyclerView.Adapter<FotoAdapter.FotoViewHolder>() {

    inner class FotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgFoto: ImageView = itemView.findViewById(R.id.imgFoto)
        val btnEliminar: ImageView = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_foto, parent, false)
        return FotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: FotoViewHolder, position: Int) {
        Glide.with(holder.imgFoto.context)
            .load(fotos[position])
            .centerCrop()
            .into(holder.imgFoto)

        holder.btnEliminar.setOnClickListener {
            onEliminar(position)
        }
    }

    override fun getItemCount() = fotos.size
}