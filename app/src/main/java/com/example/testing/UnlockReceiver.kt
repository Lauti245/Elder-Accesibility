package com.example.testing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class UnlockReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_USER_PRESENT) {
            Log.d("UnlockReceiver", "Dispositivo desbloqueado, abriendo MainActivity...")

            // Abrir MainActivity directamente
            val activityIntent = Intent(context, MainActivity::class.java)
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Necesario para abrir desde un BroadcastReceiver
            context.startActivity(activityIntent)
        }
    }
}