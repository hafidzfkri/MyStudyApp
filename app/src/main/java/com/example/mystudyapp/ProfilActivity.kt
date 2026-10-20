package com.example.mystudyapp

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread

class ProfilActivity : AppCompatActivity() {

    private lateinit var profil: ProfilManager
    private lateinit var etNama: EditText
    private lateinit var ivFoto: ImageView
    private lateinit var tvPlaceholder: TextView
    private lateinit var btnHapusFoto: Button

    // pemilih foto bawaan Android — tidak perlu izin penyimpanan
    private val pilihFoto = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) simpanFoto(uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        profil = ProfilManager(this)

        etNama        = findViewById(R.id.etNama)
        ivFoto        = findViewById(R.id.ivFoto)
        tvPlaceholder = findViewById(R.id.tvPlaceholder)
        btnHapusFoto  = findViewById(R.id.btnHapusFoto)

        if (savedInstanceState == null) etNama.setText(profil.ambilNama())
        tampilkanFoto()

        findViewById<Button>(R.id.btnPilihFoto).setOnClickListener {
            pilihFoto.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        btnHapusFoto.setOnClickListener {
            profil.hapusFoto()
            tampilkanFoto()
        }

        findViewById<Button>(R.id.btnSimpanProfil).setOnClickListener {
            profil.simpanNama(etNama.text.toString().trim())
            Toast.makeText(this, R.string.toast_profil_tersimpan, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun simpanFoto(uri: Uri) {
        // menyalin & memperkecil foto cukup berat, jadi dikerjakan di thread latar
        thread {
            val berhasil = profil.simpanFotoDariUri(uri)
            runOnUiThread {
                if (isDestroyed) return@runOnUiThread
                if (berhasil) tampilkanFoto()
                else Toast.makeText(this, R.string.toast_foto_gagal, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun tampilkanFoto() {
        val foto = profil.ambilFoto()
        if (foto != null) {
            ivFoto.setImageBitmap(foto)
            ivFoto.visibility = View.VISIBLE
            tvPlaceholder.visibility = View.GONE
            btnHapusFoto.visibility = View.VISIBLE
        } else {
            ivFoto.setImageDrawable(null)
            ivFoto.visibility = View.GONE
            tvPlaceholder.visibility = View.VISIBLE
            btnHapusFoto.visibility = View.GONE
        }
    }
}