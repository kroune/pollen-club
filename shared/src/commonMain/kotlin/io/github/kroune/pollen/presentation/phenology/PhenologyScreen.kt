package io.github.kroune.pollen.presentation.phenology

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kroune.pollen.domain.model.LoadState
import io.github.kroune.pollen.presentation.common.CollectEvents
import io.github.kroune.pollen.presentation.common.FullScreenError
import io.github.kroune.pollen.presentation.common.shimmerEffect
import io.github.kroune.pollen.presentation.theme.PollenTheme
import kotlinx.collections.immutable.ImmutableList
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PhenologyScreen(viewModel: PhenologyViewModel = koinViewModel()) {
    val uiState by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    CollectEvents(viewModel.events, snackbarHostState, onRetry = viewModel::loadData)

    if (uiState.form.showDialog) {
        val screenData = uiState.screenData
        if (screenData is LoadState.Loaded) {
            AddPhenologyDialog(
                stages = screenData.data.stages,
                selectedStage = uiState.form.selectedStage,
                comment = uiState.form.comment,
                onStageSelect = viewModel::selectStage,
                onCommentChange = viewModel::setComment,
                onConfirm = viewModel::submit,
                onDismiss = viewModel::dismissDialog,
            )
        }
    }

    Box(Modifier.fillMaxSize()) {
        when (val data = uiState.screenData) {
            is LoadState.Loading -> PhenologySkeleton()
            is LoadState.Failed -> FullScreenError(onRetry = viewModel::loadData)
            is LoadState.Loaded -> PhenologyContent(data.data)
        }

        if (uiState.screenData is LoadState.Loaded) {
            FloatingActionButton(
                onClick = viewModel::showAddDialog,
                shape = CircleShape,
                containerColor = PollenTheme.colors.accent,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить наблюдение")
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun PhenologySkeleton() {
    val colors = PollenTheme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 18.dp),
    ) {
        Box(
            Modifier
                .size(width = 120.dp, height = 28.dp)
                .clip(RoundedCornerShape(6.dp))
                .shimmerEffect(),
        )
        Box(
            Modifier
                .padding(top = 6.dp)
                .size(width = 180.dp, height = 14.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect(),
        )
        Spacer(Modifier.height(18.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(14.dp))
                .shimmerEffect(),
        )
        Spacer(Modifier.height(24.dp))
        Box(
            Modifier
                .size(width = 100.dp, height = 14.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect(),
        )
        Spacer(Modifier.height(10.dp))
        repeat(6) { i ->
            Row(
                modifier = Modifier.padding(bottom = if (i < 5) 16.dp else 0.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .shimmerEffect(),
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Box(
                        Modifier
                            .size(width = 90.dp, height = 16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect(),
                    )
                    Box(
                        Modifier
                            .padding(top = 4.dp)
                            .size(width = 140.dp, height = 12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect(),
                    )
                }
            }
        }
    }
}

@Composable
private fun PhenologyContent(data: PhenologyScreenDataUi) {
    val colors = PollenTheme.colors
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp)
            .padding(top = 18.dp, bottom = 80.dp),
    ) {
        Text(
            text = data.allergenName,
            style = MaterialTheme.typography.displaySmall.copy(fontSize = 24.sp),
            color = colors.ink,
        )
        Text(
            text = data.locationLabel,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = colors.ink3,
            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp),
        )

        if (data.currentStageLabel.isNotEmpty()) {
            CurrentStageCard(
                label = data.currentStageLabel,
                date = data.currentStageDate,
            )
        } else {
            InstructionCard()
        }

        Text(
            text = "ВСЕ СТАДИИ",
            style = MaterialTheme.typography.labelMedium,
            color = colors.ink3,
            modifier = Modifier.padding(top = 24.dp, bottom = 10.dp),
        )

        StagesTimeline(stages = data.stages)
    }
}

@Composable
private fun CurrentStageCard(label: String, date: String) {
    val colors = PollenTheme.colors
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "ТЕКУЩАЯ СТАДИЯ",
                style = MaterialTheme.typography.labelMedium,
                color = colors.ink3,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
                color = colors.ink,
                modifier = Modifier.padding(top = 6.dp),
            )
            if (date.isNotEmpty()) {
                Text(
                    text = "Отметили $date",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = colors.ink3,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun InstructionCard() {
    val colors = PollenTheme.colors
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.card),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Отмечайте стадию цветения, чтобы другие пользователи имели более точную картину пыления",
            style = MaterialTheme.typography.bodyMedium,
            color = colors.ink2,
            modifier = Modifier.padding(16.dp),
        )
    }
}

// ── Timeline ──

@Composable
private fun StagesTimeline(stages: ImmutableList<PhenologyStageUi>) {
    val colors = PollenTheme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        Box(
            modifier = Modifier
                .padding(start = 11.dp, top = 12.dp, bottom = 12.dp)
                .width(1.5.dp)
                .fillMaxHeight()
                .background(colors.line2, RoundedCornerShape(1.dp)),
        )

        Column {
            stages.forEachIndexed { index, stage ->
                StageTimelineItem(
                    stage = stage,
                    isLast = index == stages.lastIndex,
                )
            }
        }
    }
}

@Composable
private fun StageTimelineItem(stage: PhenologyStageUi, isLast: Boolean) {
    val colors = PollenTheme.colors

    Row(
        modifier = Modifier.padding(bottom = if (isLast) 0.dp else 16.dp),
        verticalAlignment = Alignment.Top,
    ) {
        StageIndicator(stage)
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                text = "Стадия №${stage.number}",
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 13.sp),
                fontWeight = if (stage.isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                color = if (stage.isDone || stage.isCurrent) colors.ink else colors.ink3,
            )
            Text(
                text = stage.name,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = colors.ink3,
                modifier = Modifier.padding(top = 1.dp),
            )
        }
    }
}

@Composable
private fun StageIndicator(stage: PhenologyStageUi) {
    val colors = PollenTheme.colors
    when {
        stage.isCurrent -> Box(
            modifier = Modifier
                .size(24.dp)
                .shadow(
                    4.dp,
                    CircleShape,
                    ambientColor = colors.accent.copy(alpha = 0.25f),
                    spotColor = colors.accent.copy(alpha = 0.25f),
                )
                .background(colors.accent, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "${stage.number}",
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
        }
        stage.isDone -> Box(
            modifier = Modifier
                .size(24.dp)
                .background(colors.paper2, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = colors.accent2,
                modifier = Modifier.size(12.dp),
            )
        }
        else -> Box(
            modifier = Modifier
                .size(24.dp)
                .dashedCircleBorder(colors.line),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "${stage.number}",
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.ink3,
            )
        }
    }
}

private fun Modifier.dashedCircleBorder(
    color: Color,
    strokeWidth: Dp = 1.5.dp,
) = this.drawBehind {
    val strokePx = strokeWidth.toPx()
    drawCircle(
        color = color,
        radius = (size.minDimension / 2) - (strokePx / 2),
        style = Stroke(
            width = strokePx,
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(4.dp.toPx(), 3.dp.toPx()),
            ),
        ),
    )
}

// ── Add Observation Dialog ──

@Composable
private fun AddPhenologyDialog(
    stages: ImmutableList<PhenologyStageUi>,
    selectedStage: Int,
    comment: String,
    onStageSelect: (Int) -> Unit,
    onCommentChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = PollenTheme.colors

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(14.dp),
        title = {
            Text("Новое наблюдение", style = MaterialTheme.typography.headlineMedium)
        },
        text = {
            Column {
                Text(
                    text = "ВЫБЕРИТЕ СТАДИЮ",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.ink3,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                stages.forEach { stage ->
                    val isSelected = selectedStage == stage.number
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) colors.accentLight else Color.Transparent)
                            .clickable { onStageSelect(stage.number) }
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(selectedColor = colors.accent),
                        )
                        Column(modifier = Modifier.padding(start = 4.dp)) {
                            Text(
                                text = "${stage.number}. ${stage.name}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                            )
                            Text(
                                text = stage.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.ink3,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = onCommentChange,
                    label = { Text("Написать комментарий к стадии") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Готово", color = colors.accent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Закрыть") }
        },
    )
}
