package finalyear.project.patientinfobank.Utils.UserCategory

data class UserCategoryUtils(
    var isDoctor: Boolean,
    var phoneNumber: String,
    var patientBirthDate: String? = null,
    var doctorDegreeList: ArrayList<String>) {
    constructor() : this(
        false,
        "",
    "",
        ArrayList<String>()
    )
}