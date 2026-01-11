package id.antasari.mentalityapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import id.antasari.mentalityapp.ui.theme.SoftNeonPink
import id.antasari.mentalityapp.ui.theme.SkyBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Data Chat
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIChatScreen(navController: NavController) {
    // State Pesan
    val messages = remember { mutableStateListOf<ChatMessage>() }
    var inputText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current

    // Pesan sapaan awal
    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages.add(ChatMessage(text = "Hi there! I'm Geny. 👋\nI'm here to listen without judgment. How are you feeling today?", isUser = false))
        }
    }

    // Auto-scroll ke bawah
    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size + (if (isTyping) 1 else 0))
        }
    }

    Scaffold(
        // 1. HEADER (Fixed di Atas)
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBack, null, tint = Color(0xFF37474F))
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFFA020F0), SoftNeonPink))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text("Geny AI", fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF37474F))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).background(Color(0xFF4CAF50), CircleShape))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Always here for you", fontFamily = PoppinsFamily, fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            }
        },

        // 2. INPUT BAR (Fixed di Bawah - Otomatis handle Keyboard)
        bottomBar = {
            Surface(
                color = Color.White,
                tonalElevation = 10.dp,
                shadowElevation = 10.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding() // Handle gesture bar HP
                        .padding(16.dp)
                        .imePadding(), // 🔥 INI KUNCINYA: Naik pas di atas keyboard
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Kolom Input Custom
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color(0xFFF0F2F5))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        if (inputText.isEmpty()) {
                            Text("Type a message...", color = Color.Gray, fontSize = 14.sp, fontFamily = PoppinsFamily)
                        }
                        BasicTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            textStyle = TextStyle(
                                fontFamily = PoppinsFamily,
                                fontSize = 14.sp,
                                color = Color.Black
                            ),
                            cursorBrush = SolidColor(SoftNeonPink),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = {
                                if (inputText.isNotBlank()) {
                                    sendMessage(inputText, messages) { isTyping = it }
                                    inputText = ""
                                    // focusManager.clearFocus() // JANGAN clear focus biar keyboard gak turun naik
                                }
                            }),
                            maxLines = 4
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Tombol Kirim
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                sendMessage(inputText, messages) { isTyping = it }
                                inputText = ""
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                brush = if (inputText.isNotBlank())
                                    Brush.linearGradient(listOf(Color(0xFFA020F0), SoftNeonPink))
                                else SolidColor(Color.LightGray),
                                shape = CircleShape
                            )
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.Send, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        },
        containerColor = Color(0xFFF7F9FC)
    ) { padding ->
        // 3. KONTEN CHAT (Di Tengah)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding) // Padding otomatis dari Scaffold (header & footer)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { msg ->
                    ChatBubbleModern(msg)
                }

                if (isTyping) {
                    item {
                        TypingBubbleAnim()
                    }
                }
            }
        }
    }
}

// --- LOGIKA & KOMPONEN LAINNYA TETAP SAMA ---

fun sendMessage(text: String, messages: MutableList<ChatMessage>, setTyping: (Boolean) -> Unit) {
    messages.add(ChatMessage(text = text, isUser = true))
    setTyping(true)
    val response = getSmartResponse(text)
    kotlinx.coroutines.GlobalScope.launch {
        delay(2000)
        setTyping(false)
        messages.add(ChatMessage(text = response, isUser = false))
    }
}

fun getSmartResponse(input: String): String {
    val lower = input.lowercase()
    return when {
        lower.contains("hello") || lower.contains("hi") -> "Hello there! How can I support you today? ✨"
        lower.contains("sad") || lower.contains("cry") -> "I'm so sorry you're feeling this way. It's okay to let it out. I'm here with you. 🫂"
        lower.contains("anxious") || lower.contains("worry") -> "Anxiety is tough. Have you tried the Breathing tool in the Explore menu? It might help calm your nerves. 🌬️"
        lower.contains("tired") || lower.contains("exhausted") -> "Rest is productive too. Please be gentle with yourself today. 🌙"
        lower.contains("thank") -> "You're very welcome! I'm always here if you need a friend. 💖"
        else -> "I hear you. Thank you for sharing that with me. Tell me more? 👂"
    }
}

@Composable
fun ChatBubbleModern(message: ChatMessage) {
    val isUser = message.isUser
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFFA020F0), SoftNeonPink))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .shadow(
                        elevation = if (isUser) 4.dp else 1.dp,
                        shape = RoundedCornerShape(
                            topStart = 20.dp, topEnd = 20.dp,
                            bottomStart = if (isUser) 20.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 20.dp
                        ),
                        spotColor = if (isUser) SoftNeonPink else Color.Black.copy(0.1f)
                    )
                    .background(
                        brush = if (isUser)
                            Brush.linearGradient(listOf(Color(0xFFA020F0), SoftNeonPink))
                        else
                            SolidColor(Color.White),
                        shape = RoundedCornerShape(
                            topStart = 20.dp, topEnd = 20.dp,
                            bottomStart = if (isUser) 20.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 20.dp
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = message.text,
                    color = if (isUser) Color.White else Color(0xFF37474F),
                    fontFamily = PoppinsFamily,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message.timestamp,
                fontSize = 10.sp,
                color = Color.Gray.copy(0.8f),
                fontFamily = PoppinsFamily,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
fun TypingBubbleAnim() {
    Row(verticalAlignment = Alignment.Bottom) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFFA020F0), SoftNeonPink))),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 4.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                JumpingDot(delay = 0)
                JumpingDot(delay = 200)
                JumpingDot(delay = 400)
            }
        }
    }
}

@Composable
fun JumpingDot(delay: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "dot")
    val dy by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -5f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0f at delay using LinearEasing
                -5f at delay + 300 using LinearEasing
                0f at delay + 600 using LinearEasing
            },
            repeatMode = RepeatMode.Restart
        ), label = "dy"
    )
    Box(modifier = Modifier.size(6.dp).offset(y = dy.dp).background(Color.Gray, CircleShape))
}