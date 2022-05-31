package com.ksnk.radio.ui.historyFragment.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ksnk.radio.R
import com.ksnk.radio.data.entity.Track


class HistoryFragmentRecyclerViewAdapter(
    var items: List<Track>,
    var context: Context?,
) : RecyclerView.Adapter<HistoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val layoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return HistoryViewHolder(
            layoutInflater.inflate(
                R.layout.track_items_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.trackNameTextView.text = items[position].name
        holder.stationTextView.text = items[position].station
        holder.dateTextView.text = items[position].date
        holder.itemView.setOnClickListener { openYouTube(items[position].name.toString()) }
    }

    private fun openYouTube(search: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.youtube.com/results?search_query=$search")
        )
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context?.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}