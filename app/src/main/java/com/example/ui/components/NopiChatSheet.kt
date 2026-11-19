package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeoButton
import com.example.ui.theme.NeoCard
import com.example.ui.theme.NpaCoral
import com.example.ui.theme.NpaCream
import com.example.ui.theme.NpaLavenderSoft
import com.example.ui.theme.NpaMint
import com.example.ui.theme.NpaNavy
import com.example.ui.theme.NpaWhite
import com.example.ui.theme.NpaYellow
import com.example.viewmodel.NopiChatMessage
import com.example.viewmodel.NopiChatRole
import com.example.viewmodel.NopiChatUiState
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NopiChatSheet(
    chatState: NopiChatUiState,
    latestHabitScore: Int?,
    latestScrollSpeedDpPerSec: Float?,
    onDismiss: () -> Unit,
    onSelectRole: (NopiChatRole) -> Unit,
    onResetChat: () -> Unit,
    onInputChange: (String) -> Unit,
    onSend: (String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listState = rememberLazyListState()

    LaunchedEffect(chatState.messages.size) {
        if (chatState.messages.isNotEmpty()) {
            listState.animateScrollToItem(chatState.messages.lastIndex)
        }
    }

    LaunchedEffect(chatState.isLoading) {
        while (chatState.isLoading) {
            delay(140)
            if (chatState.messages.isNotEmpty()) {
                listState.scrollToItem(chatState.messages.lastIndex)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NpaCream,
        contentColor = NpaNavy,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier.testTag("nopi_chat_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .navigationBarsPadding()
                .imePadding()
        ) {
            // Top neo-brutalist header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NpaYellow)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.5.dp, NpaNavy, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    NopiHdAvatar(size = 48.dp)
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Tanya Nopi AI",
                            style = MaterialTheme.typography.titleLarge,
                            color = NpaNavy,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = NpaNavy,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = chatState.role.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = NpaNavy.copy(alpha = 0.78f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = onResetChat,
                    enabled = !chatState.isLoading,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(NpaWhite)
                        .border(2.dp, NpaNavy, CircleShape)
                        .testTag("chat_reset_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Reset percakapan",
                        tint = NpaNavy
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(NpaCoral)
                        .border(2.dp, NpaNavy, CircleShape)
                        .testTag("chat_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Tutup chatbot Nopi",
                        tint = NpaNavy
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.5.dp)
                    .background(NpaNavy)
            )

            // Role selector strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NpaLavenderSoft.copy(alpha = 0.55f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NopiChatRole.entries.forEach { role ->
                    val selected = chatState.role == role
                    Surface(
                        onClick = { onSelectRole(role) },
                        enabled = !chatState.isLoading,
                        shape = CircleShape,
                        color = if (selected) NpaMint else NpaWhite,
                        border = BorderStroke(
                            width = if (selected) 2.5.dp else 1.5.dp,
                            color = NpaNavy
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = 48.dp)
                            .testTag("chat_role_${role.name.lowercase()}")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = role.shortLabel,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold
                                ),
                                color = NpaNavy,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Message history thread
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(chatState.messages, key = { it.id }) { msg ->
                    ChatMessageBubble(message = msg)
                }
            }

            // Starter suggestion chips when conversation is fresh
            if (chatState.messages.size <= 1 && !chatState.isLoading) {
                val scorePrompt = if (latestHabitScore != null && latestScrollSpeedDpPerSec != null) {
                    "Evaluasi hasil Doomscrolling Test-ku: Digital Habit Score $latestHabitScore/100 dengan kecepatan scroll rata-rata ${latestScrollSpeedDpPerSec.roundToInt()} dp/detik. Apa sarannya?"
                } else {
                    "Gimana cara mengurangi kebiasaan doomscrolling sebelum tidur?"
                }
                val starters = listOf(
                    "📊 Evaluasi kebiasaan scroll-ku" to scorePrompt,
                    "⚡ 3 tips taruh HP sekarang" to "Kasih aku 3 langkah cepat biar berhenti main HP sekarang juga!",
                    "🎲 Ide seru bareng teman" to "Beri ide aktivitas seru tanpa HP saat lagi nongkrong bareng teman."
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    starters.forEachIndexed { idx, (label, prompt) ->
                        Surface(
                            onClick = { onSend(prompt) },
                            shape = CircleShape,
                            color = NpaWhite,
                            border = BorderStroke(2.dp, NpaNavy),
                            modifier = Modifier
                                .defaultMinSize(minHeight = 48.dp)
                                .testTag("chat_starter_$idx")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = NpaNavy
                                )
                            }
                        }
                    }
                }
            }

            // Error alert banner
            AnimatedVisibility(visible = chatState.errorMessage != null) {
                NeoCard(
                    containerColor = NpaCoral.copy(alpha = 0.22f),
                    contentPadding = PaddingValues(12.dp),
                    shadowOffset = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .testTag("chat_error_banner")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ErrorOutline,
                            contentDescription = "Kesalahan",
                            tint = NpaNavy
                        )
                        Text(
                            text = chatState.errorMessage.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            color = NpaNavy
                        )
                    }
                }
            }

            // Input bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NpaCream)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = chatState.inputText,
                    onValueChange = onInputChange,
                    placeholder = {
                        Text(
                            text = "Tulis pertanyaan untuk Nopi...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NpaNavy.copy(alpha = 0.5f)
                        )
                    },
                    singleLine = false,
                    maxLines = 3,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = NpaWhite,
                        unfocusedContainerColor = NpaWhite,
                        focusedBorderColor = NpaNavy,
                        unfocusedBorderColor = NpaNavy,
                        focusedTextColor = NpaNavy,
                        unfocusedTextColor = NpaNavy,
                        cursorColor = NpaNavy
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        if (chatState.inputText.isNotBlank() && !chatState.isLoading) {
                            onSend(null)
                        }
                    }),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field")
                )

                val canSend = chatState.inputText.isNotBlank() && !chatState.isLoading
                NeoButton(
                    onClick = { onSend(null) },
                    enabled = canSend,
                    containerColor = NpaMint,
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("chat_send_button")
                ) {
                    Box(
                        modifier = Modifier.size(52.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (chatState.isLoading) {
                            CircularProgressIndicator(
                                strokeWidth = 2.5.dp,
                                color = NpaNavy,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Kirim pesan ke Nopi",
                                tint = NpaNavy,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatMessageBubble(message: NopiChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!message.isUser) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .border(2.dp, NpaNavy, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                NopiHdAvatar(size = 34.dp)
            }
            Spacer(Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (message.isUser) 18.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 18.dp
            ),
            color = if (message.isUser) NpaYellow else NpaWhite,
            border = BorderStroke(2.dp, NpaNavy),
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                if (message.text.isBlank() && message.isStreaming) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            color = NpaNavy,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Nopi sedang mengetik...",
                            style = MaterialTheme.typography.bodySmall,
                            color = NpaNavy.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                        color = NpaNavy
                    )
                    if (message.isStreaming) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "●",
                            style = MaterialTheme.typography.labelSmall,
                            color = NpaCoral
                        )
                    }
                }
            }
        }
    }
}
