package com.example.deliberate.ui.reflection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Psychology
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import com.example.deliberate.ui.theme.DeliberateTheme
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReflectionScreen(
    sessionId: Long,
    viewModel: ReflectionViewModel,
    onBackClick: () -> Unit,
    onReflectionSubmitted: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(sessionId) {
        viewModel.loadSession(sessionId)
    }

    val session by viewModel.currentSession.collectAsState()

    var rating by remember { mutableFloatStateOf(7f) }
    var whatWentWell by remember { mutableStateOf("") }
    var whatToImprove by remember { mutableStateOf("") }
    var keyTakeaway by remember { mutableStateOf("") }
    var saveAsLesson by remember { mutableStateOf(true) }
    var lessonCategory by remember { mutableStateOf("Technique") }

    val categories = listOf("Technique", "Mindset", "Efficiency", "Concept", "General")

    val currentSession = session

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Psychology,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text("Session Reflection", fontWeight = FontWeight.Bold)
                    }
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
            // Session Info Card
            if (currentSession != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = currentSession.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (currentSession.objective.isNotBlank()) {
                            Text(
                                text = "Objective: ${currentSession.objective}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // Self Assessment Rating (1-10)
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Self-Assessment Rating",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${rating.roundToInt()} / 10",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    val ratingLabel = when (rating.roundToInt()) {
                        in 1..3 -> "Challenging / Needs Focus"
                        in 4..6 -> "Moderate Progress"
                        in 7..8 -> "Strong & Intentional"
                        else -> "Peak Performance & Flow"
                    }

                    Text(
                        text = ratingLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Slider(
                        value = rating,
                        onValueChange = { rating = it },
                        valueRange = 1f..10f,
                        steps = 8
                    )
                }
            }

            // Prompt 1: What went well
            OutlinedTextField(
                value = whatWentWell,
                onValueChange = { whatWentWell = it },
                label = { Text("What went well during this session?") },
                placeholder = { Text("e.g. Maintained focus, resolved layout bugs quickly...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            // Prompt 2: What could be improved
            OutlinedTextField(
                value = whatToImprove,
                onValueChange = { whatToImprove = it },
                label = { Text("What could be improved next time?") },
                placeholder = { Text("e.g. Reduce context switching, plan state structure first...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            // Prompt 3: Key Takeaway
            OutlinedTextField(
                value = keyTakeaway,
                onValueChange = { keyTakeaway = it },
                label = { Text("Key Lesson / Core Insight *") },
                placeholder = { Text("e.g. Derived state should always use remember(key) in Compose") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            // Save as Lesson Switch
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Rounded.Book, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "Save to Personal Journal",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Switch(
                            checked = saveAsLesson,
                            onCheckedChange = { saveAsLesson = it }
                        )
                    }

                    if (saveAsLesson) {
                        Text(
                            text = "Lesson Category",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            categories.take(4).forEach { cat ->
                                AssistChip(
                                    onClick = { lessonCategory = cat },
                                    label = { Text(cat) },
                                    leadingIcon = if (lessonCategory == cat) {
                                        { Icon(Icons.Rounded.Star, contentDescription = null) }
                                    } else null
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val modId = currentSession?.moduleId ?: 1L
                    val faId = currentSession?.focusAreaId
                    viewModel.submitReflection(
                        sessionId = sessionId,
                        moduleId = modId,
                        focusAreaId = faId,
                        rating = rating.roundToInt(),
                        whatWentWell = whatWentWell,
                        whatToImprove = whatToImprove,
                        keyTakeaway = keyTakeaway,
                        saveAsLesson = saveAsLesson,
                        lessonCategory = lessonCategory,
                        onCompleted = onReflectionSubmitted
                    )
                },
                enabled = keyTakeaway.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Rounded.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Complete Reflection",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReflectionScreenPreview() {
    DeliberateTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            Text("Reflection Screen Preview")
        }
    }
}
