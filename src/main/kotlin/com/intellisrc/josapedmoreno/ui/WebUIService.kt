package com.intellisrc.josapedmoreno.ui

import com.google.gson.Gson
import com.intellisrc.core.Log
import com.intellisrc.josapedmoreno.appointment.Appointment
import com.intellisrc.josapedmoreno.auth.AuthService
import com.intellisrc.josapedmoreno.data.AppointmentModel
import com.intellisrc.web.Service
import com.intellisrc.web.ServiciableMultiple

class WebUIService : ServiciableMultiple {

    override fun getPath(): String {
        return "/system"
    }

    override fun getAllowOrigin(): String {
        return ""
    }

    override fun getServices(): MutableList<Service> {
        val services: MutableList<Service> = ArrayList()
        services.add(getAllAppointments())
        services.add(getAppointmentsWithOffset())
        services.add(updateRecord())
        services.add(getRecord())
        services.add(deleteRecord())
        return services
    }

    companion object {
        private const val MAX_RECORD = 1000
        private val appointment = Appointment()

        fun getAllAppointments(): Service {
            var ok = false
            val service = Service()
            service.method = Service.Method.GET
            service.path = "/appts"
            service.allow = AuthService.allowAdmin()
            service.action = Service.Action { _, response ->
                val apptList = arrayListOf<Map<String, Any>>()
                try {
                    val list = appointment.getAll(0, 0)
                    if (list.isEmpty()) {
                        response.status(204)
                        Log.w("Records were not found")
                    } else {
                        list.forEach {
                            apptList.add(it.toMap())
                        }
                        ok = true
                    }
                } catch (e: Exception) {
                    Log.w("Cannot proceed with the request/query")
                }
                mapOf("ok" to ok, "data" to apptList)
            }
            return service
        }

        fun getAppointmentsWithOffset(): Service {
            var ok = false
            val service = Service()
            service.method = Service.Method.GET
            service.path = "/appts/:offset/:qty"
            service.allow = AuthService.allowAdmin()
            service.action = Service.Action { request, response ->
                val apptList = arrayListOf<Map<String, Any>>()
                val offset: Int
                var qty: Int
                try {
                    offset = Integer.parseInt(request.params("offset"))
                    qty = Integer.parseInt(request.params("qty"))
                    if (offset >= 0 && qty > 0) {
                        // limit the amount of records to download at once
                        if (qty > MAX_RECORD)
                            qty = MAX_RECORD
                        Log.i("Requesting list: [%d:%d] from %s", offset, qty, request.ip())
                        val list = Appointment().getAll(offset, qty)
                        if (list.isEmpty()) {
                            response.status(204)
                            Log.w("Records were not found")
                        } else {
                            list.forEach {
                                apptList.add(it.toMap())
                            }
                            ok = true
                        }
                    } else {
                        response.status(400)
                        Log.w("Requested list parameters were mistaken")
                    }
                } catch (e: NumberFormatException) {
                    Log.w("Requested list parameters were mistaken")
                }
                mapOf("ok" to ok, "data" to apptList)
            }
            return service
        }

        fun updateRecord(): Service {
            val service = Service()
            service.method = Service.Method.POST
            service.path = "/update/:id"
            service.allow = AuthService.allowAdmin()
            service.action = Service.Action { request, response ->
                var ok = false
                var id = 0
                var err = ""
                val gson = Gson()
                try {
                    id = Integer.parseInt(request.params("id"))
                    val body = request.body().trim()
                    if (body.isNotEmpty()) {
                        if (id == 0) {
                            Log.i("The last ID is %d", appointment.getLastID())
                            id = appointment.getLastID() + 1
                            val isOk = appointment.createRecord(gson.fromJson(body, AppointmentModel::class.java))
                            return@Action mapOf("ok" to isOk)
                        } else
                            appointment.updateRecord(id)
                        val data = gson.fromJson(body, AppointmentModel::class.java).toMap()
                        if (data.isNotEmpty()) {
                            Log.i("Update record requested by %s", request.ip())
                            data.entries.forEach {
                                when (it.key) {
                                    "firstName", "lastName" -> ok = appointment.updateName(data["firstName"].toString(), data["lastName"].toString())
                                    "email" -> ok = appointment.updateEmail(data["email"].toString())
                                    "contactNumber" -> ok = appointment.updateContactNumber(data["contactNumber"].toString())
                                    "apptType" -> ok = appointment.updateApptType(data["apptType"].toString())
                                    "apptDate" -> ok = appointment.updateApptDate(data["apptDate"].toString())
                                    "pid" -> { Log.i("We don't need %s key here", it.key) }
                                    else -> Log.w("Unidentified key: %s", it.key)
                                }
                            }
                        } else {
                            response.status(400)
                            err = "data was empty or invalid. Please check the request."
                        }
                    } else {
                        response.status(400)
                        err = "body was empty"
                    }
                } catch (e: Appointment.IllegalApptException) {
                    response.status(400)
                    Log.e("Invalid ID %d passed to update.", id)
                }
                if (err.isNotEmpty()) {
                    Log.w("err: %e", err)
                }
                mapOf("ok" to ok, "err" to err)
            }
            return service
        }

        fun getRecord(): Service {
            val service = Service()
            service.method = Service.Method.GET
            service.path = "/get_record/:id"
            service.allow = AuthService.allowAdmin()
            service.action = Service.Action { request, response ->
                var ok = false
                var id = 0
                val gson = Gson()
                var record = ""
                try {
                    id = Integer.parseInt(request.params("id"))
                    record = gson.toJson(appointment.getRecord(id))
                    ok = if (record.isNotEmpty()) {
                        true
                    } else {
                        response.status(204)
                        false
                    }
                } catch (e: Appointment.IllegalApptException) {
                    response.status(400)
                    Log.e("Invalid ID %d passed to get record.", id)
                }
                mapOf("ok" to ok, "data" to record)
            }
            return service
        }

        fun deleteRecord(): Service {
            val service = Service()
            service.method = Service.Method.DELETE
            service.path = "/delete/:id"
            service.allow = AuthService.allowAdmin()
            service.action = Service.Action { request, response ->
                var ok = false
                var id = 0
                try {
                    id = Integer.parseInt(request.params("id"))
                    ok = appointment.deleteRecord(id)
                } catch (e: Appointment.IllegalApptException) {
                    response.status(400)
                    Log.e("Invalid ID %d passed to get record.", id)
                }
                mapOf("ok" to ok)
            }
            return service
        }
    }
}