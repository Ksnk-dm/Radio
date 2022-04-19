package com.ksnk.radio

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class MainRecyclerViewAdapter(private var items: List<RadioWave>, var context: Context, var sharedPreferences: SharedPreferences) :
    RecyclerView.Adapter<WaveViewHolder>(), NameInterface {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaveViewHolder {
        val layoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return WaveViewHolder(layoutInflater.inflate(R.layout.wave_items, parent, false))
    }

    override fun onBindViewHolder(holder: WaveViewHolder, position: Int) {
        var name = sharedPreferences.getString("name","")
        if(name.equals(items[position].name)){
            holder.lottieAnimationView?.visibility=View.VISIBLE
        } else{
            holder.lottieAnimationView?.visibility=View.GONE
        }
        holder.frequencyTextView?.text = items[position].fmFrequency
        holder.nameTextView?.text = items[position].name
        Picasso.get()
            .load(items[position].image)
            .into(holder.imageViewWave)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("items", items[position])
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun getInterface():NameInterface{
        return this
    }

    override fun setName(name: String) {
        TODO("Not yet implemented")
    }
}