package at.fh.weatherapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_data")
data class UserData(
    @PrimaryKey
    val id: Int = 1, // Only one user for now
    val firstName: String,
    val lastName: String,
    val email: String
)
