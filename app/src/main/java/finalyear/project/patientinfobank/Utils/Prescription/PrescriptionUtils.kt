package finalyear.project.patientinfobank.Utils.Prescription

import finalyear.project.patientinfobank.Utils.UserCategory.UserCategoryUtils
import java.io.Serializable

data class PrescriptionUtils(
    var doctorUtils: UserCategoryUtils?,
    var CCList: ArrayList<String>,
    var OEList: ArrayList<String>,
    var adviceList: ArrayList<String>,
    var medicineList: ArrayList<MedicineUtils>
): Serializable{
    constructor(): this(
        null,
        ArrayList<String>(),
        ArrayList<String>(),
        ArrayList<String>(),
        ArrayList<MedicineUtils>()
    )

}