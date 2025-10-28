package com.example

data class Bahan (
    var nama: String,
    var kategori: String
){
    override fun toString(): String {
        return "$nama ($kategori)"
    }
}