package com.example.deliberate.ui.focusarea

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import kotlin.math.roundToInt
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deliberate.data.local.entity.FocusAreaEntity
import com.example.deliberate.data.local.entity.PracticeSessionEntity
import com.example.deliberate.ui.theme.DeliberateTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusAreaDetailScreen(
    focusAreaId: Long,
    viewModel: FocusAreaViewModel,
    onBackClick: () -> Unit,
    onStartSessionClick: (moduleId: Long, focusAreaId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(focusAreaId) {
        viewModel.loadFocusArea(focusAreaId)
    }

    val focusArea by viewModel.focusArea.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val currentFA = focusArea

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentFA?.title ?: "Focus Area Details",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (currentFA != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = "Delete Focus Area",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (currentFA != null) {
                ExtendedFloatingActionButton(
                    onClick = { onStartSessionClick(currentFA.moduleId, currentFA.id) },
                    icon = { Icon(Icons.Rounded.PlayArrow, contentDescription = null) },
                    text = { Text("Start Practice") },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        if (currentFA == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Focus Area not found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Assessment Card
                item {
                    FocusAreaAssessmentCard(
                        focusArea = currentFA,
                        onRatingUpdated = { newRating ->
                            viewModel.updateCurrentRating(newRating)
                        },
                        onStatusUpdated = { newStatus ->
                            viewModel.updateStatus(newStatus)
                        }
                    )
                }

                // Practice History Section
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Practice History (${sessions.size})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (sessions.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "No practice sessions recorded yet",
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Tap 'Start Practice' to begin your first targeted practice session for this focus area.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(sessions, key = { it.id }) { session ->
                        SessionHistoryItemCard(session = session)
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Focus Area?") },
                text = { Text("Are you sure you want to delete this focus area? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteDialog = false
                            viewModel.deleteFocusArea(onDeleted = onBackClick)
                        },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun FocusAreaAssessmentCard(
    focusArea: FocusAreaEntity,
    onRatingUpdated: (Int) -> Unit,
    onStatusUpdated: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var ratingSlider by remember(focusArea.currentRating) { mutableFloatStateOf(focusArea.currentRating.toFloat()) }
    val statuses = listOf("ACTIVE", "MASTERED", "PAUSED")

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = focusArea.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${ratingSlider.roundToInt()} / ${focusArea.targetRating}",
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            if (focusArea.description.isNotBlank()) {
                Text(
                    text = focusArea.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }

            // Status Filter Chips
            Text(
                text = "Status",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                statuses.forEach { st ->
                    FilterChip(
                        selected = focusArea.status == st,
                        onClick = { onStatusUpdated(st) },
                        label = { Text(st) },
                        leadingIcon = if (focusArea.status == st) {
                            { Icon(Icons.Rounded.CheckCircle, contentDescription = null) }
                        } else null
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Rating Progress
            val progress = (ratingSlider / focusArea.targetRating.toFloat()).coerceIn(0f, 1f)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Baseline: ${focusArea.baselineRating} / 10",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = "${(progress * 100).toInt()}% Target Reached",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Current Rating: ${ratingSlider.roundToInt()} / 10",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Slider(
                value = ratingSlider,
                onValueChange = {
                    ratingSlider = it
                    onRatingUpdated(it.roundToInt())
                },
                valueRange = 1f..10f,
                steps = 8
            )
        }
    }
}

@Composable
fun SessionHistoryItemCard(
    session: PracticeSessionEntity,
    modifier: Modifier = Modifier
) {
    val durationMinutes = session.actualDurationSeconds / 60
    val durationSeconds = session.actualDurationSeconds % 60
    val dateStr = SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault()).format(Date(session.startTime))

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                AssistChip(
                    onClick = { },
                    label = { Text("${durationMinutes}m ${durationSeconds}s") }
                )
            }

            if (session.objective.isNotBlank()) {
                Text(
                    text = "Objective: ${session.objective}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            if (session.successCriteria.isNotBlank()) {
                Text(
                    text = "Criteria: ${session.successCriteria}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = dateStr,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FocusAreaAssessmentCardPreview() {
    DeliberateTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            FocusAreaAssessmentCard(
                focusArea = FocusAreaEntity(
                    id = 1,
                    moduleId = 1,
                    title = "Flows & Coroutine Scope",
                    description = "Mastering async state streams and scope cancellation.",
                    baselineRating = 3,
                    targetRating = 10,
                    currentRating = 7,
                    status = "ACTIVE"
                ),
                onRatingUpdated = {},
                onStatusUpdated = {}
            )
        }
    }
}
