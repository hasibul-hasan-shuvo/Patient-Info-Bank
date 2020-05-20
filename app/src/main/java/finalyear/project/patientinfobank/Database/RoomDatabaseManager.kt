package finalyear.project.patientinfobank.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import finalyear.project.patientinfobank.Utils.Prescription.MedicineUtils
import finalyear.project.patientinfobank.Utils.Reminder.MedicineReminderUtils
import finalyear.project.patientinfobank.Utils.Util

@Database(
    entities = [MedicineReminderUtils::class, MedicineUtils::class],
    version = Util.ROOM_DATABASE_VERSION
)
abstract class RoomDatabaseManager: RoomDatabase() {
    abstract fun getMedicineReminderDAO(): MedicineReminderDAO
    abstract fun getMedicineDAO(): MedicineDAO

    companion object{
        var INSTANCE: RoomDatabaseManager? = null

        fun getInstance(context: Context): RoomDatabaseManager? {

            if (INSTANCE == null) {
                synchronized(RoomDatabaseManager::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        RoomDatabaseManager::class.java,
                        Util.ROOM_DATABASE_NAME
                    ).allowMainThreadQueries().build()
                }
            }

            return INSTANCE
        }

        fun destroy(){
            INSTANCE = null
        }
    }
}