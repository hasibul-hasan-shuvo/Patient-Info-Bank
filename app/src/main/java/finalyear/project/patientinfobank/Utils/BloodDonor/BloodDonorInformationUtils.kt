package finalyear.project.patientinfobank.Utils.BloodDonor

import java.io.Serializable

@SuppressWarnings("serial")
class BloodDonorInformationUtils(
    var fullName: String,
    var division: String,
    var district: String,
    var address: String,
    var phoneNumber: String,
    var email: String,
    var birthDate: String,
    var bloodGroup: String
): Serializable {
    constructor(): this(
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        ""
    )
}