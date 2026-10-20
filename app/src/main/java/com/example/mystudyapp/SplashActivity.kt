package com.example.mystudyapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    private val keHalamanUtama = Runnable {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        animasiPudar()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // pengaturan sudah diterapkan di MyStudyApp; di sini cukup dibaca untuk warna aksen
        val settings = SettingsManager(this)
        findViewById<TextView>(R.id.tvSplashNama)
            .setTextColor(getColor(settings.warnaAksenRes()))

        handler.postDelayed(keHalamanUtama, 1200)   // tampil 1,2 detik
    }

    override fun onDestroy() {
        handler.removeCallbacks(keHalamanUtama)     // cegah pindah halaman setelah ditutup
        super.onDestroy()
    }

    private fun animasiPudar() {
        if (Build.VERSION.SDK_INT >= 34) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN, android.R.anim.fade_in, android.R.anim.fade_out
            )
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}