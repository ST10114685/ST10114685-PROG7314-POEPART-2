package com.example.deliberate.ui.modules

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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deliberate.data.local.entity.FocusAreaEntity
import com.example.deliberate.data.local.entity.LearningModuleEntity
import com.example.deliberate.ui.theme.DeliberateTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleDetailScreen(
    moduleId: Long,
    viewModel: LearningModuleViewModel,
    onBackClick: () -> Unit,
    onFocusAreaClick: (Long) -> Unit,
    onStartSessionClick: (moduleId: Long, focusAreaId: Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(moduleId) {
        viewModel.selectModule(moduleId)
    }

    val module by viewModel.selectedModule.collectAsState()
    val focusAreas by viewModel.selectedModuleFocusAreas.collectAsState()
    var showAddFocusAreaDialog by remember { mutableStateOf(false) }

    val currentModule = module

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentModule?.title ?: "Module Details",
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
        if (currentModule == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Module not found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Assessment Card
                item {
                    ModuleHeaderCard(
                        module = currentModule,
                        onRatingUpdated = { newRating ->
                            viewModel.updateModule(currentModule.copy(currentRating = newRating))
                        },
                        onStartSession = { onStartSessionClick(currentModule.id, null) }
                    )
                }

                // Focus Areas Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.TrackChanges,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Focus Areas (${focusAreas.size})",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = { showAddFocusAreaDialog = true },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Focus Area")
                        }
                    }
                }

                if (focusAreas.isEmpty()) {
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
                                    text = "No focus areas added yet",
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Break down this module into specific targeted focus areas (e.g. StateFlow, Recomposition, Dynamic Color).",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(focusAreas, key = { it.id }) { fa ->
                        FocusAreaCard(
                            focusArea = fa,
                            onClick = { onFocusAreaClick(fa.id) },
                            onStartSession = { onStartSessionClick(currentModule.id, fa.id) }
                        )
                    }
                }
            }
        }

        if (showAddFocusAreaDialog && currentModule != null) {
            AddFocusAreaDialog(
                onDismiss = { showAddFocusAreaDialog = false },
                onSave = { title, desc, baselineRating, targetRating ->
                    viewModel.addFocusArea(
                        moduleId = currentModule.id,
                        title = title,
                        description = desc,
                        baselineRating = baselineRating,
                        targetRating = targetRating
                    )
                    showAddFocusAreaDialog = false
                }
            )
        }
    }
}

@Composable
fun ModuleHeaderCard(
    module: LearningModuleEntity,
    onRatingUpdated: (Int) -> Unit,
    onStartSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    var ratingSlider by remember(module.currentRating) { mutableFloatStateOf(module.currentRating.toFloat()) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = { },
                    label = { Text(module.category) },
                    leadingIcon = { Icon(Icons.Rounded.Category, contentDescription = null) }
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Rounded.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "${ratingSlider.toInt()} / ${module.targetRating}",
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = module.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            if (module.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = module.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rating Progress
            val progress = (ratingSlider / module.targetRating.toFloat()).coerceIn(0f, 1f)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Baseline: ${module.baselineRating}/10",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = "Target Goal: ${module.targetRating}/10",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Self-Assessment Rating: ${ratingSlider.toInt()} / 10",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Slider(
                value = ratingSlider,
                onValueChange = {
                    ratingSlider = it
                    onRatingUpdated(it.toInt())
                },
                valueRange = 1f..10f,
                steps = 8
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onStartSession,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Practice Session")
            }
        }
    }
}

@Composable
fun FocusAreaCard(
    focusArea: FocusAreaEntity,
    onClick: () -> Unit,
    onStartSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = focusArea.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    AssistChip(
                        onClick = { },
                        label = { Text(focusArea.status) }
                    )
                }

                if (focusArea.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = focusArea.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Rating: ${focusArea.currentRating} / ${focusArea.targetRating} (Baseline ${focusArea.baselineRating})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onStartSession) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = "Start Session",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun AddFocusAreaDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, desc: String, baselineRating: Int, targetRating: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var baselineRating by remember { mutableFloatStateOf(2f) }
    var targetRating by remember { mutableFloatStateOf(9f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Focus Area", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Focus Area Title *") },
                    placeholder = { Text("e.g. StateFlow vs SharedFlow") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    placeholder = { Text("e.g. Cold vs Hot streams in coroutines") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Baseline Assessment: ${baselineRating.toInt()} / 10",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Slider(
                    value = baselineRating,
                    onValueChange = { baselineRating = it },
                    valueRange = 1f..10f,
                    steps = 8
                )

                Text(
                    text = "Target Rating: ${targetRating.toInt()} / 10",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Slider(
                    value = targetRating,
                    onValueChange = { targetRating = it },
                    valueRange = 1f..10f,
                    steps = 8
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, desc, baselineRating.toInt(), targetRating.toInt())
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Add")
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
fun ModuleHeaderCardPreview() {
    DeliberateTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ModuleHeaderCard(
                module = LearningModuleEntity(
                    id = 1,
                    title = "Digital Art & Drawing",
                    description = "Anatomy, lighting, and color theory.",
                    category = "Art",
                    baselineRating = 2,
                    targetRating = 10,
                    currentRating = 5
                ),
                onRatingUpdated = {},
                onStartSession = {}
            )
        }
    }
}
