package finalyear.project.patientinfobank.Utils.Prescription

import java.io.Serializable


data class MedicineUtils(
    val medicineType: String,
    val medicineName: String,
    val medicineTime: String,
    val mealTime: String,
    val medicinePeriod: String,
    val comment: String
): Serializable {
    constructor(): this(
        "",
        "",
        "",
        "",
        "",
        ""
    )
}