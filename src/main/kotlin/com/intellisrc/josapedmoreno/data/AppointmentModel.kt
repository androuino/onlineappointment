package com.intellisrc.josapedmoreno.data

data class AppointmentModel(
    var pid: Int = 0,
    var lastName: String = "",
    var firstName: String = "",
    var email: String = "",
    var contactNumber: String = "",
    var apptType: String = "",
    var apptDate: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "pid" to pid,
            "lastName" to lastName,
            "firstName" to firstName,
            "email" to email,
            "contactNumber" to contactNumber,
            "apptType" to apptType,
            "apptDate" to apptDate
        )
    }
}
