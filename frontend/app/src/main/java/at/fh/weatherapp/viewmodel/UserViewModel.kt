package at.fh.weatherapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import at.fh.weatherapp.data.local.UserData
import at.fh.weatherapp.data.local.UserDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = UserDatabase.getDatabase(application).userDao()

    val userData: StateFlow<UserData?> = userDao.getUserData()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun saveUser(firstName: String, lastName: String, email: String) {
        viewModelScope.launch {
            userDao.insertUserData(UserData(firstName = firstName, lastName = lastName, email = email))
        }
    }
}
