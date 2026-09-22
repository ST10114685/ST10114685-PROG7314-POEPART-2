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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deliberate.ui.theme.DeliberateTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeSessionScreen(
    viewModel: PracticeSessionViewModel,
    onBackClick: () -> Unit,
    onSessionCompleted: (sessionId: Long) -> Unit,
    onSessionCancelled: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeSession by viewModel.activeSession.collectAsState()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()

    var notesText by remember { mutableStateOf("") }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }

    val currentSession = activeSession

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentSession?.title ?: "Active Practice Session",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
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
        if (currentSession == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "No active practice session running",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Button(onClick = onBackClick) {
                        Text("Return")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Live Timer Circle Display
                val targetSeconds = currentSession.targetDurationMinutes * 60L
                val progress = if (targetSeconds > 0) {
                    (elapsedSeconds.toFloat() / targetSeconds.toFloat()).coerceIn(0f, 1f)
                } else 0f

                val minutes = elapsedSeconds / 60
                val seconds = elapsedSeconds % 60
                val timerString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

                Surface(
                    modifier = Modifier.size(240.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shadowElevation = 6.dp
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(220.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 10.dp,
                            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = timerString,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isPaused) "PAUSED" else "Target: ${currentSession.targetDurationMinutes}m",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (isPaused) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Controls Row (Play/Pause, Complete, Cancel)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cancel
                    OutlinedButton(
                        onClick = { showCancelDialog = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Rounded.Close, contentDescription = "Cancel")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cancel")
                    }

                    // Play / Pause Toggle
                    Button(
                        onClick = {
                            if (isPaused || !isTimerRunning) {
                                viewModel.resumeSession()
                            } else {
                                viewModel.pauseSession()
                            }
                        },
                        shape = CircleShape,
                        modifier = Modifier.size(64.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = if (isPaused || !isTimerRunning) Icons.Rounded.PlayArrow else Icons.Rounded.Pause,
                            contentDescription = if (isPaused) "Resume" else "Pause",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Complete
                    Button(
                        onClick = { showCompleteDialog = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = "Finish")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Finish")
                    }
                }

                // Objective & Success Criteria Card
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
                        Text(
                            text = "Practice Focus",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (currentSession.objective.isNotBlank()) {
                            Column {
                                Text(
                                    text = "Objective",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    text = currentSession.objective,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        if (currentSession.successCriteria.isNotBlank()) {
                            Column {
                                Text(
                                    text = "Success Criteria",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    text = currentSession.successCriteria,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Quick Session Notes
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Live Session Notes / Observations") },
                    placeholder = { Text("Record insights, mistakes, or key breakthroughs as you practice...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        }

        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = { Text("Cancel Practice Session?") },
                text = { Text("Are you sure you want to stop and cancel this practice session?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showCancelDialog = false
                            viewModel.cancelSession(onCancelled = onSessionCancelled)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Discard Session")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCancelDialog = false }) {
                        Text("Keep Practicing")
                    }
                }
            )
        }

        if (showCompleteDialog) {
            AlertDialog(
                onDismissRequest = { showCompleteDialog = false },
                title = { Text("Complete Practice Session!") },
                text = { Text("Great work! Ready to log this practice session and proceed to reflection?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showCompleteDialog = false
                            viewModel.completeSession(
                                notes = notesText,
                                onCompleted = onSessionCompleted
                            )
                        }
                    ) {
                        Text("Complete & Reflect")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCompleteDialog = false }) {
                        Text("Continue Practice")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PracticeSessionScreenPreview() {
    DeliberateTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            Text("Practice Session Screen Preview")
        }
    }
}
