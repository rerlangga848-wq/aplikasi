package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NpaData
import com.example.data.TeamMember
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun TeamScreen(modifier: Modifier = Modifier) {
    var wavingIndex by remember { mutableStateOf<Int?>(null) }
    val teamRows = remember { NpaData.team.chunked(2) }

    LaunchedEffect(wavingIndex) {
        if (wavingIndex != null) {
            delay(1400)
            wavingIndex = null
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
        item(key = "team_header", contentType = "header") {
            Column(
                modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TIM NPA",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
                    color = NpaCoral
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Manusia di balik gerakan ini.",
                    style = MaterialTheme.typography.headlineLarge,
                    color = NpaNavy,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Klik foto siapa saja buat nyapa! 👋",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NpaNavy.copy(alpha = 0.7f)
                )
            }
        }

        itemsIndexed(
            items = teamRows,
            key = { rowIdx, _ -> "team_row_$rowIdx" },
            contentType = { _, _ -> "team_row" }
        ) { rowIdx, pair ->
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.widthIn(max = 680.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                pair.forEachIndexed { colIdx, member ->
                    val absoluteIndex = rowIdx * 2 + colIdx
                    TeamMemberCard(
                        member = member,
                        isWaving = wavingIndex == absoluteIndex,
                        onTapPhoto = { wavingIndex = absoluteIndex },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (pair.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TeamMemberCard(
    member: TeamMember,
    isWaving: Boolean,
    onTapPhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    NeoCard(
        containerColor = NpaWhite,
        contentPadding = PaddingValues(0.dp),
        onClick = onTapPhoto,
        modifier = modifier
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .background(member.color.copy(alpha = 0.35f))
            ) {
                if (member.photoRes != null) {
                    val photoPainter = rememberCrispBitmapPainter(
                        resId = member.photoRes,
                        maxDimPx = 560,
                        placeholderColor = member.color.copy(alpha = 0.35f)
                    )
                    Image(
                        painter = photoPainter,
                        contentDescription = "Foto ${member.name}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(member.color)
                                .border(2.5.dp, NpaNavy, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = member.name.first().toString(),
                                style = MaterialTheme.typography.headlineLarge,
                                color = NpaNavy
                            )
                        }
                    }
                }

                // Crisp bottom scrim only behind role badge (keeps portrait sharp & vivid)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, NpaNavy.copy(alpha = 0.28f))
                            )
                        )
                )

                // Waving hand on tap
                androidx.compose.animation.AnimatedVisibility(
                    visible = isWaving,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    Text("👋", fontSize = 28.sp)
                }

                // Role pill
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                        .clip(CircleShape)
                        .background(member.color)
                        .border(2.dp, NpaNavy, CircleShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = member.role,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = NpaNavy,
                        maxLines = 1
                    )
                }
            }

            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
                    color = NpaNavy
                )
                Text(
                    text = member.bio,
                    style = MaterialTheme.typography.labelSmall,
                    color = NpaNavy.copy(alpha = 0.72f)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(NpaNavy.copy(alpha = 0.07f))
                            .border(1.5.dp, NpaNavy.copy(alpha = 0.18f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "IG ${member.ig}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = NpaNavy
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(NpaNavy.copy(alpha = 0.07f))
                            .border(1.5.dp, NpaNavy.copy(alpha = 0.18f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "in ${member.linkedin}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = NpaNavy
                        )
                    }
                }
            }
        }
    }
}
