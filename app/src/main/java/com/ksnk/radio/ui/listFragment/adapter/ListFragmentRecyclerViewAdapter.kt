package com.ksnk.radio.ui.listFragment.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.ksnk.radio.*
import com.ksnk.radio.data.entity.RadioWave
import com.ksnk.radio.listeners.MenuItemIdListener
import com.ksnk.radio.services.PlayerService
import com.squareup.picasso.Picasso

class ListFragmentRecyclerViewAdapter(
    private var items: List<RadioWave>,
    var context: Context?,
    var mPlayer: ExoPlayer,
    var mService: PlayerService,
    var menuItemIdListener: MenuItemIdListener
) :
    RecyclerView.Adapter<WaveViewHolder>() {

    fun setItems(items: List<RadioWave>) {
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaveViewHolder {
        val layoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return WaveViewHolder(layoutInflater.inflate(R.layout.wave_items_list, parent, false))
    }


    override fun onBindViewHolder(holder: WaveViewHolder, position: Int) {
        holder.frequencyTextView?.text = items[position].fmFrequency
        holder.nameTextView?.text = items[position].name
        checkImageNull(position, holder)
        checkFavItem(position, holder)
        checkCustomItem(position, holder)
        holder.menuImageButton?.setOnClickListener { menuItemIdListener.getItemMenu(items[position].id) }
        holder.itemView.setOnClickListener {
            setMediaItem(position)
            menuItemIdListener.updateCountOpenItem(items[position].id)
        }
        radioWaveNameEquals(position, holder)
    }

    private fun checkCustomItem(position: Int, holder: WaveViewHolder) {
        if (items[position].custom == false) {
            holder.menuImageButton?.visibility = View.GONE
        } else {
            holder.menuImageButton?.visibility = View.VISIBLE
        }
    }

    private fun checkFavItem(position: Int, holder: WaveViewHolder) {
        if (items[position].favorite == true) {
            holder.favImageView?.visibility = View.VISIBLE
        } else {
            holder.favImageView?.visibility = View.INVISIBLE
        }
    }

    private fun checkImageNull(position: Int, holder: WaveViewHolder) {
        if (TextUtils.isEmpty(items[position].image)) {
            holder.imageViewWave?.setImageResource(R.mipmap.ic_launcher_round);
        } else {
            Picasso.get()
                .load(items[position].image)
                .into(holder.imageViewWave)
        }
    }

    private fun radioWaveNameEquals(position: Int, holder: WaveViewHolder) {
        if (mService.getRadioWave()?.name?.toString().equals(items[position].name)) {
            holder.lottieAnimationView?.visibility = View.VISIBLE
        } else {
            holder.lottieAnimationView?.visibility = View.INVISIBLE
        }
    }

    private fun setMediaItem(position: Int) {
        val mediaItem: MediaItem = MediaItem.fromUri(items[position].url.toString())
        mService.getPlayer()!!.setMediaItem(mediaItem)
        mService.getPlayer()!!.prepare()
        mService.getPlayer()!!.play()
        mService.setRadioWave(items[position])
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }
}