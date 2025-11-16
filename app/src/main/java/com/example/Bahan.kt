package com.example
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Bahan (
    var nama: String,
    var kategori: String,
    var gambar: String
): Parcelable{
    override fun toString(): String {
        return "$nama ($kategori)"
    }
}