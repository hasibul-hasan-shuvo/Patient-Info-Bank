package finalyear.project.patientinfobank.Utils.Prescription

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import finalyear.project.patientinfobank.Utils.Util
import java.io.Serializable

@Entity(tableName = Util.MEDICINE_LIST_TABLE)
data class MedicineUtils(
    @ColumnInfo(name = Util.TYPE_COLUMN)
    val medicineType: String,
    @PrimaryKey
    @ColumnInfo(name = Util.NAME_COLUMN)
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