package com.phonebook.sync.contactdirectory.services

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.phonebook.sync.contactdirectory.R
import com.phonebook.sync.contactdirectory.ui.activities.MainActivity

class CallStateWatcherService : Service() {

    private lateinit var telephonyManager: TelephonyManager
    private lateinit var phoneStateListener: PhoneStateListener

    override fun onCreate() {
        super.onCreate()
        startForegroundNotification()
        setupPhoneListener()
    }

    private fun setupPhoneListener() {
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        phoneStateListener = object : PhoneStateListener() {
            private var wasRinging = false
            private var wasOffHook = false

            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                when (state) {
                    TelephonyManager.CALL_STATE_RINGING -> {
                        wasRinging = true
                        wasOffHook = false
                    }

                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        wasOffHook = true
                    }

                    TelephonyManager.CALL_STATE_IDLE -> {
                        if (wasRinging || wasOffHook) {
                            Log.d("CallWatcher", "Call ended, showing bottom sheet")
                            showBottomSheet()
                        }
                        wasRinging = false
                        wasOffHook = false
                    }
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("CallWatcher", "Permission READ_PHONE_STATE not granted")
            return
        }

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    private fun startForegroundNotification() {
        val channelId = "call_watcher_service_channel"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Call Watcher Service",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }

        val notif = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Monitoring calls")
            .setContentText("Waiting for call to end…")
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .setOngoing(true)
            .build()

        startForeground(9876, notif)
    }

    private fun showBottomSheet() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (isAppInForeground(this)) {
                launchBottomSheetDirect()
            } else {
                showNotificationForBottomSheet()
            }
        } else {
            launchBottomSheetDirect()
        }
    }

    private fun launchBottomSheetDirect() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("show_bottom_sheet", true)
        }
        startActivity(intent)
        stopSelf()
    }

    private fun showNotificationForBottomSheet() {
        val channelId = "call_response_channel"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Call Response",
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val openIntent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("show_bottom_sheet", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Call ended")
            .setContentText("Tap to respond")
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(2001, notification)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = context.packageName
        for (appProcess in appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                && appProcess.processName == packageName
            ) {
                return true
            }
        }
        return false
    }
}