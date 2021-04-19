package com.intellisrc.josapedmoreno.appointment

import com.intellisrc.core.Config
import com.intellisrc.core.Log
import com.intellisrc.core.SysInfo
import com.intellisrc.db.DB
import com.intellisrc.db.Data
import com.intellisrc.db.Database
import com.intellisrc.josapedmoreno.data.AppointmentModel
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*
import kotlin.collections.ArrayList

class Appointment {
    var id = 0

    fun getAll(offset: Int, qty: Int): List<AppointmentModel> {
        val db: DB = Database.connect()
        val rows = db.table("appointments").limit(qty, offset).get()
        db.close()
        val data = rows.toListMap()
        data.forEach {
            Log.i(it.entries.toString())
        }
        return fromData(rows)
    }

    private fun cleanInputMap(input: Map<Any?, Any?>?): Map<Any?, Any?> {
        val cleanMap = HashMap<Any?, Any?>()
        var ok = true
        for (inKey in input?.keys!!) {
            when (inKey) {
                "id" -> cleanMap[inKey as String] = input[inKey].toString().toInt()
                "first_name", "last_name" -> {
                    val name = input[inKey].toString().trim { it <= ' ' }
                        .replace("[^a-z A-Z]".toRegex(), "")
                    if (name.isNotEmpty()) {
                        cleanMap[inKey as String] = name
                    } else {
                        Log.w("%s value was invalid: %s", inKey, input[inKey].toString())
                        ok = false
                    }
                }
                "email" -> try {
                    val email = input[inKey].toString().replace("\\..*".toRegex(), "")
                    if (email != "") {
                        cleanMap[inKey as String] = email
                    } else {
                        ok = false
                    }
                } catch (ignored: NumberFormatException) {
                    Log.w("Age value was invalid: %s", input[inKey].toString())
                    ok = false
                }
                "contact_number" -> {
                    val contactNumber = input[inKey].toString()
                    if (contactNumber.isNotEmpty())
                        cleanMap[inKey as String] = contactNumber
                    else {
                        Log.w("%s value was invalid: %s", inKey, input[inKey].toString())
                        ok = false
                    }
                }
                "appointment_type" -> {
                    val appointmentType = input[inKey].toString()
                    if (appointmentType.isNotEmpty())
                        cleanMap[inKey as String] = appointmentType
                    else {
                        Log.w("%s value was invalid: %s", inKey, input[inKey].toString())
                        ok = false
                    }
                }
                "appointment_date" -> {
                    val appointmentDate = input[inKey].toString()
                    if (appointmentDate.isNotEmpty())
                        cleanMap[inKey as String] = appointmentDate
                    else {
                        Log.w("%s value was invalid: %s", inKey, input[inKey].toString())
                        ok = false
                    }
                }
                else -> {
                    Log.w("Unidentified key: %s", inKey)
                    ok = false
                }
            }
        }
        if (!ok) {
            Log.w("Input map contained incorrect data")
            throw IllegalApptException()
        }
        return cleanMap
    }

    private fun fromMap(map: Map<Any?, Any?>?): AppointmentModel {
        val appt = AppointmentModel()
        val apptMap: Map<Any?, Any?> = cleanInputMap(map)
        appt.firstName = apptMap["first_name"].toString()
        appt.lastName = apptMap["last_name"].toString()
        appt.email = apptMap["email"].toString()
        appt.contactNumber = apptMap["contact_number"].toString()
        appt.apptType = apptMap["appointment_type"].toString()
        appt.apptDate = apptMap["appointment_date"].toString()
        return appt
    }

    private fun fromData(rows: Data): List<AppointmentModel> {
        val list: MutableList<AppointmentModel> = ArrayList()
        val data = rows.toListMap()
        for (datum in data) {
            try {
                list.add(fromMap(datum))
            } catch (ex: IllegalApptException) {
                Log.w("")
            }
        }
        return Collections.unmodifiableList(list)
    }

    class IllegalApptException : Exception()

    companion object {
        fun initDB() {
            val dbFile = SysInfo.getFile(Config.get("db.name", "main") + ".db")
            if (!dbFile.exists()) {
                val db: DB = Database.connect()
                val createSql: File = SysInfo.getFile("create.sql")
                if (createSql.exists()) {
                    try {
                        val query = Files.readString(createSql.toPath())
                        if (query.isNotEmpty()) {
                            query.replace("\n", "").split(";").forEach {
                                if (it.trim().isNotEmpty())
                                    db.set(it)
                            }
                        }
                    } catch (e: IOException) {
                        Log.w("File doesn't exist or unable to open %s", e)
                    }
                }
                db.close()
            }
        }
    }
}