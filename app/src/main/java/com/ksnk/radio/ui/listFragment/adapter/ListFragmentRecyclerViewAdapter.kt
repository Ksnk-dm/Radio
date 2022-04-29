package com.ksnk.radio.ui.listFragment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.ksnk.radio.*
import com.ksnk.radio.data.entity.RadioWave
import com.ksnk.radio.services.PlayerService
import com.squareup.picasso.Picasso
import javax.inject.Inject

class ListFragmentRecyclerViewAdapter(
    private var items: List<RadioWave>,
    var context: Context?,
    var mPlayer: ExoPlayer,
    var mService:PlayerService
) :
    RecyclerView.Adapter<WaveViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaveViewHolder {
        val layoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return WaveViewHolder(layoutInflater.inflate(R.layout.wave_items, parent, false))
    }

    override fun onBindViewHolder(holder: WaveViewHolder, position: Int) {
        checkStatusAnim(holder, position)
        holder.frequencyTextView?.text = items[position].fmFrequency
        holder.nameTextView?.text = items[position].name
        Picasso.get()
            .load(items[position].image)
            .into(holder.imageViewWave)
        holder.itemView.setOnClickListener {
            startPlayerActivity(position)
            var mediaItem: MediaItem = MediaItem.fromUri(items[position].url.toString())
            mPlayer.setMediaItem(mediaItem)
            mPlayer.play()
            mService?.setRadioWave(items[position])
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    private fun checkStatusAnim(holder: WaveViewHolder, position: Int) {
//        val name = sharedPreferences.getString(context.getString(R.string.get_name_shared_prefs_variable), "")
//        if (name.equals(items[position].name)) {
//            holder.lottieAnimationView?.visibility = View.VISIBLE
//        } else {
//            holder.lottieAnimationView?.visibility = View.GONE
//        }
    }

    private fun startPlayerActivity(position: Int) {
//        val intent = Intent(context, PlayerActivity::class.java)
//        intent.putExtra(context.getString(R.string.get_serializable_extra), items[position])
//        context.startActivity(intent)
    }
}