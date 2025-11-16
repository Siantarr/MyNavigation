package com.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.content.Context
import android.content.SharedPreferences
import android.view.GestureDetector
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.databinding.FragmentBahanBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BahanFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BahanFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentBahanBinding? = null
    private val binding get() = _binding!!

    var data = mutableListOf<Bahan>()

    private lateinit var sp: SharedPreferences
    private var gson = Gson()
    private var SP_BAHAN_KEY = "dt_bahan"
    private var SP_NAME = "ResepSP"

    private fun loadDataBahan(){
        val json = sp.getString(SP_BAHAN_KEY, null)
        if (json != null){
            val type = object : TypeToken<MutableList<Bahan>>() {}.type
            data.clear()
            data.addAll(gson.fromJson(json, type))
        }
    }

    private fun simpanDataBahan(){
        val json = gson.toJson(data)
        sp.edit().putString(SP_BAHAN_KEY, json).apply()
    }

    private fun siapkanData(){
        val nama = resources.getStringArray(R.array.bahan_nama)
        val kategori = resources.getStringArray(R.array.bahan_kategori)
        val gambar = resources.getStringArray(R.array.bahan_gambar)

        data.clear()
        for(i in nama.indices){
            data.add(Bahan(nama[i], kategori[i], gambar[i]))
        }
    }

    private fun showAddDialog(adapter: ArrayAdapter<Bahan>) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Tambah Bahan Baru")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)

        val etNama = EditText(requireContext())
        etNama.hint = "Masukkan Nama Bahan"
        layout.addView(etNama)

        val etGambar = EditText(requireContext())
        etGambar.hint = "Masukkan Gambar Bahan"
        layout.addView(etGambar)


        val etKategori = EditText(requireContext())
        etKategori.hint = "Masukkan Kategori"
        layout.addView(etKategori)

        builder.setView(layout)

        builder.setPositiveButton("Simpan") { dialog, _ ->
            val nama = etNama.text.toString().trim()
            val kategori = etKategori.text.toString().trim()
            val gambar = etGambar.text.toString().trim()

            if (nama.isNotEmpty() && kategori.isNotEmpty() && gambar.isNotEmpty()) {
                data.add(Bahan(nama, kategori, gambar))
                adapter.notifyDataSetChanged()
                simpanDataBahan()
                Toast.makeText(
                    requireContext(),
                    "Bahan '$nama' ditambahkan",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Nama dan Kategori tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentBahanBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sp = requireActivity().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        loadDataBahan()
        if (data.isEmpty()){
            siapkanData()
            simpanDataBahan()
        }

        val lvAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            data

        )

        val lvBahan = view.findViewById<ListView>(R.id.lvBahan)
        lvBahan.adapter = lvAdapter

        lvBahan.setOnItemClickListener {
            parent, view, position, id ->
            Toast.makeText(requireContext(),
                data[position].toString(),
                Toast.LENGTH_SHORT
            ).show()

        }

        val btnTambah = view.findViewById<Button>(R.id.btnTambah)
        btnTambah.setOnClickListener {
            showAddDialog(lvAdapter)
        }

        val gestureDetector = GestureDetector(
            requireContext(),
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    val position = lvBahan.pointToPosition(
                        e.x.toInt(),
                        e.y.toInt()
                    )
                    if (position != ListView.INVALID_POSITION) {
                        val selectedItem = data[position]
                        showActionDialog(position, selectedItem, data, lvAdapter)
                    }
                    return true
                }
            }
        )
        lvBahan.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }
    }
    private fun showActionDialog(
        position: Int,
        selectedItem: Bahan,
        data: MutableList<Bahan>,
        adapter: ArrayAdapter<Bahan>
    ) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("ITEM ${selectedItem.nama}")
        builder.setMessage("Pilih tindakan yang ingin dilakukan")

        builder.setPositiveButton("Update Kategori") { _, _ ->

            showUpdateCategoryDialog(position, selectedItem, data, adapter)
        }
        builder.setNegativeButton("Hapus") { _, _ ->
            data.removeAt(position)
            adapter.notifyDataSetChanged()
            simpanDataBahan()
            Toast.makeText(
                requireContext(),
                "Hapus Item ${selectedItem.nama}",
                Toast.LENGTH_SHORT
            ).show()
        }
        builder.setNeutralButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun showUpdateCategoryDialog(
        position: Int,
        oldValue: Bahan,
        data: MutableList<Bahan>,
        adapter: ArrayAdapter<Bahan>
    ) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Update Data Bahan")

        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 40, 50, 10)


        val tvOld = TextView(requireContext())
        tvOld.text = "Nama Bahan: ${oldValue.nama}"
        tvOld.textSize = 16f


        val etNew = EditText(requireContext())
        etNew.hint = "Masukkan Kategori Baru"
        etNew.setText(oldValue.kategori)

        val etGambar = EditText(requireContext())
        etGambar.hint = "Masukkan Gambar Bahan"
        etGambar.setText(oldValue.gambar)


        layout.addView(tvOld)
        layout.addView(etNew)
        layout.addView(etGambar)


        builder.setView(layout)

        builder.setPositiveButton("Simpan") { dialog, _ ->
            val newValue = etNew.text.toString().trim()
            val newGambar = etGambar.text.toString().trim()

            if (newValue.isNotEmpty() && newGambar.isNotEmpty()) {
                data[position].kategori = newValue
                data[position].gambar = newGambar
                adapter.notifyDataSetChanged()
                simpanDataBahan()
                Toast.makeText(
                    requireContext(),
                    "Data Update",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Kategori dan Gambar tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BahanFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BahanFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}