package finalyear.project.patientinfobank.Utils.UserCategory

data class UserCategoryUtils(
    var isDoctor: Boolean,
    var phoneNumber: String,
    var patientBirthDate: String? = null,
    var doctorDegreeList: List<String>? = null) {
    constructor() : this(
        false,
        "",
    "",
    emptyList())
}