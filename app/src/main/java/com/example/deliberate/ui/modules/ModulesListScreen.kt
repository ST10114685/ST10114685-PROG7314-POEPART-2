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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import com.example.deliberate.data.local.entity.LearningModuleEntity
import com.example.deliberate.ui.theme.DeliberateTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModulesListScreen(
    viewModel: LearningModuleViewModel,
    onModuleClick: (Long) -> Unit,
    onStartSessionClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeModules by viewModel.activeModules.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Learning Modules",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Rounded.Add, contentDescription = "Add Skill Domain") },
                text = { Text("New Module") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (activeModules.isEmpty()) {
            EmptyModulesView(
                onAddModuleClick = { showAddDialog = true },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(activeModules, key = { it.id }) { module ->
                    ModuleItemCard(
                        module = module,
                        onClick = { onModuleClick(module.id) },
                        onStartSession = { onStartSessionClick(module.id) }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddModuleDialog(
                onDismiss = { showAddDialog = false },
                onSave = { title, desc, cat, baseRating, targetRating ->
                    viewModel.addModule(
                        title = title,
                        description = desc,
                        category = cat,
                        baselineRating = baseRating,
                        targetRating = targetRating
                    )
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun ModuleItemCard(
    module: LearningModuleEntity,
    onClick: () -> Unit,
    onStartSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
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
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Rating",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Level ${module.currentRating} / ${module.targetRating}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = module.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (module.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = module.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rating progress bar
            val progress = (module.currentRating.toFloat() / module.targetRating.toFloat()).coerceIn(0f, 1f)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Baseline: ${module.baselineRating}/10",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "${(progress * 100).toInt()}% Target Reached",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onStartSession,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Practice")
                }
            }
        }
    }
}

@Composable
fun EmptyModulesView(
    onAddModuleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.School,
                contentDescription = null,
                modifier = Modifier.height(72.dp).width(72.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
            Text(
                text = "No Skill Domains Yet",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Create your first learning module (e.g., Kotlin, Guitar, Digital Art) to start deliberate practice.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onAddModuleClick,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Module")
            }
        }
    }
}

@Composable
fun AddModuleDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, desc: String, category: String, baselineRating: Int, targetRating: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Programming") }
    var baselineRating by remember { mutableFloatStateOf(3f) }
    var targetRating by remember { mutableFloatStateOf(9f) }

    val categories = listOf("Programming", "Music", "Art", "Language", "Fitness", "Design", "General")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create Learning Module", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Module Title *") },
                    placeholder = { Text("e.g. Kotlin Jetpack Compose") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description") },
                    placeholder = { Text("e.g. Modern Android UI development") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(4).forEach { cat ->
                        AssistChip(
                            onClick = { category = cat },
                            label = { Text(cat) },
                            leadingIcon = if (category == cat) {
                                { Icon(Icons.Rounded.Star, contentDescription = null) }
                            } else null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

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
                    text = "Target Skill Goal: ${targetRating.toInt()} / 10",
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
                        onSave(
                            title,
                            desc,
                            category,
                            baselineRating.toInt(),
                            targetRating.toInt()
                        )
                    }
                },
                enabled = title.isNotBlank()
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

@Preview(showBackground = true)
@Composable
fun ModuleItemCardPreview() {
    DeliberateTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ModuleItemCard(
                module = LearningModuleEntity(
                    id = 1,
                    title = "Kotlin & Jetpack Compose",
                    description = "Mastering reactive UI architectures and custom layouts.",
                    category = "Programming",
                    baselineRating = 3,
                    targetRating = 9,
                    currentRating = 6
                ),
                onClick = {},
                onStartSession = {}
            )
        }
    }
}
