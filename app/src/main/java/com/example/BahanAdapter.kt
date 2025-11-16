package com.example

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
class BahanAdapter(private val listBahan: List<Bahan>) :
    RecyclerView.Adapter<BahanAdapter.ListViewHolder>() {

        private lateinit var onItemClickCallback: OnItemClickCallback
        private lateinit var onCartClickCallback: OnCartClickCallback

        interface OnItemClickCallback {
            fun onItemClicked(data: Bahan, position: Int)
        }
        interface OnCartClickCallback {
            fun onCartClicked(data: Bahan)
        }
        fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
            this.onItemClickCallback = onItemClickCallback
        }
        fun setOnCartClickCallback(onCartClickCallback: OnCartClickCallback) {
            this.onCartClickCallback = onCartClickCallback
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.item_bahan, parent, false)
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


            holder.itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(listBahan[holder.adapterPosition], holder.adapterPosition)
            }

            holder.ivCart.setOnClickListener {
                onCartClickCallback.onCartClicked(listBahan[holder.adapterPosition])
            }
        }

        override fun getItemCount(): Int = listBahan.size

        class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val ivGambar: ImageView = view.findViewById(R.id.ivGambarBahan)
            val tvNama: TextView = view.findViewById(R.id.tvNamaBahan)
            val tvKategori: TextView = view.findViewById(R.id.tvKategoriBahan)
            val ivCart: ImageView = view.findViewById(R.id.ivAddToCart)
        }
}