package id.antasari.mentalityapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.antasari.mentalityapp.R
import id.antasari.mentalityapp.ui.theme.MainGradient
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import id.antasari.mentalityapp.ui.theme.SkyBlue
import id.antasari.mentalityapp.ui.theme.SoftNeonPink

// --- 1. DATA MODEL ---
data class Article(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String, // "Article" or "Psychology"
    val readTime: String,
    val content: String,
    val color: Color
)

// Dummy Data
val articlesDB = listOf(
    // --- KATEGORI: ARTICLES (Tips Sehari-hari & Self-Care) ---
    Article(
        id = "1",
        title = "Why Do I Feel So Tired?",
        subtitle = "It's not just lack of sleep. It might be emotional fatigue.",
        category = "Articles",
        readTime = "3 min",
        color = Color(0xFF81C784), // Green Pastel
        content = """
            Have you ever slept for 8 hours but still woke up exhausted? You might be experiencing emotional burnout, not just physical tiredness.
            
            In psychology, this is often linked to 'Mental Load'. When our brain is constantly processing background stress—like pending assignments, social anxiety, or family issues—it consumes massive energy, even when you're sitting still.
            
            Here are 3 signs you need an emotional break, not just a nap:
            1. You get irritated easily by small things (Hypersensitivity).
            2. You feel detached, numb, or cynical (Depersonalization).
            3. You have trouble focusing or making simple decisions (Brain Fog).
            
            💡 The Fix: 'Active Rest'.
            Scrolling TikTok is NOT rest; it's sensory overload. Try 15 minutes of low-stimulation activity: folding laundry, walking without music, or just staring at the ceiling. Let your brain bore itself to heal.
        """.trimIndent()
    ),
    Article(
        id = "2",
        title = "The Art of Saying 'No'",
        subtitle = "Setting boundaries without feeling guilty.",
        category = "Articles",
        readTime = "4 min",
        color = Color(0xFF64B5F6), // Blue Pastel
        content = """
            "No" is a complete sentence. You don't need to explain yourself, apologize, or make up a lie about being busy.
            
            Many of us are 'People Pleasers' because we confuse being 'Nice' with being 'Kind'.
            - Being Nice: Pleasing others at the expense of your own mental health.
            - Being Kind: Being honest about your capacity so you don't resent them later.
            
            📝 Try this script next time:
            "I appreciate you asking me, but I don't have the emotional capacity for this right now."
            
            It feels scary at first, but people actually respect clear boundaries more than fake "Yes"s.
        """.trimIndent()
    ),
    Article(
        id = "3",
        title = "Imposter Syndrome",
        subtitle = "Feeling like a fraud? You're actually in good company.",
        category = "Articles",
        readTime = "3 min",
        color = Color(0xFF9575CD), // Purple Pastel
        content = """
            Do you ever feel like you don't deserve your achievements? Like you just got lucky, and any minute now, people will find out you're not as smart as they think?
            
            This is called the 'Imposter Phenomenon'. Ironically, it mostly affects high-achieving people.
            
            Psychological fact: The Dunning-Kruger Effect suggests that smart people often underestimate their competence because they realize how much they *don't* know.
            
            💡 Reminder:
            Your feelings are real, but they are not facts. You are in this university/position because you worked for it. You belong here.
        """.trimIndent()
    ),
    Article(
        id = "4",
        title = "Doomscrolling & Envy",
        subtitle = "Why social media makes us feel 'less than'.",
        category = "Articles",
        readTime = "4 min",
        color = Color(0xFFFF8A65), // Orange Pastel
        content = """
            We compare our 'Behind-The-Scenes' (our struggles, messy rooms, insecurities) with everyone else's 'Highlight Reel' (their awards, vacations, best angles).
            
            This is an unfair comparison that our brain isn't evolved to handle.
            
            When you scroll and feel a pang of jealousy, notice it. That jealousy is actually data. It tells you what you value.
            - Jealous of a travel vlog? Maybe you need a break.
            - Jealous of a graduation post? Maybe you crave achievement.
            
            Instead of letting it eat you, use it as a compass. And then, turn off the phone. Real life happens offline.
        """.trimIndent()
    ),

    // --- KATEGORI: PSYCHOLOGY (Teori & Edukasi Emosi) ---
    Article(
        id = "5",
        title = "Emotions 101: Validation",
        subtitle = "Why fighting your feelings makes them stronger.",
        category = "Psychology",
        readTime = "2 min",
        color = Color(0xFFFFB74D), // Yellow-Orange
        content = """
            Validation does NOT mean you agree with the feeling. It simply means you acknowledge the feeling is REAL.
            
            Imagine a child crying because they dropped their ice cream.
            Invalidation: "Stop crying, it's just ice cream." (The child cries harder).
            Validation: "I see you're sad. That was a yummy ice cream." (The child feels heard and calms down).
            
            We do this to ourselves too.
            Instead of saying: "I shouldn't be angry, it's stupid."
            Try saying: "It makes sense that I'm angry because my boundaries were crossed."
            
            What we resist, persists. What we feel, heals.
        """.trimIndent()
    ),
    Article(
        id = "6",
        title = "The Anxiety Loop",
        subtitle = "How thoughts trigger physical reactions (CBT Basics).",
        category = "Psychology",
        readTime = "5 min",
        color = Color(0xFFBA68C8), // Magenta Pastel
        content = """
            Anxiety is often a loop explained by Cognitive Behavioral Therapy (CBT):
            
            1. Trigger: You have an exam tomorrow.
            2. Thought: "What if I fail? I'll be a failure forever." (Catastrophizing).
            3. Emotion: Fear/Panic.
            4. Physical Reaction: Heart races, palms sweat, stomach hurts.
            5. Behavior: Procrastinating studying to avoid the feeling.
            
            To break the loop, you can intervene at the PHYSICAL stage first.
            You cannot 'think' your way out of a panic attack because your logical brain (Prefrontal Cortex) is offline.
            
            Use the 4-7-8 breathing or Cold Water splash first. Calm the body, and the mind will follow.
        """.trimIndent()
    ),
    Article(
        id = "7",
        title = "Cognitive Distortions",
        subtitle = "Lies your brain tells you.",
        category = "Psychology",
        readTime = "4 min",
        color = Color(0xFF4DB6AC), // Teal Pastel
        content = """
            Our brains are wired for survival, not happiness. Sometimes, they develop 'Cognitive Distortions'—mental filters that make reality look worse than it is.
            
            Common ones:
            1. All-or-Nothing Thinking: "If I don't get an A, I failed." (Life is rarely black and white).
            2. Mind Reading: "They didn't text back, they must hate me." (You don't know that. They might be asleep).
            3. Labeling: "I made a mistake" vs "I AM a mistake."
            
            Next time you have a harsh thought, ask yourself: "Is this a fact, or is this a distortion?"
        """.trimIndent()
    ),
    Article(
        id = "8",
        title = "Window of Tolerance",
        subtitle = "Understanding your nervous system's capacity.",
        category = "Psychology",
        readTime = "3 min",
        color = Color(0xFF7986CB), // Indigo Pastel
        content = """
            Imagine your capacity to handle stress is a window.
            
            - Inside the Window: You can handle stress. You are calm, alert, and grounded.
            - Hyper-Arousal (Upstairs): Panic, anxiety, anger, racing thoughts. (Fight/Flight).
            - Hypo-Arousal (Downstairs): Numbness, depression, disconnection, shutting down. (Freeze).
            
            Trauma and chronic stress shrink this window, making us jump to Panic or Shutdown easily.
            
            Grounding techniques (like 5-4-3-2-1) help widen your window or bring you back inside it. It's not about 'fixing' you, it's about regulating your nervous system.
        """.trimIndent()
    )
)

// --- 2. SCREEN: LIST ARTIKEL ---
@Composable
fun ArticleListScreen(navController: NavController, categoryFilter: String = "All") {
    // Filter data berdasarkan kategori (Article vs Psychology)
    val filteredList = if (categoryFilter == "All") articlesDB else articlesDB.filter { it.category == categoryFilter }

    val pageTitle = if (categoryFilter == "Psychology") "Emotions 101" else "Library"
    val pageSubtitle = if (categoryFilter == "Psychology") "Understanding how you tick" else "Gentle reads for your soul"

    Box(modifier = Modifier.fillMaxSize().background(MainGradient)) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Spacer(modifier = Modifier.height(20.dp))

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(40.dp).background(Color.White.copy(0.5f), CircleShape)
                ) {
                    Icon(Icons.Rounded.ArrowBack, null, tint = Color.Gray)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(pageTitle, fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF37474F))
                    Text(pageSubtitle, fontFamily = PoppinsFamily, fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // List Artikel
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredList) { article ->
                    ArticleCard(article) {
                        navController.navigate("article_detail/${article.id}")
                    }
                }
            }
        }
    }
}

// Komponen Card Artikel
@Composable
fun ArticleCard(article: Article, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Kotak Warna/Gambar (Dummy)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(article.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = article.title.take(1),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = article.color
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Label Kategori
                Text(
                    text = article.category.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = article.color,
                    fontFamily = PoppinsFamily,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PoppinsFamily,
                    color = Color(0xFF37474F),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Timer, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(article.readTime, fontSize = 12.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                }
            }
        }
    }
}

// --- 3. SCREEN: DETAIL ARTIKEL (BACA) ---
@Composable
fun ArticleDetailScreen(navController: NavController, articleId: String) {
    val article = articlesDB.find { it.id == articleId } ?: articlesDB[0]

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Image Area (Warna)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(article.color.copy(alpha = 0.2f))
            ) {
                // Tombol Back di atas gambar
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(top = 48.dp, start = 24.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(Icons.Rounded.ArrowBack, null, tint = Color.Black)
                }

                // Judul Besar di dalam Hero
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                ) {
                    Text(
                        text = article.category.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = article.color,
                        letterSpacing = 1.sp,
                        modifier = Modifier.background(Color.White.copy(0.8f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = article.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PoppinsFamily,
                        color = Color(0xFF37474F),
                        lineHeight = 36.sp
                    )
                }
            }

            // Konten Tulisan
            Column(modifier = Modifier.padding(24.dp)) {
                // Metadata
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Timer, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(article.readTime + " read", fontSize = 14.sp, color = Color.Gray, fontFamily = PoppinsFamily)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Rounded.BookmarkBorder, null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Rounded.Share, null, tint = Color.Gray)
                }

                Divider(modifier = Modifier.padding(vertical = 24.dp), color = Color.Gray.copy(0.2f))

                // Isi Artikel
                Text(
                    text = article.content,
                    fontSize = 16.sp,
                    fontFamily = PoppinsFamily,
                    color = Color(0xFF455A64),
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}