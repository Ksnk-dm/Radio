package com.ksnk.radio.ui.historyFragment.adapter

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ksnk.radio.R

class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var trackNameTextView: TextView = itemView.findViewById(R.id.textViewTrackName)
    var stationTextView: TextView = itemView.findViewById(R.id.stationHistoryTextView)
    var dateTextView: TextView = itemView.findViewById(R.id.historyDateTextView)
    var openYouTubeImageButton: ImageButton = itemView.findViewById(R.id.openYouTubeImageButton)
    var trackImageView: ImageView = itemView.findViewById(R.id.trackImageView)
}