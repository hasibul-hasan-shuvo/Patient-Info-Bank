package finalyear.project.patientinfobank.Utils.Reminder

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Medicine_Reminder")
data class MedicineReminderUtils(
    @PrimaryKey
    @ColumnInfo(name = "Id")
    val id: String,
    @ColumnInfo(name = "Name")
    val name: String,
    @ColumnInfo(name = "Start_Date")
    val startDate: String,
    @ColumnInfo(name = "End_Date")
    val endDate: String,
    @ColumnInfo(name = "Time")
    val time: String,
    @ColumnInfo(name = "Interval")
    val interval: String
) {
}