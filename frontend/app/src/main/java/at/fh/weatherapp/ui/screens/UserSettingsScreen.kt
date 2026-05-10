package at.fh.weatherapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.fh.weatherapp.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSettingsScreen(
    userViewModel: UserViewModel,
    onNavigateBack: () -> Unit
) {
    val userData by userViewModel.userData.collectAsState()

    var firstName by remember(userData) { mutableStateOf(userData?.firstName ?: "") }
    var lastName by remember(userData) { mutableStateOf(userData?.lastName ?: "") }
    var email by remember(userData) { mutableStateOf(userData?.email ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Settings") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    userViewModel.saveUser(firstName, lastName, email)
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}
