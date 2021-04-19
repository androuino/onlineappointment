package com.intellisrc.josapedmoreno.data

data class AppointmentModel(
    var id: Int = 0,
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var contactNumber: String = "",
    var apptType: String = "",
    var apptDate: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "contactNumber" to contactNumber,
            "apptType" to apptType,
            "apptDate" to apptDate
        )
    }
}
