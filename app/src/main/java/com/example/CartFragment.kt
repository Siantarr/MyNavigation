package com.example

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartFragment : Fragment() {

    private lateinit var rvCart: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: CartAdapter
    private var cartList = mutableListOf<Bahan>()

    // SharedPreferences
    private lateinit var sp: SharedPreferences
    private val gson = Gson()
    private val SP_CART_KEY = "dt_cart"
    private val SP_BOUGHT_KEY = "dt_bought"
    private val SP_NAME = "ResepSP"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        rvCart = view.findViewById(R.id.rvCart)
        tvEmpty = view.findViewById(R.id.tvEmptyCart)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sp = requireActivity().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    }

    override fun onResume() {
        super.onResume()
        loadCartData()
        setupRecyclerView()
        checkEmptyView()
    }

    private fun loadCartData() {
        val json = sp.getString(SP_CART_KEY, null)
        cartList.clear()
        if (json != null) {
            val type = object : TypeToken<MutableList<Bahan>>() {}.type
            cartList.addAll(gson.fromJson(json, type))
        }
    }

    private fun setupRecyclerView() {
        adapter = CartAdapter(cartList)
        rvCart.layoutManager = LinearLayoutManager(requireContext())
        rvCart.adapter = adapter


        adapter.setOnCheckClickCallback(object : CartAdapter.OnCheckClickCallback {
            override fun onCheckClicked(data: Bahan, position: Int) {
                markAsBought(data, position)
            }
        })
    }

    private fun markAsBought(bahan: Bahan, position: Int) {

        val boughtJson = sp.getString(SP_BOUGHT_KEY, null)
        val type = object : TypeToken<MutableList<Bahan>>() {}.type
        val boughtList: MutableList<Bahan> = if (boughtJson != null) {
            gson.fromJson(boughtJson, type)
        } else {
            mutableListOf()
        }

        if (!boughtList.any { it.nama == bahan.nama }) {
            boughtList.add(bahan)
            val newBoughtJson = gson.toJson(boughtList)
            sp.edit().putString(SP_BOUGHT_KEY, newBoughtJson).apply()
        }

        cartList.removeAt(position)
        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position, cartList.size)


        val newCartJson = gson.toJson(cartList)
        sp.edit().putString(SP_CART_KEY, newCartJson).apply()

        Toast.makeText(requireContext(), "${bahan.nama} ditandai sudah dibeli", Toast.LENGTH_SHORT).show()
        checkEmptyView()
    }

    private fun checkEmptyView() {
        if (cartList.isEmpty()) {
            rvCart.visibility = View.GONE
            tvEmpty.visibility = View.VISIBLE
        } else {
            rvCart.visibility = View.VISIBLE
            tvEmpty.visibility = View.GONE
        }
    }
}