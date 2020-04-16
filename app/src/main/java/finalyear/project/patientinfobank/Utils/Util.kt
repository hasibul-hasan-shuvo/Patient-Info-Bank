package finalyear.project.patientinfobank.Utils

class Util {
    companion object {
        val RC_SIGN_IN: Int = 1
        val EMPTY_VALUE = ""
        val REGISTRATION_SUCCESSFUL_MESSAGE = "Registration successful"
        val REGISTRATION_FAILED_MESSAGE = "Registration failed"
        val OPERATION_FAILED_MESSAGE = "Operation failed"
        val DATA_NOT_FOUND_MESSAGE = "Data not found!"
        val UPDATE_SUCCESSFUL_MESSAGE = "Update successful"

        val SEARCH_PATIENT_ID = "SearchPatientId"

        // Toolbar titles
        val PROFILE = "Profile"
        val PATIENT_PRESCRIPTION = "Patient information"
        val BE_A_DONOR = "Donor information"


        // Sign in
        val SIGN_IN_SUCCESSFUL_MESSAGE = "Sign in successful"
        val SIGN_IN_FAILED_MESSAGE = "Sign in failed"


        //Bottom Navigation Item
        //Doctor:
        val DOCTOR_HOME = 0
        val DOCTOR_PROFILE = 1
        // Patient
        val PATIENT_HOME = 0
        val PATIENT_REMINDERS = 1
        val PATIENT_BLOOD = 2
        val PATIENT_PROFILE = 3

        // Error messages
        val EMPTY_ERROR_MESSAGE = "Field is empty"
        val NO_DEGREE_ADDED_ERROR_MESSAGE = "No degree is added"
        val INVALID_PHONE_NUMBER_ERROR_MESSAGE = "Invalid phone number"
        val INVALID_BIRTH_DATE_ERROR_MESSAGE = "Invalid birth date"


        // DatabasePaths
        val USER_CATEGORY_DATABASE = "UsersCategory"

        // FirebaseDatabase utils
        // UserCategory:
        val USER_CATEGORY_IS_DOCTOR = "doctor"
        val USER_CATEGORY_PHONE_NUMBER = "phoneNumber"
        val USER_CATEGORY_DOCTOR_DEGREE_LIST = "doctorDegreeList"
        val USER_CATEGORY_PATIENT_BIRTH_DATE = "patientBirthDate"
    }

}