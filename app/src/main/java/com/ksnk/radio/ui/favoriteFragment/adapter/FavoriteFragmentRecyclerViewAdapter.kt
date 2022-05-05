package com.ksnk.radio.ui.favoriteFragment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.ksnk.radio.R
import com.ksnk.radio.data.entity.RadioWave
import com.ksnk.radio.services.PlayerService
import com.squareup.picasso.Picasso

class FavoriteFragmentRecyclerViewAdapter(
    private var items: List<RadioWave>,
    var context: Context?,
    var mPlayer: ExoPlayer,
    var mService: PlayerService
) :
    RecyclerView.Adapter<FavoriteViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val layoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return FavoriteViewHolder(layoutInflater.inflate(R.layout.wave_items, parent, false))
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.frequencyTextView?.text = items[position].fmFrequency
        holder.nameTextView?.text = items[position].name
        Picasso.get()
            .load(items[position].image)
            .into(holder.imageViewWave)
        holder.itemView.setOnClickListener {
            val mediaItem: MediaItem = MediaItem.fromUri(items[position].url.toString())
            mPlayer.setMediaItem(mediaItem)
            mPlayer.play()
            mService?.setRadioWave(items[position])
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}