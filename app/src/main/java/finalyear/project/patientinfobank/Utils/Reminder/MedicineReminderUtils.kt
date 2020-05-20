package finalyear.project.patientinfobank.Utils.Reminder

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import finalyear.project.patientinfobank.Utils.Util

@Entity(tableName = Util.MEDICINE_REMINDER_TABLE)
data class MedicineReminderUtils(
    @PrimaryKey
    @ColumnInfo(name = Util.ID_COLUMN)
    val id: String,
    @ColumnInfo(name = Util.NAME_COLUMN)
    val name: String,
    @ColumnInfo(name = Util.START_DATE_COLUMN)
    val startDate: String,
    @ColumnInfo(name = Util.END_DATE_COLUMN)
    val endDate: String,
    @ColumnInfo(name = Util.TIME_COLUMN)
    val time: String,
    @ColumnInfo(name = Util.INTERVAL_COLUMN)
    val interval: String
) {
}