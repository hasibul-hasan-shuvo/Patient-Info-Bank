package finalyear.project.patientinfobank.Services

import android.content.Context
import android.net.ConnectivityManager

class InternetConnection {
    companion object {
        fun checkConnection(context: Context): Boolean {
            val connectivityManager: ConnectivityManager = context.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager

            if (connectivityManager != null) {
                val activeNetworkinfo = connectivityManager.activeNetworkInfo

                if (activeNetworkinfo != null) {
                    if (activeNetworkinfo.type == ConnectivityManager.TYPE_WIFI) {
                        return true
                    } else {
                        return activeNetworkinfo.type == ConnectivityManager.TYPE_MOBILE
                    }
                }
            }

            return false
        }
    }
}