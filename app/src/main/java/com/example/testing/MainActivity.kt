package com.example.testing

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var unlockReceiver: UnlockReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnCall1 = findViewById<Button>(R.id.btnCall1)
        val btnCall2 = findViewById<Button>(R.id.btnCall2)
        val btnCall3 = findViewById<Button>(R.id.btnCall3)

        btnCall1.setOnClickListener { openWhatsApp("34675044175") }
        btnCall2.setOnClickListener { openWhatsApp("34652169980") }
        btnCall3.setOnClickListener { openWhatsApp("34675044175") }

        // Registrar el UnlockReceiver dinámicamente
        unlockReceiver = UnlockReceiver()
        val filter = IntentFilter(Intent.ACTION_USER_PRESENT)
        registerReceiver(unlockReceiver, filter)
    }
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
     

    }

    override fun onDestroy() {
        super.onDestroy()
        // Desregistrar el UnlockReceiver cuando la Activity se destruya
        unregisterReceiver(unlockReceiver)
    }

    @SuppressLint("NewApi")
    private fun openWhatsApp(phoneNumber: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("https://wa.me/$phoneNumber")
            `package` = "com.whatsapp"
            putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://com.whatsapp"))
        }

        try {
            startActivity(sendIntent)
            Log.d("AutoClickService", "Servicio entra")

            // Iniciar el ForegroundService
            val serviceIntent = Intent(this, ForegroundService::class.java)
            startForegroundService(serviceIntent)
            Log.d("AutoClickService", "Servicio sale")
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "WhatsApp no está instalado o ocurrió un error", Toast.LENGTH_LONG).show()
        }
    }
}