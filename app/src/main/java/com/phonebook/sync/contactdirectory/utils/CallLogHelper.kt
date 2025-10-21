package com.phonebook.sync.contactdirectory.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.CallLog
import java.util.Date

data class CallEntry(
    val name: String?,
    val number: String?,
    val type: String,
    val date: String,
    val duration: String
)

object CallLogHelper {

    @SuppressLint("Range", "SimpleDateFormat")
    fun getCallLogs(context: Context, filterType: Int? = null): List<CallEntry> {
        val list = mutableListOf<CallEntry>()

        val projection = arrayOf(
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION
        )

        val selection = if (filterType != null) "${CallLog.Calls.TYPE} = ?" else null
        val selectionArgs = if (filterType != null) arrayOf(filterType.toString()) else null

        val cursor = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            CallLog.Calls.DATE + " DESC"
        )

        cursor?.use {
            while (it.moveToNext()) {
                val name = it.getString(it.getColumnIndex(CallLog.Calls.CACHED_NAME))
                val number = it.getString(it.getColumnIndex(CallLog.Calls.NUMBER))
                val type = when (it.getInt(it.getColumnIndex(CallLog.Calls.TYPE))) {
                    CallLog.Calls.INCOMING_TYPE -> "Incoming"
                    CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                    CallLog.Calls.MISSED_TYPE -> "Missed"
                    CallLog.Calls.REJECTED_TYPE -> "Rejected"
                    CallLog.Calls.VOICEMAIL_TYPE -> "Voicemail"
                    else -> "Other"
                }

                val date = Date(it.getLong(it.getColumnIndex(CallLog.Calls.DATE))).toString()
                val duration = it.getString(it.getColumnIndex(CallLog.Calls.DURATION)) + " sec"

                list.add(CallEntry(name ?: "Unknown", number, type, date, duration))
            }
        }

        return list
    }
}