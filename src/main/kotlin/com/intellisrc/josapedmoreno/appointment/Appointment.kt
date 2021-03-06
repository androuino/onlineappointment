package com.intellisrc.josapedmoreno.appointment

import com.intellisrc.core.Config
import com.intellisrc.core.Log
import com.intellisrc.core.SysInfo
import com.intellisrc.db.DB
import com.intellisrc.db.Data
import com.intellisrc.db.Database
import com.intellisrc.db.Query
import com.intellisrc.josapedmoreno.data.AppointmentModel
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*
import kotlin.collections.ArrayList

class Appointment {
    private val table = "appointments"
    private var appointmentModel = AppointmentModel()

    fun getAll(offset: Int, qty: Int): List<AppointmentModel> {
        val db: DB = Database.connect()
        val rows = if (qty == 0)
            db.table(table).get()
        else
            db.table(table).limit(qty, offset).get()
        db.close()
        val data = rows.toListMap()
        data.forEach {
            Log.i(it.entries.toString())
        }
        return fromData(rows)
    }

    fun createRecord(appointmentModel: AppointmentModel): Boolean {
        val ok: Boolean
        val db: DB = Database.connect()
        ok = db.table(table).insert(mapOf(
            "lastName" to appointmentModel.lastName,
            "firstName" to appointmentModel.firstName,
            "email" to appointmentModel.email,
            "contactNumber" to appointmentModel.contactNumber,
            "apptType" to appointmentModel.apptType,
            "apptDate" to appointmentModel.apptDate)
        )
        db.close()
        if (ok)
            Log.i("Successfully added a new record.")
        return ok
    }

    fun updateRecord(id : Int) {
        if (id <= 0) {
            Log.w("Invalid ID %s", id)
            throw IllegalApptException()
        }
        val db: DB = Database.connect()
        val row = db.table(table).key("id").get(id)
        db.close()

        if (row.isEmpty || row == null) {
            Log.w("Record with id: %d doesn't exists!", id)
            throw IllegalApptException()
        }

        val appt = cleanInputMap(row.toMap())
        appointmentModel.pid           = appt["id"].toString().toInt()
        appointmentModel.lastName      = appt["lastName"].toString()
        appointmentModel.firstName     = appt["firstName"].toString()
        appointmentModel.email         = appt["email"].toString()
        appointmentModel.contactNumber = appt["contactNumber"].toString()
        appointmentModel.apptType      = appt["apptType"].toString()
        appointmentModel.apptDate      = appt["apptDate"].toString()
    }

    private fun isValid(): Boolean {
        return appointmentModel.pid > 0 || appointmentModel.firstName.isNotEmpty()
    }

    private fun cleanInputMap(input: Map<Any?, Any?>?): Map<Any?, Any?> {
        val cleanMap = HashMap<Any?, Any?>()
        var ok = true
        for (inKey in input?.keys!!) {
            when (inKey) {
                "id" -> cleanMap[inKey as String] = input[inKey].toString().toInt()
                "lastName", "firstName" -> {
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
                "contactNumber" -> {
                    val contactNumber = input[inKey].toString()
                    if (contactNumber.isNotEmpty())
                        cleanMap[inKey as String] = contactNumber
                    else {
                        Log.w("%s value was invalid: %s", inKey, input[inKey].toString())
                        ok = false
                    }
                }
                "apptType" -> {
                    val appointmentType = input[inKey].toString()
                    if (appointmentType.isNotEmpty())
                        cleanMap[inKey as String] = appointmentType
                    else {
                        Log.w("%s value was invalid: %s", inKey, input[inKey].toString())
                        ok = false
                    }
                }
                "apptDate" -> {
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
        appt.pid           = apptMap["id"].toString().toInt()
        appt.lastName      = apptMap["lastName"].toString()
        appt.firstName     = apptMap["firstName"].toString()
        appt.email         = apptMap["email"].toString()
        appt.contactNumber = apptMap["contactNumber"].toString()
        appt.apptType      = apptMap["apptType"].toString()
        appt.apptDate      = apptMap["apptDate"].toString()
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
        return Collections.unmodifiableList(list).sortedBy { it.lastName }
    }

    fun updateName(newFirstName : String, newLastName: String): Boolean {
        var ok = false
        if (isValid() && appointmentModel.firstName.isNotEmpty() && appointmentModel.lastName.isNotEmpty()) {
            val db: DB = Database.connect()
            ok = db.table(table).key("id").update(mapOf("firstName" to newFirstName, "lastName" to newLastName), appointmentModel.pid)
            if (ok) {
                appointmentModel.lastName = newLastName
                appointmentModel.firstName = newFirstName
            }
            db.close()
            Log.i("Record with id: %d updated name to: %s %s", appointmentModel.pid, newFirstName, newLastName)
        } else {
            Log.w("updateName(): Not valid.")
        }
        return ok
    }

    fun updateEmail(newEmail: String): Boolean {
        var ok = false
        if (isValid()) {
            val db: DB = Database.connect()
            ok = db.table(table).key("id").update(mapOf("email" to newEmail), appointmentModel.pid)
            if (ok)
                appointmentModel.email = newEmail
            db.close()
            Log.i("Record with id: %d updated Email to: %s", appointmentModel.pid, newEmail)
        } else {
            Log.w("updateEmail(): Not valid.")
        }
        return ok
    }

    fun updateContactNumber(newContactNumber: String): Boolean {
        var ok = false
        if (isValid()) {
            val db: DB = Database.connect()
            ok = db.table(table).key("id").update(mapOf("contactNumber" to newContactNumber), appointmentModel.pid)
            if (ok)
                appointmentModel.contactNumber = newContactNumber
            db.close()
            Log.i("Record with id: %d updated Contact Number to: %s", appointmentModel.pid, newContactNumber)
        } else {
            Log.w("updateContactNumber(): Not valid.")
        }
        return ok
    }

    fun updateApptType(newApptType: String): Boolean {
        var ok = false
        if (isValid()) {
            val db: DB = Database.connect()
            ok = db.table(table).key("id").update(mapOf("apptType" to newApptType), appointmentModel.pid)
            if (ok)
                appointmentModel.apptType = newApptType
            db.close()
            Log.i("Record with id: %d updated Appt. Type to: %s", appointmentModel.pid, newApptType)
        } else {
            Log.w("updateApptType(): Not valid.")
        }
        return ok
    }

    fun updateApptDate(newApptDate: String): Boolean {
        var ok = false
        if (isValid()) {
            val db: DB = Database.connect()
            ok = db.table(table).key("id").update(mapOf("apptDate" to newApptDate), appointmentModel.pid)
            if (ok)
                appointmentModel.apptDate = newApptDate
            db.close()
            Log.i("Record with id: %d updated Appt. Date to: %s", appointmentModel.pid, newApptDate)
        } else {
            Log.w("updateApptDate(): Not valid.")
        }
        return ok
    }

    fun getLastID(): Int {
        val db: DB = Database.connect()
        db.table(table).openIfClosed()
        val last = db.table(table).order("id", Query.SortOrder.DESC).get()
        db.close()
        return last.toInt()
    }

    fun getRecord(id: Int): AppointmentModel {
        val db: DB = Database.connect()
        val row = db.table(table).key("id").get(id)
        db.close()
        if (row.isEmpty || row == null) {
            Log.w("Record with id: %d doesn't exists!", id)
            throw IllegalApptException()
        }
        val appt = cleanInputMap(row.toMap())
        appointmentModel.pid           = appt["id"].toString().toInt()
        appointmentModel.lastName      = appt["lastName"].toString()
        appointmentModel.firstName     = appt["firstName"].toString()
        appointmentModel.email         = appt["email"].toString()
        appointmentModel.contactNumber = appt["contactNumber"].toString()
        appointmentModel.apptType      = appt["apptType"].toString()
        appointmentModel.apptDate      = appt["apptDate"].toString()
        return appointmentModel
    }

    fun deleteRecord(id: Int): Boolean {
        val ok: Boolean
        val db: DB = Database.connect()
        ok = db.table(table).key("id").delete(id)
        if (ok)
            Log.i("Successfully deleted a record.")
        db.close()
        return ok
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