package com.intellisrc.josapedmoreno.ui

import com.google.gson.Gson
import com.intellisrc.core.Log
import com.intellisrc.josapedmoreno.appointment.Appointment
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
        services.add(buttonActivity())
        services.add(getAppointments())
        services.add(updateRecord())
        return services
    }

    companion object {
        private const val MAX_RECORD = 1000

        fun buttonActivity(): Service {
            val service = Service()
            //service.method = Service.Method.POST
            service.allowOrigin = "*"
            service.path = ".button"
            service.action = Service.Action { _, _ ->
                Log.i("Button is clicked")
                mapOf("ok" to true)
            }
            return service
        }

        fun getAppointments(): Service {
            var ok = false
            val service = Service()
            service.method = Service.Method.GET
            service.path = "/appts/:offset/:qty"
            service.allowOrigin = "*"
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
            service.allowOrigin = "*"
            service.action = Service.Action { request, response ->
                var ok = false
                val id: Int
                var err = ""
                val gson = Gson()
                try {
                    id = Integer.parseInt(request.params("id"))
                    val body = request.body().trim()
                    if (body.isNotEmpty()) {
                        Appointment().updateRecord(id)
                        val data = gson.fromJson(body, AppointmentModel::class.java).toMap()
                        if (data.isNotEmpty()) {
                            Log.i("Update record requested by %s", request.ip())
                            data.entries.forEach {
                                when (it.key) {
                                    "firstName", "lastName" -> ok = Appointment().updateName(data["firstName"].toString(), data["lastName"].toString())
                                    "email" -> ok = Appointment().updateEmail(data["email"].toString())
                                    "contactNumber" -> ok = Appointment().updateContactNumber(data["contactNumber"].toString())
                                    "apptType" -> ok = Appointment().updateApptType(data["apptType"].toString())
                                    "apptDate" -> ok = Appointment().updateApptDate(data["apptDate"].toString())
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
                    Log.e("Invalid ID %s passed to update.", e)
                }
                if (err.isNotEmpty()) {
                    Log.w("err: %e", err)
                }
                mapOf("ok" to ok, "err" to err)
            }
            return service
        }
    }
}