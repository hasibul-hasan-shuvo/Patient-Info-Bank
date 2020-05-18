package finalyear.project.patientinfobank.Database

import androidx.room.*
import finalyear.project.patientinfobank.Utils.Reminder.MedicineReminderUtils

@Dao
interface MedicineReminderDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(medicineReminder: MedicineReminderUtils)

    @Update
    fun update(medicineReminder: MedicineReminderUtils)

    @Delete
    fun delete(medicineReminder: MedicineReminderUtils)

    @Query("SELECT * FROM Medicine_Reminder WHERE Id == :id")
    fun getMedicineReminder(id: String): MedicineReminderUtils

    @Query("SELECT * FROM Medicine_Reminder")
    fun getAllReminders(): List<MedicineReminderUtils>
}