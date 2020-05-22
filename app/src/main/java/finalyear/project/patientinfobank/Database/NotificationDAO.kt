package finalyear.project.patientinfobank.Database

import androidx.room.*
import finalyear.project.patientinfobank.Utils.Notification.NotificationUtils

@Dao
interface NotificationDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notification: NotificationUtils)

    @Update
    fun update(notification: NotificationUtils)

    @Delete
    fun delete(notification: NotificationUtils)

    @Query("SELECT * FROM Notification WHERE Id == :id")
    fun getNotificationById(id: Int): NotificationUtils

    @Query("SELECT * FROM Notification WHERE User_ID == :userId")
    fun getNotificationByUserId(userId: String): NotificationUtils

    @Query("SELECT * FROM Notification")
    fun getAllNotifications(): List<NotificationUtils>
}