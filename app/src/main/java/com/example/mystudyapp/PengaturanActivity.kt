package com.example.mystudyapp

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat

class PengaturanActivity : AppCompatActivity() {

    private lateinit var settings: SettingsManager

    private lateinit var scrollPengaturan: ScrollView
    private lateinit var tvRingkasan: TextView
    private lateinit var tvStatusNotifikasi: TextView
    private lateinit var btnReset: Button


    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_pengaturan)

        // =====================================================
        // SETTINGS
        // =====================================================

        settings = SettingsManager(this)


        // =====================================================
        // VIEW
        // =====================================================

        scrollPengaturan = findViewById(R.id.scrollPengaturan)

        tvRingkasan = findViewById(R.id.tvRingkasan)

        tvStatusNotifikasi =
            findViewById(R.id.tvStatusNotifikasi)

        btnReset =
            findViewById(R.id.btnReset)


        // =====================================================
        // RADIO GROUP
        // =====================================================

        val rgBahasa =
            findViewById<RadioGroup>(R.id.rgBahasa)

        val rgTema =
            findViewById<RadioGroup>(R.id.rgTema)

        val rgUkuran =
            findViewById<RadioGroup>(R.id.rgUkuran)

        val rgWarna =
            findViewById<RadioGroup>(R.id.rgWarna)


        // =====================================================
        // NOTIFIKASI
        // =====================================================

        val swNotifikasi =
            findViewById<SwitchCompat>(R.id.swNotifikasi)


        // =====================================================
        // KEADAAN AWAL
        // =====================================================

        isiKeadaanAwal(
            rgBahasa,
            rgTema,
            rgUkuran,
            rgWarna,
            swNotifikasi
        )


        // =====================================================
        // BAHASA
        // =====================================================

        rgBahasa.setOnCheckedChangeListener { _, id ->

            val bahasa =
                if (id == R.id.rbEnglish) {
                    SettingsManager.BAHASA_EN
                } else {
                    SettingsManager.BAHASA_ID
                }

            settings.simpanBahasa(bahasa)

            SettingsManager.terapkanKeAplikasi(this)

            recreate()
        }


        // =====================================================
        // TEMA
        // =====================================================

        rgTema.setOnCheckedChangeListener { _, id ->

            val tema =
                if (id == R.id.rbGelap) {
                    SettingsManager.TEMA_GELAP
                } else {
                    SettingsManager.TEMA_TERANG
                }

            settings.simpanTema(tema)

            SettingsManager.terapkanKeAplikasi(this)

            recreate()
        }


        // =====================================================
        // UKURAN TEKS
        // =====================================================

        rgUkuran.setOnCheckedChangeListener { _, id ->

            val ukuran = when (id) {

                R.id.rbKecil ->
                    SettingsManager.UKURAN_KECIL

                R.id.rbBesar ->
                    SettingsManager.UKURAN_BESAR

                else ->
                    SettingsManager.UKURAN_SEDANG
            }

            settings.simpanUkuran(ukuran)

            perbaruiRingkasan()
        }


        // =====================================================
        // WARNA AKSEN
        // =====================================================

        rgWarna.setOnCheckedChangeListener { _, id ->

            val warna = when (id) {

                R.id.rbHijau ->
                    SettingsManager.WARNA_AKSEN_HIJAU

                R.id.rbUngu ->
                    SettingsManager.WARNA_AKSEN_UNGU

                else ->
                    SettingsManager.WARNA_AKSEN_BIRU
            }

            // Simpan warna
            settings.simpanWarnaAksen(warna)

            // Terapkan warna langsung
            terapkanWarnaAksen()

            // Perbarui ringkasan
            perbaruiRingkasan()
        }


        // =====================================================
        // NOTIFIKASI
        // =====================================================

        swNotifikasi.setOnCheckedChangeListener { _, aktif ->

            settings.simpanNotifikasi(aktif)

            perbaruiRingkasan()
        }


        // =====================================================
        // RESET
        // =====================================================

        btnReset.setOnClickListener {
            konfirmasiReset()
        }


        // =====================================================
        // TERAPKAN WARNA AWAL
        // =====================================================

        terapkanWarnaAksen()
    }


    // =========================================================
    // ON RESUME
    // =========================================================

    override fun onResume() {
        super.onResume()

        perbaruiRingkasan()

        terapkanWarnaAksen()
    }


    // =========================================================
    // KEADAAN AWAL
    // =========================================================

    private fun isiKeadaanAwal(
        rgBahasa: RadioGroup,
        rgTema: RadioGroup,
        rgUkuran: RadioGroup,
        rgWarna: RadioGroup,
        swNotifikasi: SwitchCompat
    ) {

        // =====================================================
        // BAHASA
        // =====================================================

        rgBahasa.check(

            if (
                settings.ambilBahasa()
                == SettingsManager.BAHASA_EN
            ) {
                R.id.rbEnglish
            } else {
                R.id.rbIndonesia
            }
        )


        // =====================================================
        // TEMA
        // =====================================================

        rgTema.check(

            if (
                settings.ambilTema()
                == SettingsManager.TEMA_GELAP
            ) {
                R.id.rbGelap
            } else {
                R.id.rbTerang
            }
        )


        // =====================================================
        // UKURAN
        // =====================================================

        rgUkuran.check(

            when (settings.ambilUkuran()) {

                SettingsManager.UKURAN_KECIL ->
                    R.id.rbKecil

                SettingsManager.UKURAN_BESAR ->
                    R.id.rbBesar

                else ->
                    R.id.rbSedang
            }
        )


        // =====================================================
        // WARNA AKSEN
        // =====================================================

        rgWarna.check(

            when (settings.ambilWarnaAksen()) {

                SettingsManager.WARNA_AKSEN_HIJAU ->
                    R.id.rbHijau

                SettingsManager.WARNA_AKSEN_UNGU ->
                    R.id.rbUngu

                else ->
                    R.id.rbBiru
            }
        )


        // =====================================================
        // NOTIFIKASI
        // =====================================================

        swNotifikasi.isChecked =
            settings.ambilNotifikasi()


        // =====================================================
        // RINGKASAN
        // =====================================================

        perbaruiRingkasan()
    }


    // =========================================================
    // TERAPKAN WARNA AKSEN
    // =========================================================

    private fun terapkanWarnaAksen() {

        // -----------------------------------------------------
        // Ambil warna aksen
        // -----------------------------------------------------

        val aksen = ContextCompat.getColor(
            this,
            settings.warnaAksenRes()
        )


        // -----------------------------------------------------
        // Ambil warna latar aksen
        // -----------------------------------------------------

        val latarAksen = ContextCompat.getColor(
            this,
            settings.warnaAksenLatarRes()
        )


        // -----------------------------------------------------
        // Background halaman
        // -----------------------------------------------------

        scrollPengaturan.setBackgroundColor(
            latarAksen
        )


        // -----------------------------------------------------
        // Tombol reset
        // -----------------------------------------------------

        btnReset.backgroundTintList =
            ColorStateList.valueOf(aksen)

        btnReset.setTextColor(
            ContextCompat.getColor(
                this,
                R.color.warna_teks_di_aksen
            )
        )


        // -----------------------------------------------------
        // Ringkasan
        // -----------------------------------------------------

        tvRingkasan.setTextColor(aksen)


        // -----------------------------------------------------
        // Status notifikasi
        // -----------------------------------------------------

        tvStatusNotifikasi.setTextColor(aksen)
    }


    // =========================================================
    // PERBARUI RINGKASAN
    // =========================================================

    private fun perbaruiRingkasan() {

        tvRingkasan.text =
            RingkasanPengaturan.teks(
                this,
                settings
            )

        tvStatusNotifikasi.text =
            RingkasanPengaturan.statusNotifikasi(
                this,
                settings
            )
    }


    // =========================================================
    // KONFIRMASI RESET
    // =========================================================

    private fun konfirmasiReset() {

        AlertDialog.Builder(this)

            .setTitle(
                R.string.dialog_reset_judul
            )

            .setMessage(
                R.string.dialog_reset_pesan
            )

            .setNegativeButton(
                R.string.dialog_batal,
                null
            )

            .setPositiveButton(
                R.string.dialog_ya
            ) { _, _ ->

                // -------------------------------------------------
                // Hapus semua pengaturan
                // -------------------------------------------------

                settings.reset()


                // -------------------------------------------------
                // Terapkan pengaturan default
                // -------------------------------------------------

                SettingsManager.terapkanKeAplikasi(
                    this
                )


                // -------------------------------------------------
                // Pesan
                // -------------------------------------------------

                Toast.makeText(
                    this,
                    getString(R.string.toast_reset),
                    Toast.LENGTH_SHORT
                ).show()


                // -------------------------------------------------
                // Refresh
                // -------------------------------------------------

                recreate()
            }

            .show()
    }
}