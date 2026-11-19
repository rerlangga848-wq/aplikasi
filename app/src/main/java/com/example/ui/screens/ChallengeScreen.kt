package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.NopiPose
import com.example.data.NpaData
import com.example.data.PersistedNpaState
import com.example.ui.components.NopiMascot
import com.example.ui.theme.*
import com.example.viewmodel.ChallengeUiState
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChallengeScreen(
    challengeStateFlow: StateFlow<ChallengeUiState>,
    persistedStateFlow: StateFlow<PersistedNpaState>,
    onSelectChallenge: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onSubmitPledge: () -> Unit,
    onResetPledge: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by challengeStateFlow.collectAsStateWithLifecycle()
    val persistedState by persistedStateFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val active = remember(uiState.selectedChallengeId) {
        NpaData.challenges.find { it.id == uiState.selectedChallengeId }
    }
    val displayDate = remember(persistedState.pledgeDate) {
        persistedState.pledgeDate.ifBlank {
            SimpleDateFormat("d MMMM yyyy", Locale.forLanguageTag("id-ID")).format(Date())
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NpaCream),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "NPA CHALLENGE",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                    color = NpaCoral
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Pilih durasi challengemu.",
                    style = MaterialTheme.typography.headlineLarge,
                    color = NpaNavy,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Tidak ada yang terlalu kecil. Satu hari pun udah dimulai.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NpaNavy.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Challenge cards
        item {
            Column(
                modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NpaData.challenges.forEach { ch ->
                    val isActive = uiState.selectedChallengeId == ch.id
                    NeoCard(
                        containerColor = if (isActive) ch.bg else NpaWhite,
                        shadowOffset = if (isActive) 6.dp else 4.dp,
                        onClick = { onSelectChallenge(ch.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("challenge_option_${ch.id}")
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(ch.emoji, fontSize = 30.sp)
                                NeoPill(containerColor = NpaWhite.copy(alpha = 0.65f)) {
                                    Text(ch.label, style = MaterialTheme.typography.labelSmall, color = NpaNavy)
                                }
                            }
                            Text(ch.title, style = MaterialTheme.typography.headlineMedium, color = NpaNavy)
                            Text(ch.desc, style = MaterialTheme.typography.bodyMedium, color = NpaNavy.copy(alpha = 0.85f))
                        }
                    }
                }
            }
        }

        if (active != null) {
            item {
                Box(modifier = Modifier.widthIn(max = 520.dp).fillMaxWidth()) {
                    if (!uiState.pledged) {
                        NeoCard(containerColor = NpaWhite) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                NopiMascot(pose = NopiPose.POINT, size = 80.dp)
                                Text(
                                    text = "\"${active.nopiLine}\"",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = NpaNavy,
                                    textAlign = TextAlign.Center
                                )
                                OutlinedTextField(
                                    value = uiState.pledgerName,
                                    onValueChange = onNameChange,
                                    label = { Text("Masukkan namamu (opsional)") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NpaNavy,
                                        unfocusedBorderColor = NpaNavy,
                                        focusedLabelColor = NpaNavy,
                                        focusedTextColor = NpaNavy,
                                        unfocusedTextColor = NpaNavy
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("pledge_name_input")
                                )
                                NeoButton(
                                    onClick = onSubmitPledge,
                                    containerColor = NpaCoral,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("submit_pledge_button")
                                ) {
                                    Text(
                                        text = "✊ Buat Pledge Card",
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            NeoCard(
                                gradient = Brush.linearGradient(active.gradientColors),
                                contentPadding = PaddingValues(24.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    NeoPill(containerColor = NpaWhite.copy(alpha = 0.72f)) {
                                        Text("NPA PLEDGE CARD", style = MaterialTheme.typography.labelSmall, color = NpaNavy)
                                    }
                                    Text(active.emoji, fontSize = 38.sp)
                                    Text(
                                        text = active.title,
                                        style = MaterialTheme.typography.displayMedium,
                                        color = NpaNavy,
                                        textAlign = TextAlign.Center
                                    )
                                    if (uiState.pledgerName.isNotBlank()) {
                                        Text(
                                            text = "oleh ${uiState.pledgerName}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = NpaNavy.copy(alpha = 0.85f)
                                        )
                                    }
                                    Text(
                                        text = "Dimulai $displayDate",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NpaNavy.copy(alpha = 0.65f)
                                    )
                                    Text(
                                        text = "\"${active.tagline}\"",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontStyle = FontStyle.Italic
                                        ),
                                        color = NpaNavy,
                                        textAlign = TextAlign.Center
                                    )
                                    NopiMascot(pose = NopiPose.WIN, size = 76.dp)
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                NeoButton(
                                    onClick = {
                                        val shareText = buildString {
                                            append("Aku mulai ${active.title}! ")
                                            if (uiState.pledgerName.isNotBlank()) append("(${uiState.pledgerName}) ")
                                            append("\"${active.tagline}\" — Dimulai $displayDate #NPAChallenge #NoPhonesAround")
                                        }
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_SUBJECT, "NPA Pledge Card")
                                            putExtra(Intent.EXTRA_TEXT, shareText)
                                        }
                                        context.startActivity(Intent.createChooser(intent, "Bagikan Pledge Card"))
                                    },
                                    containerColor = NpaYellow,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "↗ Share Pledge",
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                                NeoButton(
                                    onClick = onResetPledge,
                                    containerColor = NpaWhite,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "↺ Ganti Challenge",
                                        style = MaterialTheme.typography.labelLarge,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
