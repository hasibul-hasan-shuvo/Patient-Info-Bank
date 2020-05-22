package finalyear.project.patientinfobank.Utils.Notification

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import finalyear.project.patientinfobank.Utils.Util
import java.io.Serializable


@Entity(tableName = Util.NOTIFICATION_TABLE)
data class NotificationUtils(
    @ColumnInfo(name = Util.USER_ID_COLUMN)
    val userId: String?,
    @PrimaryKey
    @ColumnInfo(name = Util.ID_COLUMN)
    val Id: Int,
    @ColumnInfo(name = Util.TITLE_COLUMN)
    val title: String?,
    @ColumnInfo(name = Util.MESSAGE_COLUMN)
    val message: String?,
    @ColumnInfo(name = Util.TOPIC_COLUMN)
    val topic: String?,
    @ColumnInfo(name = Util.IMAGE_URL_COLUMN)
    val imageUrl: String?,
    @ColumnInfo(name = Util.DATE_COLUMN)
    val date: String?,
    @ColumnInfo(name = Util.TIME_COLUMN)
    val time: String?
): Serializable {
}