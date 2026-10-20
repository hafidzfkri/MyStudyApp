package com.example.mystudyapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

class ProfilManager(context: Context) {

    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences(NAMA_PREFS, Context.MODE_PRIVATE)

    companion object {
        private const val NAMA_PREFS = "profil_pengguna"
        const val KEY_NAMA = "profile_name"
        const val KEY_FOTO = "profile_photo"      // menyimpan path file foto
        private const val NAMA_FILE_FOTO = "foto_profil.jpg"
        private const val SISI_MAKS = 1024        // foto diperkecil agar hemat memori
    }

    // ================= NAMA =================
    fun ambilNama(): String = prefs.getString(KEY_NAMA, "") ?: ""
    fun simpanNama(nama: String) = prefs.edit().putString(KEY_NAMA, nama).apply()

    // ================= FOTO =================
    fun ambilFoto(): Bitmap? {
        val path = prefs.getString(KEY_FOTO, null) ?: return null
        val file = File(path)
        return if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
    }

    /** Menyalin foto pilihan pengguna ke penyimpanan internal aplikasi.
     *  URI dari galeri tidak dijamin bisa dibuka lagi setelah aplikasi ditutup,
     *  jadi yang disimpan adalah SALINAN-nya. Panggil dari thread latar. */
    fun simpanFotoDariUri(uri: Uri): Boolean {
        return try {
            val cr = appContext.contentResolver

            // 1) ukur dulu, lalu tentukan skala pengecilan
            val ukur = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            cr.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, ukur) }
            var skala = 1
            while (ukur.outWidth / skala > SISI_MAKS || ukur.outHeight / skala > SISI_MAKS) skala *= 2

            // 2) baca gambar yang sudah diperkecil
            val opsi = BitmapFactory.Options().apply { inSampleSize = skala }
            val asli = cr.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opsi) }
                ?: return false

            // 3) putar sesuai data EXIF (foto kamera sering "miring")
            val derajat = cr.openInputStream(uri)?.use {
                when (ExifInterface(it).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
                )) {
                    ExifInterface.ORIENTATION_ROTATE_90  -> 90f
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                    else -> 0f
                }
            } ?: 0f
            val hasil = if (derajat != 0f) {
                Bitmap.createBitmap(
                    asli, 0, 0, asli.width, asli.height,
                    Matrix().apply { postRotate(derajat) }, true
                )
            } else asli

            // 4) tulis ke filesDir, simpan path-nya
            val file = File(appContext.filesDir, NAMA_FILE_FOTO)
            FileOutputStream(file).use { hasil.compress(Bitmap.CompressFormat.JPEG, 90, it) }
            prefs.edit().putString(KEY_FOTO, file.absolutePath).apply()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun hapusFoto() {
        prefs.getString(KEY_FOTO, null)?.let { File(it).delete() }
        prefs.edit().remove(KEY_FOTO).apply()
    }
}