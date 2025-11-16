package com.example

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class CartAdapter(private val listBahan: MutableList<Bahan>) :
    RecyclerView.Adapter<CartAdapter.ListViewHolder>() {

    private lateinit var onCheckClickCallback: OnCheckClickCallback


    interface OnCheckClickCallback {
        fun onCheckClicked(data: Bahan, position: Int)
    }
    fun setOnCheckClickCallback(onCheckClickCallback: OnCheckClickCallback) {
        this.onCheckClickCallback = onCheckClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val bahan = listBahan[position]
        holder.tvNama.text = bahan.nama
        holder.tvKategori.text = bahan.kategori
        Picasso.get()
            .load(bahan.gambar)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher_round)
            .into(holder.ivGambar)

        holder.ivCheck.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                onCheckClickCallback.onCheckClicked(listBahan[currentPosition], currentPosition)
            }
        }
    }

    override fun getItemCount(): Int = listBahan.size

    class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivGambar: ImageView = view.findViewById(R.id.ivGambarBahan)
        val tvNama: TextView = view.findViewById(R.id.tvNamaBahan)
        val tvKategori: TextView = view.findViewById(R.id.tvKategoriBahan)
        val ivCheck: ImageView = view.findViewById(R.id.ivMarkAsBought)
    }
}