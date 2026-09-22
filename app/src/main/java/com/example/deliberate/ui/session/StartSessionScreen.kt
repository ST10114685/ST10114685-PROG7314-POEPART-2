package com.example.deliberate.ui.session

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deliberate.ui.modules.LearningModuleViewModel
import com.example.deliberate.ui.theme.DeliberateTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartSessionScreen(
    initialModuleId: Long?,
    initialFocusAreaId: Long?,
    moduleViewModel: LearningModuleViewModel,
    sessionViewModel: PracticeSessionViewModel,
    onBackClick: () -> Unit,
    onSessionStarted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val modules by moduleViewModel.activeModules.collectAsState()
    
    var selectedModuleId by remember(initialModuleId) {
        mutableStateOf(initialModuleId ?: modules.firstOrNull()?.id ?: 0L)
    }

    // Auto-select first module if selectedModuleId is 0 and modules exist
    if (selectedModuleId == 0L && modules.isNotEmpty()) {
        selectedModuleId = modules.first().id
    }

    var selectedFocusAreaId by remember(initialFocusAreaId) {
        mutableStateOf(initialFocusAreaId)
    }

    var title by remember { mutableStateOf("") }
    var objective by remember { mutableStateOf("") }
    var successCriteria by remember { mutableStateOf("") }
    var targetDurationMinutes by remember { mutableIntStateOf(25) }

    val durationOptions = listOf(15, 25, 30, 45, 60)

    val selectedModule = modules.find { it.id == selectedModuleId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New Practice Session",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
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
            // Module Banner Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Skill Domain",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedModule?.title ?: "Select a Module",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (selectedModule?.description?.isNotBlank() == true) {
                        Text(
                            text = selectedModule.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Session Title") },
                placeholder = { Text("e.g. Compose State & Side-Effects") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Objective Input
            OutlinedTextField(
                value = objective,
                onValueChange = { objective = it },
                label = { Text("Session Objective *") },
                placeholder = { Text("e.g. Master LaunchedEffect, rememberUpdatedState, and produceState") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            // Success Criteria Input
            OutlinedTextField(
                value = successCriteria,
                onValueChange = { successCriteria = it },
                label = { Text("Success Criteria *") },
                placeholder = { Text("e.g. Build working countdown timer without memory leaks or state bugs") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            // Target Duration Chips
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Timer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Target Duration",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    durationOptions.forEach { mins ->
                        FilterChip(
                            selected = targetDurationMinutes == mins,
                            onClick = { targetDurationMinutes = mins },
                            label = { Text("${mins}m") },
                            leadingIcon = if (targetDurationMinutes == mins) {
                                { Icon(Icons.Rounded.CheckCircle, contentDescription = null) }
                            } else null
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (selectedModuleId > 0L) {
                        sessionViewModel.startSession(
                            moduleId = selectedModuleId,
                            focusAreaId = selectedFocusAreaId,
                            title = title.ifBlank { "${selectedModule?.title ?: "Practice"} Session" },
                            objective = objective,
                            successCriteria = successCriteria,
                            targetDurationMinutes = targetDurationMinutes,
                            onStarted = onSessionStarted
                        )
                    }
                },
                enabled = selectedModuleId > 0L && objective.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Start Practice Timer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StartSessionScreenPreview() {
    DeliberateTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            Text("Start Session Screen Preview")
        }
    }
}
