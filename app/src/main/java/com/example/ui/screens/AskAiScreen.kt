package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EnergyRepository
import com.example.model.ChatMessage
import com.example.model.ScreenRoute
import com.example.network.GeminiService
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AskAiScreen(
    onNavigate: (ScreenRoute) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var messages by remember { mutableStateOf(EnergyRepository.initialChatMessages) }
    var inputText by remember { mutableStateOf("") }
    var isThinking by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val quickPrompts = listOf(
        "Why did Mombasa sales increase in September?",
        "Show top products by volume",
        "Who are our top customers?",
        "What is Mombasa tank capacity?",
        "Compare regional margins"
    )

    fun sendMessage(text: String) {
        val query = text.trim()
        if (query.isBlank()) return

        val userMsg = ChatMessage(
            id = "usr-${System.currentTimeMillis()}",
            sender = "user",
            text = query,
            timestamp = "Just now"
        )
        messages = messages + userMsg
        inputText = ""
        isThinking = true

        coroutineScope.launch {
            listState.animateScrollToItem(messages.size)
            val response = GeminiService.askAssistant(query)
            val aiMsg = ChatMessage(
                id = "ai-${System.currentTimeMillis()}",
                sender = "ai",
                text = response.answer,
                timestamp = "Just now",
                chips = response.followUpChips
            )
            messages = messages + aiMsg
            isThinking = false
            listState.animateScrollToItem(messages.size)
        }
    }

    Scaffold(
        topBar = {
            Surface(color = GalanaNavy, shadowElevation = 3.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Column {
                            Text(
                                text = "Galana AI Intelligence",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Petroleum Operations Copilot",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GalanaAmber.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Executive Live", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GalanaAmberLight)
                    }
                }
            }
        },
        containerColor = BackgroundLight,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Chat message list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(messages) { msg ->
                    ChatBubbleItem(
                        message = msg,
                        onChipClick = { chipText ->
                            when {
                                chipText.contains("Mombasa", ignoreCase = true) -> onNavigate(ScreenRoute.MOMBASA_DEPOT)
                                chipText.contains("customer", ignoreCase = true) -> onNavigate(ScreenRoute.KEY_CUSTOMERS)
                                chipText.contains("product", ignoreCase = true) -> onNavigate(ScreenRoute.PRODUCT_PERFORMANCE)
                                chipText.contains("day", ignoreCase = true) || chipText.contains("trend", ignoreCase = true) -> onNavigate(ScreenRoute.SALES_TREND)
                                else -> sendMessage(chipText)
                            }
                        }
                    )
                }

                if (isThinking) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = GalanaNavy)
                            Text("Analyzing operational logs & energy datasets...", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }
            }

            // Quick Prompt Chips Row
            Surface(color = Color.White, shadowElevation = 2.dp) {
                Column {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(quickPrompts) { prompt ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .clickable { sendMessage(prompt) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(prompt, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    // Input Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Ask energy analyst...", fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ask_ai_input")
                        )

                        IconButton(
                            onClick = { sendMessage(inputText) },
                            enabled = inputText.isNotBlank() && !isThinking,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (inputText.isNotBlank() && !isThinking) GalanaNavy else Color(0xFFCBD5E1))
                                .testTag("send_ai_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubbleItem(
    message: ChatMessage,
    onChipClick: (String) -> Unit
) {
    val isUser = message.sender == "user"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(GalanaNavy),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = GalanaAmberLight,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Column {
                Card(
                    shape = RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (isUser) 14.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 14.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isUser) GalanaNavy else Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Text(
                        text = message.text,
                        fontSize = 13.sp,
                        color = if (isUser) Color.White else TextPrimary,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                // Follow up suggested chips
                if (!isUser && message.chips.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        message.chips.take(3).forEach { chip ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFFEF3C7))
                                    .clickable { onChipClick(chip) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(chip, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GalanaAmberDark)
                            }
                        }
                    }
                }
            }
        }
    }
}
