package finalyear.project.patientinfobank.Utils.BloodDonor

class BloodDonorInformationUtils(
    var fullName: String,
    var division: String,
    var district: String,
    var address: String,
    var phoneNumber: String,
    var email: String,
    var birthDate: String,
    var bloodGroup: String
) {
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