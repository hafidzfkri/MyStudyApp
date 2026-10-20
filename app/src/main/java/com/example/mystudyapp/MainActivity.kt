
package com.example.mystudyapp

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var settings: SettingsManager
    private lateinit var profil: ProfilManager

    private lateinit var tvNamaApp: TextView
    private lateinit var tvSubJudul: TextView
    private lateinit var tvMateriIsi: TextView
    private lateinit var tvJadwalIsi: TextView
    private lateinit var tvTugasIsi: TextView
    private lateinit var tvRingkasan: TextView
    private lateinit var btnPengaturan: Button

    // Ikon materi, jadwal, dan tugas
    private lateinit var ikonMateri: TextView
    private lateinit var ikonJadwal: TextView
    private lateinit var ikonTugas: TextView

    // Bagian profil
    private lateinit var tvSalam: TextView
    private lateinit var frameFotoMain: FrameLayout
    private lateinit var ivFotoMain: ImageView
    private lateinit var tvPlaceholderMain: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        settings = SettingsManager(this)
        profil = ProfilManager(this)

        // Menghubungkan komponen dari activity_main.xml
        tvNamaApp = findViewById(R.id.tvNamaApp)
        tvSubJudul = findViewById(R.id.tvSubJudul)
        tvMateriIsi = findViewById(R.id.tvMateriIsi)
        tvJadwalIsi = findViewById(R.id.tvJadwalIsi)
        tvTugasIsi = findViewById(R.id.tvTugasIsi)
        tvRingkasan = findViewById(R.id.tvRingkasan)
        btnPengaturan = findViewById(R.id.btnPengaturan)

        ikonMateri = findViewById(R.id.ikonMateri)
        ikonJadwal = findViewById(R.id.ikonJadwal)
        ikonTugas = findViewById(R.id.ikonTugas)

        tvSalam = findViewById(R.id.tvSalam)
        frameFotoMain = findViewById(R.id.frameFotoMain)
        ivFotoMain = findViewById(R.id.ivFotoMain)
        tvPlaceholderMain = findViewById(R.id.tvPlaceholderMain)

        // Membuka halaman pengaturan
        btnPengaturan.setOnClickListener {
            startActivity(
                Intent(this, PengaturanActivity::class.java)
            )
        }

        // Membuka halaman profil saat kartu profil ditekan
        findViewById<View>(R.id.kartuProfil).setOnClickListener {
            startActivity(
                Intent(this, ProfilActivity::class.java)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        terapkanPengaturanTampilan()
        tampilkanProfil()
    }

    private fun terapkanPengaturanTampilan() {

        // 1. Mengatur ukuran teks
        val ukuran = settings.ukuranTeksSp()

        listOf(
            tvSubJudul,
            tvMateriIsi,
            tvJadwalIsi,
            tvTugasIsi
        ).forEach { teks ->
            teks.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                ukuran
            )
        }

        // 2. Mengatur warna aksen
        val aksen = ContextCompat.getColor(
            this,
            settings.warnaAksenRes()
        )

        val latarAksen = ContextCompat.getColor(
            this,
            settings.warnaAksenLatarRes()
        )

        tvNamaApp.setTextColor(aksen)

        btnPengaturan.backgroundTintList =
            ColorStateList.valueOf(aksen)

        btnPengaturan.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.warna_teks_di_aksen
            )
        )

        // Mengubah latar ikon dan bingkai foto profil
        listOf(
            ikonMateri,
            ikonJadwal,
            ikonTugas,
            frameFotoMain
        ).forEach { komponen ->
            komponen.backgroundTintList =
                ColorStateList.valueOf(latarAksen)
        }

        // 3. Menampilkan ringkasan pengaturan
        tvRingkasan.text =
            RingkasanPengaturan.teks(this, settings)
    }

    private fun tampilkanProfil() {

        // Mengambil nama profil
        val nama = profil.ambilNama()

        tvSalam.text = if (nama.isBlank()) {
            getString(R.string.salam_tamu)
        } else {
            getString(R.string.salam_format, nama)
        }

        // Mengambil foto profil
        val foto = profil.ambilFoto()

        if (foto != null) {
            ivFotoMain.setImageBitmap(foto)
            ivFotoMain.visibility = View.VISIBLE
            tvPlaceholderMain.visibility = View.GONE
        } else {
            ivFotoMain.setImageDrawable(null)
            ivFotoMain.visibility = View.GONE
            tvPlaceholderMain.visibility = View.VISIBLE
        }
    }
}
