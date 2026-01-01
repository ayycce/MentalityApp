package id.antasari.mentalityapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.antasari.mentalityapp.ui.theme.PoppinsFamily

// --- DEFINISI WARNA PASTEL (TAILWIND 400 SCALE) ---
// Ini warna asli dari referensi desain kamu (Soft & Clean)
val PastelPink = Color(0xFFF472B6)   // pink-400
val PastelPurple = Color(0xFFC084FC) // purple-400
val PastelBlue = Color(0xFF60A5FA)   // blue-400
val PastelYellow = Color(0xFFFACC15) // yellow-400
val PastelCyan = Color(0xFF22D3EE)   // cyan-400
val PastelRose = Color(0xFFFB7185)   // rose-400

data class QuoteCard(
    val id: Int,
    val quote: String,
    val subtext: String, // Tambahan subtext kayak di desain
    val gradientColors: List<Color>,
    val pattern: String // dots, waves, circles (Simulasi)
)

@Composable
fun FeedScreen() {
    // Data Quotes dengan Warna Pastel yang Benar
    val cards = remember {
        listOf(
            QuoteCard(
                1,
                "your mental health matters",
                "take a break, you deserve it ✨",
                listOf(PastelPink, PastelPurple, PastelBlue), // Pink -> Purple -> Blue
                "dots"
            ),
            QuoteCard(
                2,
                "healing is not linear",
                "be patient with yourself 💕",
                listOf(PastelYellow, PastelPink, PastelPurple), // Yellow -> Pink -> Purple
                "waves"
            ),
            QuoteCard(
                3,
                "you are doing great",
                "keep going, beautiful soul ⭐",
                listOf(PastelCyan, PastelBlue, PastelPurple), // Cyan -> Blue -> Purple
                "circles"
            ),
            QuoteCard(
                4,
                "it's okay to not be okay",
                "your feelings are valid 🌸",
                listOf(PastelRose, PastelPink, PastelPurple), // Rose -> Pink -> Purple
                "stars"
            )
        )
    }

    // Pager State
    val pagerState = rememberPagerState(pageCount = { cards.size })

    Box(modifier = Modifier.fillMaxSize()) {
        // Vertical Pager (Snap Scroll)
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            FeedItem(card = cards[page])
        }

        // Top Logo (MindTok)
        Text(
            text = "MindTok",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = PoppinsFamily,
            color = Color.White,
            modifier = Modifier
                .padding(top = 40.dp, start = 24.dp)
                .align(Alignment.TopStart)
        )
    }
}

@Composable
fun FeedItem(card: QuoteCard) {
    var isLiked by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(card.gradientColors))
    ) {
        // --- PATTERN OVERLAY (Hiasan Tipis) ---
        // Kita pakai titik-titik putih transparan biar mirip desain Y2K/Dot pattern
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.05f)) // Pattern dummy
        )

        // --- CONTENT TENGAH ---
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Quote Utama (Font Besar & Trendy)
            Text(
                text = card.quote,
                fontSize = 40.sp, // Ukuran besar ala poster
                fontWeight = FontWeight.ExtraBold,
                fontFamily = PoppinsFamily,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 44.sp,
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Subtext (Glassmorphism Pill)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(50))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = card.subtext,
                    fontSize = 16.sp,
                    color = Color.White,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Hiasan Emoji 3D (Floating)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                GlassEmoji("✨")
                GlassEmoji("💫")
                GlassEmoji("🌟")
            }
        }

        // --- TOMBOL AKSI (KANAN) ---
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 120.dp, end = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Like
            FeedActionButton(
                icon = if (isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                text = if (isLiked) "1.2k" else "1.1k",
                tint = if (isLiked) Color(0xFFFF5252) else Color.White, // Merah Soft
                onClick = { isLiked = !isLiked }
            )

            // Save
            FeedActionButton(
                icon = if (isSaved) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                text = "Save",
                tint = if (isSaved) Color(0xFFFFD740) else Color.White, // Kuning Soft
                onClick = { isSaved = !isSaved }
            )

            // Share
            FeedActionButton(
                icon = Icons.Rounded.Share,
                text = "Share",
                tint = Color.White,
                onClick = { /* Share */ }
            )
        }
    }
}

// Komponen Tombol Aksi (Glassmorphism Bulat)
@Composable
fun FeedActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(56.dp) // Sedikit lebih besar
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.25f)) // Glass effect
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.White,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.shadow(4.dp) // Drop shadow biar kebaca
        )
    }
}

// Komponen Emoji Kaca (Hiasan)
@Composable
fun GlassEmoji(emoji: String) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.2f))
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = emoji, fontSize = 28.sp)
    }
}