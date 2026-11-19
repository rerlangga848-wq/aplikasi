package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.NpaData
import com.example.data.PersistedNpaState
import com.example.ui.theme.*
import kotlinx.coroutines.flow.StateFlow

@Composable
fun DetoxScreen(
    detoxRunningFlow: StateFlow<Boolean>,
    detoxSecondsFlow: StateFlow<Int>,
    persistedStateFlow: StateFlow<PersistedNpaState>,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val detoxRunning by detoxRunningFlow.collectAsStateWithLifecycle()
    val secs by detoxSecondsFlow.collectAsStateWithLifecycle()
    val persistedState by persistedStateFlow.collectAsStateWithLifecycle()
    val progressBrush = remember { Brush.horizontalGradient(listOf(NpaCoral, NpaYellow, NpaMint)) }
    val currentStage = NpaData.detoxStages.lastOrNull { secs >= it.thresholdSec } ?: NpaData.detoxStages.first()
    val nextStage = NpaData.detoxStages.firstOrNull { it.thresholdSec > secs }
    val progress = if (nextStage != null) {
        ((secs - currentStage.thresholdSec).toFloat() /
                (nextStage.thresholdSec - currentStage.thresholdSec).coerceAtLeast(1)).coerceIn(0f, 1f)
    } else 1f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NpaCream),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column(
                modifier = Modifier.widthIn(max = 560.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "DIGITAL DETOX TIMER",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                    color = NpaCoral
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Taruh HP. Mulai timer.",
                    style = MaterialTheme.typography.headlineLarge,
                    color = NpaNavy,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Lihat hidupmu tumbuh seiring waktu.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NpaNavy.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }

        item {
            NeoCard(
                containerColor = NpaWhite,
                contentPadding = PaddingValues(24.dp),
                modifier = Modifier.widthIn(max = 460.dp).fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(currentStage.emoji, fontSize = 58.sp)

                    Text(
                        text = formatDuration(secs),
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 56.sp, lineHeight = 60.sp),
                        color = NpaNavy
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(currentStage.label, style = MaterialTheme.typography.titleMedium, color = NpaNavy)
                        Text(
                            text = currentStage.desc,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NpaNavy.copy(alpha = 0.65f),
                            textAlign = TextAlign.Center
                        )
                    }

                    if (nextStage != null) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("menuju ${nextStage.label}", style = MaterialTheme.typography.labelSmall, color = NpaNavy.copy(alpha = 0.65f))
                                Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = NpaNavy.copy(alpha = 0.65f))
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(14.dp)
                                    .clip(CircleShape)
                                    .background(NpaCream)
                                    .border(2.dp, NpaNavy, CircleShape)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progress)
                                        .fillMaxHeight()
                                        .clip(CircleShape)
                                        .background(progressBrush)
                                )
                            }
                        }
                    }

                    // Milestones
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        NpaData.detoxStages.forEach { st ->
                            val reached = secs >= st.thresholdSec
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.alpha(if (reached) 1f else 0.3f)
                            ) {
                                Text(st.shortEmoji, fontSize = 18.sp)
                                Text(
                                    text = if (st.thresholdSec >= 60) "${st.thresholdSec / 60}m" else "0",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = NpaNavy
                                )
                            }
                        }
                    }

                    // Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (!detoxRunning) {
                            NeoButton(
                                onClick = onStart,
                                containerColor = NpaCoral,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("detox_start_button")
                            ) {
                                Text(
                                    text = if (secs > 0) "▶ Lanjut" else "▶ Mulai",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            NeoButton(
                                onClick = onPause,
                                containerColor = NpaNavy,
                                contentColor = NpaCream,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("detox_pause_button")
                            ) {
                                Text(
                                    text = "⏸ Pause & Simpan",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        NeoButton(
                            onClick = onReset,
                            containerColor = NpaWhite,
                            modifier = Modifier.testTag("detox_reset_button")
                        ) {
                            Text("↺", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
        }

        // Persisted personal detox progress summary
        item {
            Row(
                modifier = Modifier.widthIn(max = 460.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NeoCard(
                    containerColor = NpaMint,
                    contentPadding = PaddingValues(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column {
                        Text("Rekor Terlama", style = MaterialTheme.typography.labelSmall, color = NpaNavy.copy(alpha = 0.7f))
                        Text(
                            text = formatDuration(persistedState.bestDetoxSeconds),
                            style = MaterialTheme.typography.headlineMedium,
                            color = NpaNavy
                        )
                    }
                }
                NeoCard(
                    containerColor = NpaYellow,
                    contentPadding = PaddingValues(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column {
                        Text("Total Sesi", style = MaterialTheme.typography.labelSmall, color = NpaNavy.copy(alpha = 0.7f))
                        Text(
                            text = "${persistedState.detoxSessionsCount}× (${formatDuration(persistedState.totalDetoxSeconds)})",
                            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 18.sp),
                            color = NpaNavy
                        )
                    }
                }
            }
        }
    }
}

private fun formatDuration(totalSec: Int): String {
    val m = totalSec / 60
    val s = totalSec % 60
    return "%02d:%02d".format(m, s)
}
