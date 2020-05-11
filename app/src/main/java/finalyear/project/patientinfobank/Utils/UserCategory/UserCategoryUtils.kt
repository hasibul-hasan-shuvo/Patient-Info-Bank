package finalyear.project.patientinfobank.Utils.UserCategory

import java.io.Serializable

data class UserCategoryUtils(
    var isDoctor: Boolean,
    var name: String,
    var phoneNumber: String,
    var patientBirthDate: String? = null,
    var doctorDegreeList: ArrayList<String>): Serializable {
    constructor() : this(
        false,
        "",
        "",
    "",
        ArrayList<String>()
    )
}