package com.example.deliberate.ui.experiments

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Science
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deliberate.data.local.entity.ExperimentEntity
import com.example.deliberate.data.local.entity.PracticeCycleEntity
import com.example.deliberate.ui.theme.DeliberateTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperimentsScreen(
    viewModel: ExperimentViewModel,
    modifier: Modifier = Modifier
) {
    val experiments by viewModel.experiments.collectAsState()
    val cycles by viewModel.cycles.collectAsState()
    val activeCycle by viewModel.activeCycle.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showAddExperimentDialog by remember { mutableStateOf(false) }
    var showAddCycleDialog by remember { mutableStateOf(false) }
    var selectedExpForComplete by remember { mutableStateOf<ExperimentEntity?>(null) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Science,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text("Experiments & Cycles", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                TabRow(selectedTabIndex = selectedTabIndex) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Experiments (${experiments.size})") },
                        icon = { Icon(Icons.Rounded.Science, contentDescription = null) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Cycles (${cycles.size})") },
                        icon = { Icon(Icons.Rounded.Autorenew, contentDescription = null) }
                    )
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (selectedTabIndex == 0) showAddExperimentDialog = true
                    else showAddCycleDialog = true
                },
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                text = { Text(if (selectedTabIndex == 0) "New Experiment" else "New Cycle") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedTabIndex == 0) {
                // Experiments List
                if (experiments.isEmpty()) {
                    EmptyExperimentsView(onAddClick = { showAddExperimentDialog = true })
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(experiments, key = { it.id }) { exp ->
                            ExperimentCard(
                                experiment = exp,
                                onClick = {
                                    if (exp.status == "ACTIVE") {
                                        selectedExpForComplete = exp
                                    }
                                }
                            )
                        }
                    }
                }
            } else {
                // Cycles List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (activeCycle != null) {
                        item {
                            ActiveCycleCard(
                                cycle = activeCycle!!,
                                onComplete = { review ->
                                    viewModel.completeCycle(activeCycle!!.id, review)
                                },
                                onLogPractice = {
                                    viewModel.logCyclePractice(activeCycle!!.id)
                                }
                            )
                        }
                    }

                    if (cycles.isEmpty() && activeCycle == null) {
                        item {
                            EmptyCyclesView(onAddClick = { showAddCycleDialog = true })
                        }
                    } else {
                        items(cycles.filter { it.id != activeCycle?.id }, key = { it.id }) { cycle ->
                            CycleCard(cycle = cycle)
                        }
                    }
                }
            }
        }

        if (showAddExperimentDialog) {
            AddExperimentDialog(
                onDismiss = { showAddExperimentDialog = false },
                onSave = { title, hyp, action, metric ->
                    viewModel.addExperiment(title, hyp, action, metric)
                    showAddExperimentDialog = false
                }
            )
        }

        if (showAddCycleDialog) {
            AddCycleDialog(
                onDismiss = { showAddCycleDialog = false },
                onSave = { title, goal, days ->
                    viewModel.addCycle(title, goal, days)
                    showAddCycleDialog = false
                }
            )
        }

        if (selectedExpForComplete != null) {
            CompleteExperimentDialog(
                experiment = selectedExpForComplete!!,
                onDismiss = { selectedExpForComplete = null },
                onComplete = { outcome, rating ->
                    viewModel.completeExperiment(selectedExpForComplete!!.id, outcome, rating)
                    selectedExpForComplete = null
                }
            )
        }
    }
}

@Composable
fun ExperimentCard(
    experiment: ExperimentEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = experiment.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                AssistChip(
                    onClick = { },
                    label = { Text(experiment.status) }
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Hypothesis",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = experiment.hypothesis,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            if (experiment.actionItems.isNotBlank()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Action Plan",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = experiment.actionItems,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (experiment.status == "COMPLETED" && experiment.outcomeNotes.isNotBlank()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Outcome & Conclusion",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = experiment.outcomeNotes,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveCycleCard(
    cycle: PracticeCycleEntity,
    onComplete: (reviewNotes: String) -> Unit,
    onLogPractice: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showReviewDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
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
                    text = "Active Cycle",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                AssistChip(
                    onClick = { },
                    label = { Text("Streak: ${cycle.streakCount} days 🔥") }
                )
            }

            Text(
                text = cycle.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Goal: ${cycle.goal}",
                style = MaterialTheme.typography.bodyLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onLogPractice,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Log Today")
                }
                OutlinedButton(
                    onClick = { showReviewDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Finish Cycle")
                }
            }
        }
    }

    if (showReviewDialog) {
        var reviewText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Complete Practice Cycle") },
            text = {
                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Cycle Review Notes / Key Wins") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showReviewDialog = false
                        onComplete(reviewText)
                    }
                ) {
                    Text("Finish Cycle")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CycleCard(
    cycle: PracticeCycleEntity,
    modifier: Modifier = Modifier
) {
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
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = cycle.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = cycle.status,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = cycle.goal,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (cycle.reviewNotes.isNotBlank()) {
                Text(
                    text = "Review: ${cycle.reviewNotes}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun EmptyExperimentsView(onAddClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Science,
                contentDescription = null,
                modifier = Modifier.height(72.dp).width(72.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
            Text(
                text = "No Experiments Yet",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Test learning hypotheses (e.g., 'Pomodoro 25m increases retention', 'Drawing line-art first improves proportion').",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onAddClick) {
                Icon(Icons.Rounded.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Create Experiment")
            }
        }
    }
}

@Composable
fun EmptyCyclesView(onAddClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Autorenew,
                contentDescription = null,
                modifier = Modifier.height(72.dp).width(72.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
            Text(
                text = "No Active Cycle",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Set a 14-day or 30-day dedicated practice cycle goal.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onAddClick) {
                Icon(Icons.Rounded.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Start Practice Cycle")
            }
        }
    }
}

@Composable
fun AddExperimentDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, hypothesis: String, actionItems: String, metricToTrack: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var hypothesis by remember { mutableStateOf("") }
    var actionItems by remember { mutableStateOf("") }
    var metricToTrack by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Practice Experiment", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Experiment Title *") },
                    placeholder = { Text("e.g. 25-minute Pomodoro Sprints") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = hypothesis,
                    onValueChange = { hypothesis = it },
                    label = { Text("Hypothesis *") },
                    placeholder = { Text("e.g. Short timed sprints improve focus and speed") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                OutlinedTextField(
                    value = actionItems,
                    onValueChange = { actionItems = it },
                    label = { Text("Action Items") },
                    placeholder = { Text("e.g. Turn off notifications, use timer during practice") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = metricToTrack,
                    onValueChange = { metricToTrack = it },
                    label = { Text("Metric To Track") },
                    placeholder = { Text("e.g. Self-Rating score / 10") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && hypothesis.isNotBlank()) {
                        onSave(title, hypothesis, actionItems, metricToTrack)
                    }
                },
                enabled = title.isNotBlank() && hypothesis.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddCycleDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, goal: String, durationDays: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Start Practice Cycle", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Cycle Name *") },
                    placeholder = { Text("e.g. 14-Day Jetpack Compose Sprint") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = goal,
                    onValueChange = { goal = it },
                    label = { Text("Cycle Goal *") },
                    placeholder = { Text("e.g. Practice 30 mins daily for 14 days straight") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && goal.isNotBlank()) {
                        onSave(title, goal, 14)
                    }
                },
                enabled = title.isNotBlank() && goal.isNotBlank()
            ) {
                Text("Start Cycle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CompleteExperimentDialog(
    experiment: ExperimentEntity,
    onDismiss: () -> Unit,
    onComplete: (outcomeNotes: String, rating: Int) -> Unit
) {
    var outcomeNotes by remember { mutableStateOf("") }
    var rating by remember { mutableFloatStateOf(8f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Complete Experiment", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Experiment: ${experiment.title}")
                OutlinedTextField(
                    value = outcomeNotes,
                    onValueChange = { outcomeNotes = it },
                    label = { Text("Conclusion & Outcome Notes") },
                    placeholder = { Text("e.g. Hypothesis confirmed! Focus rating increased by 20%.") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Text("Experiment Rating: ${rating.toInt()} / 10")
                Slider(
                    value = rating,
                    onValueChange = { rating = it },
                    valueRange = 1f..10f,
                    steps = 8
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onComplete(outcomeNotes, rating.toInt()) }
            ) {
                Text("Complete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ExperimentsScreenPreview() {
    DeliberateTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            Text("Experiments Screen Preview")
        }
    }
}
