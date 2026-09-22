package com.example.deliberate.ui.lessons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.example.deliberate.data.local.entity.LessonEntity
import com.example.deliberate.ui.theme.DeliberateTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonsScreen(
    viewModel: LessonViewModel,
    modifier: Modifier = Modifier
) {
    val lessons by viewModel.allLessons.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }
    var showOnlyFavorites by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Favorites", "Technique", "Mindset", "Efficiency", "Concept")

    val filteredLessons = lessons.filter { lesson ->
        if (showOnlyFavorites && !lesson.isFavorite) false
        else if (selectedCategory != "All" && selectedCategory != "Favorites" && lesson.category != selectedCategory) false
        else true
    }

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
                                imageVector = Icons.Rounded.Book,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text("Journal & Lessons", fontWeight = FontWeight.Bold)
                        }
                    }
                )

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = (cat == "Favorites" && showOnlyFavorites) || (cat == selectedCategory && !showOnlyFavorites),
                            onClick = {
                                if (cat == "Favorites") {
                                    showOnlyFavorites = !showOnlyFavorites
                                } else {
                                    showOnlyFavorites = false
                                    selectedCategory = cat
                                }
                            },
                            label = { Text(cat) },
                            leadingIcon = if (cat == "Favorites" && showOnlyFavorites) {
                                { Icon(Icons.Rounded.Favorite, contentDescription = null) }
                            } else null
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                text = { Text("New Lesson") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (filteredLessons.isEmpty()) {
            EmptyLessonsView(onAddClick = { showAddDialog = true }, modifier = Modifier.padding(innerPadding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredLessons, key = { it.id }) { lesson ->
                    LessonCard(
                        lesson = lesson,
                        onToggleFavorite = { isFav ->
                            viewModel.toggleFavorite(lesson.id, isFav)
                        }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddLessonDialog(
                onDismiss = { showAddDialog = false },
                onSave = { title, text, cat ->
                    viewModel.addLesson(title, text, cat)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun LessonCard(
    lesson: LessonEntity,
    onToggleFavorite: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(lesson.createdAt))

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = { },
                    label = { Text(lesson.category) },
                    leadingIcon = { Icon(Icons.Rounded.Category, contentDescription = null) }
                )

                IconButton(onClick = { onToggleFavorite(!lesson.isFavorite) }) {
                    Icon(
                        imageVector = if (lesson.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (lesson.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                }
            }

            Text(
                text = lesson.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = lesson.lessonText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = dateStr,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun EmptyLessonsView(
    onAddClick: () -> Unit,
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
                imageVector = Icons.Rounded.Book,
                contentDescription = null,
                modifier = Modifier.height(72.dp).width(72.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
            Text(
                text = "No Lessons Logged",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Record core insights and principles discovered during practice or reflections.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onAddClick) {
                Icon(Icons.Rounded.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Lesson")
            }
        }
    }
}

@Composable
fun AddLessonDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, text: String, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Technique") }

    val categories = listOf("Technique", "Mindset", "Efficiency", "Concept", "General")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Personal Lesson", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Lesson Title *") },
                    placeholder = { Text("e.g. Always state key in LazyColumn items") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Detailed Explanation *") },
                    placeholder = { Text("e.g. Prevents unnecessary recompositions when lists shift.") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Text("Category", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                FlowRow(
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
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && text.isNotBlank()) {
                        onSave(title, text, category)
                    }
                },
                enabled = title.isNotBlank() && text.isNotBlank()
            ) {
                Text("Save Lesson")
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
fun LessonsScreenPreview() {
    DeliberateTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            Text("Lessons Screen Preview")
        }
    }
}
