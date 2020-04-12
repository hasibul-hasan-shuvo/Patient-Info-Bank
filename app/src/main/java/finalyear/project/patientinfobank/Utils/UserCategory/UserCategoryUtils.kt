package finalyear.project.patientinfobank.Utils.UserCategory

data class UserCategoryUtils(
    val isDoctor: Boolean,
    val phoneNumber: String,
    val patientBirthDate: String? = null,
    val doctorDegreeList: List<String>? = null) {
    constructor() : this(
        false,
        "",
    "",
    emptyList())
}