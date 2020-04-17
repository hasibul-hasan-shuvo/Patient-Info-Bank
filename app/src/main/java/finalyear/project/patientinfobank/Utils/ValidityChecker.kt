package finalyear.project.patientinfobank.Utils

import android.util.Patterns
import com.google.common.base.Strings.isNullOrEmpty

class ValidityChecker {

    // Method to check phone number is valid or not
    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        // Checking the phone number is valid or not
        val regex = "^01".toRegex()

        if (phoneNumber.length < 11 ||
            !regex.containsMatchIn(phoneNumber)) {
            return false
        }

        return true
    }

    // Method to check email is valid or not
    fun isValidEmail(email: String): Boolean {

        return !isNullOrEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}