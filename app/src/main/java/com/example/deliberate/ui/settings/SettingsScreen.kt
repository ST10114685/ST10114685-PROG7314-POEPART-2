package com.example.deliberate.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deliberate.ui.theme.DeliberateTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val prefs by viewModel.userPreferences.collectAsState()

    var userName by remember(prefs.userName) { mutableStateOf(prefs.userName) }
    var userEmail by remember(prefs.userEmail) { mutableStateOf(prefs.userEmail) }
    var showResetDialog by remember { mutableStateOf(false) }

    val themeModes = listOf("SYSTEM", "LIGHT", "DARK")
    val goalOptions = listOf(15, 30, 60)
    val isCustomSelected = prefs.dailyGoalMinutes !in goalOptions
    var customMinutesInput by remember(prefs.dailyGoalMinutes) {
        mutableStateOf(if (isCustomSelected) prefs.dailyGoalMinutes.toString() else "")
    }
    var showCustomInput by remember(isCustomSelected) { mutableStateOf(isCustomSelected) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text("Settings & Profile", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.height(48.dp).width(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Text(
                            text = "Practitioner Profile",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = userEmail,
                        onValueChange = { userEmail = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            viewModel.updateUserName(userName)
                            viewModel.updateUserEmail(userEmail)
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Profile")
                    }
                }
            }

            // Preferences Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Theme Mode
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Rounded.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "App Theme",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            themeModes.forEach { mode ->
                                FilterChip(
                                    selected = prefs.themeMode == mode,
                                    onClick = { viewModel.updateThemeMode(mode) },
                                    label = { Text(mode) },
                                    leadingIcon = if (prefs.themeMode == mode) {
                                        { Icon(Icons.Rounded.CheckCircle, contentDescription = null) }
                                    } else null
                                )
                            }
                        }
                    }

                    // Daily Practice Goal
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Rounded.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "Daily Practice Goal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            goalOptions.forEach { mins ->
                                FilterChip(
                                    selected = prefs.dailyGoalMinutes == mins && !showCustomInput,
                                    onClick = {
                                        showCustomInput = false
                                        viewModel.updateDailyGoalMinutes(mins)
                                    },
                                    label = { Text("${mins}m") },
                                    leadingIcon = if (prefs.dailyGoalMinutes == mins && !showCustomInput) {
                                        { Icon(Icons.Rounded.CheckCircle, contentDescription = null) }
                                    } else null
                                )
                            }
                            FilterChip(
                                selected = showCustomInput,
                                onClick = { showCustomInput = true },
                                label = { Text("Custom") },
                                leadingIcon = if (showCustomInput) {
                                    { Icon(Icons.Rounded.CheckCircle, contentDescription = null) }
                                } else null
                            )
                        }

                        if (showCustomInput) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = customMinutesInput,
                                    onValueChange = { newValue ->
                                        if (newValue.all { it.isDigit() }) {
                                            customMinutesInput = newValue
                                        }
                                    },
                                    label = { Text("Minutes") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                Button(
                                    onClick = {
                                        customMinutesInput.toIntOrNull()?.let { mins ->
                                            if (mins > 0) {
                                                viewModel.updateDailyGoalMinutes(mins)
                                            }
                                        }
                                    },
                                    enabled = customMinutesInput.isNotBlank()
                                ) {
                                    Text("Apply")
                                }
                            }
                        }
                    }

                    // Reminders & Notifications Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Rounded.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Column {
                                Text(
                                    text = "Practice Reminders",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Daily reflection & streak notifications",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = prefs.notificationsEnabled,
                            onCheckedChange = { viewModel.updateNotificationsEnabled(it) }
                        )
                    }
                }
            }

            // Session & Profile Control Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Session Control",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign Out of Session")
                    }
                }
            }

            // Data Reset Card
            OutlinedButton(
                onClick = { showResetDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Reset User Preferences")
            }
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Reset Preferences?") },
                text = { Text("Are you sure you want to reset all user preferences to default settings?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.resetPreferences()
                            showResetDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Reset")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    DeliberateTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            Text("Settings Screen Preview")
        }
    }
}
