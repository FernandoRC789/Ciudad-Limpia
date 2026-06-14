package com.nickrodriguez.ciudadlimpia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.nickrodriguez.ciudadlimpia.R
import com.nickrodriguez.ciudadlimpia.model.OnboardingItem

class OnboardingAdapter(
    private val onboardingItems: List<OnboardingItem>
) : RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    inner class OnboardingViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val imageView: ImageView =
            view.findViewById(R.id.imgOnboarding)

        val lottieView: LottieAnimationView =
            view.findViewById(R.id.lottieOnboarding)
        val title: TextView = view.findViewById(R.id.txtTitle)
        val description: TextView = view.findViewById(R.id.txtDescription)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OnboardingViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_onboarding,
                parent,
                false
            )

        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: OnboardingViewHolder,
        position: Int
    ) {

        val item = onboardingItems[position]

        if(item.lottieFile != null){

            holder.lottieView.visibility = View.VISIBLE
            holder.imageView.visibility = View.GONE

            holder.lottieView.setAnimation(
                item.lottieFile
            )

            holder.lottieView.playAnimation()

        }else{

            holder.imageView.visibility = View.VISIBLE
            holder.lottieView.visibility = View.GONE

            holder.imageView.setImageResource(
                item.imageRes!!
            )
        }
        holder.title.text = item.title
        holder.description.text = item.description

        holder.title.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                item.titleColorRes
            )
        )

        holder.description.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                item.descriptionColorRes
            )
        )
    }

    override fun getItemCount(): Int {
        return onboardingItems.size
    }
}


