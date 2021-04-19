package com.intellisrc.josapedmoreno.ui

import com.intellisrc.core.Log
import com.intellisrc.josapedmoreno.appointment.Appointment
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
    }
}