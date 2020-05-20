package finalyear.project.patientinfobank.Database

import androidx.room.*
import finalyear.project.patientinfobank.Utils.Prescription.MedicineUtils

@Dao
interface MedicineDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(medicine: MedicineUtils)

    @Update
    fun update(medicine: MedicineUtils)

    @Delete
    fun delete(medicine: MedicineUtils)

    @Query("SELECT Name FROM Medicine_List WHERE Name == :name")
    fun getMedicine(name: String): String

    @Query("SELECT Name FROM Medicine_List ORDER BY Name ASC")
    fun getAllMedicines(): List<String>

    @Query("SELECT Name FROM Medicine_List WHERE Type == :type ORDER BY Name ASC")
    fun getAllMedicinesBasedOnTypes(type: String): List<String>
}