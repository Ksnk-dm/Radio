package com.ksnk.radio.ui.listFragment.adapter

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.ksnk.radio.R

class WaveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var imageViewWave: ImageView? = itemView.findViewById(R.id.imageViewWave)
    var nameTextView: TextView? = itemView.findViewById(R.id.textViewName)
    var frequencyTextView: TextView? = itemView.findViewById(R.id.textViewFrequency)
    var lottieAnimationView:LottieAnimationView? =itemView.findViewById(R.id.favAnimationView)
    var menuImageButton:ImageButton?=itemView.findViewById(R.id.menuImageButton)
}