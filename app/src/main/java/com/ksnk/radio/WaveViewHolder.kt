package com.ksnk.radio

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WaveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var imageViewWave: ImageView? = itemView.findViewById(R.id.imageViewWave)
    var nameTextView: TextView? = itemView.findViewById(R.id.textViewName)
    var frequencyTextView: TextView? = itemView.findViewById(R.id.textViewFrequency)
}