package com.phonebook.sync.contactdirectory.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.phonebook.sync.contactdirectory.ui.activities.MainActivity

class CallEndReceiver : BroadcastReceiver() {

    companion object {
        private var lastState: String? = null
        private var isOutgoing = false
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            when (intent.action) {

                Intent.ACTION_NEW_OUTGOING_CALL -> {
                    isOutgoing = true
                    Log.d("CallEndReceiver", "Outgoing call started")
                }

                TelephonyManager.ACTION_PHONE_STATE_CHANGED -> {
                    val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

                    if (state == TelephonyManager.EXTRA_STATE_IDLE &&
                        lastState == TelephonyManager.EXTRA_STATE_OFFHOOK
                    ) {
                        Log.d("CallEndReceiver", "Call ended (outgoing=$isOutgoing)")
                        showBottomSheet(context)
                        isOutgoing = false
                    }

                    lastState = state
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showBottomSheet(context: Context) {
        val i = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("show_bottom_sheet", true)
        }
        context.startActivity(i)
    }
}
