package com.example.testing

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Path
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import java.util.LinkedList

class AutoClickService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.example.testing.PERFORM_AUTO_CLICK") {
                performAutoClick()
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()
        // Registrar el BroadcastReceiver
        val filter = IntentFilter("com.example.testing.PERFORM_AUTO_CLICK")
        registerReceiver(broadcastReceiver, filter)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // No necesitamos manejar eventos de accesibilidad en este caso
    }

    override fun onInterrupt() {
        Log.d("AutoClickService", "Servicio interrumpido")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("AutoClickService", "Servicio conectado")

        // Configura el servicio para detectar eventos de accesibilidad
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK
            notificationTimeout = 100
        }
        this.serviceInfo = info
    }

    // Método para realizar el auto-click
    private fun performAutoClick() {
        Log.d("AutoClickService", "Ejecutando performAutoClick")

        // Simula el clic en el ícono de llamada después de 3 segundos
        handler.postDelayed({
            findAndClickCallButton()
        }, 5000) // Espera 3 segundos antes de ejecutar la acción
    }

    private fun findAndClickCallButton() {
        // Obtén la raíz del árbol de accesibilidad
        val rootNode = rootInActiveWindow
        if (rootNode == null) {
            Log.d("AutoClickService", "No se pudo obtener el nodo raíz")
            return
        }

        // Busca el ícono de llamada
        val callButtonNode = findCallButton(rootNode)

        if (callButtonNode != null) {
            // Simula el clic en el ícono de llamada
            callButtonNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            Log.d("AutoClickService", "Clic simulado en el ícono de llamada")
        } else {
            Log.d("AutoClickService", "No se encontró el ícono de llamada")
        }
    }

    private fun findCallButton(rootNode: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        val queue = LinkedList<AccessibilityNodeInfo>()
        queue.add(rootNode)

        while (queue.isNotEmpty()) {
            val currentNode = queue.poll()

            // Log para depuración: muestra información del nodo actual
            Log.d("NodeInfo", "Clase: ${currentNode.className}, Descripción: ${currentNode.contentDescription}, Texto: ${currentNode.text}")

            // Verifica si el nodo actual es el ícono de llamada
            if (isCallButton(currentNode)) {
                return currentNode
            }

            // Agrega los hijos del nodo actual a la cola
            for (i in 0 until currentNode.childCount) {
                val childNode = currentNode.getChild(i)
                if (childNode != null) {
                    queue.add(childNode)
                }
            }
        }

        return null
    }

    private fun isCallButton(node: AccessibilityNodeInfo): Boolean {
        // Verifica si el nodo es un ImageButton y tiene la descripción "Voice call"
        return node.className == "android.widget.ImageButton" &&
                node.contentDescription?.toString() == "Voice call"
    }

    override fun onDestroy() {
        super.onDestroy()
        // Desregistrar el BroadcastReceiver
        unregisterReceiver(broadcastReceiver)
        // Detén cualquier tarea pendiente del Handler
        handler.removeCallbacksAndMessages(null)
        Log.d("AutoClickService", "Servicio destruido")
    }
}