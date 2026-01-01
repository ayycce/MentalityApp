package id.antasari.mentalityapp.data

// Model untuk menyimpan data Check-in
data class MoodCheckIn(
    val id: Long = System.currentTimeMillis(),
    val moodIndex: Int,      // 0-4 (Sad - Excited)
    val intensity: Float,    // 1.0 - 5.0
    val promptId: String,    // ID pertanyaan (biar tracking gampang)
    val selectedContext: String? // Jawaban user (null jika skip)
)

// Model untuk Pertanyaan
data class ContextPrompt(
    val id: String,
    val question: String,
    val options: List<String> // Pilihan jawaban (Chips)
)

// 🧠 BANK PERTANYAAN CERDAS (The Brain - Updated)
// Prinsip: 1 Pertanyaan = 1 Napas. Netral & Kontekstual.
object PromptBank {

    // 1. KATEGORI: SAD (0) & UPSET (1)
    // Fokus: Beban, Kelelahan, Validasi Rasa Berat
    private val sadPrompts = listOf(
        ContextPrompt(
            id = "sad_heavy",
            question = "Hal apa yang paling kerasa berat hari ini?",
            options = listOf("Pekerjaan", "Relasi", "Ekspektasi", "Fisik", "Keuangan", "Ga tau")
        ),
        ContextPrompt(
            id = "sad_tired",
            question = "Capeknya lebih ke pikiran atau perasaan?",
            options = listOf("Pikiran Penuh", "Hati Berat", "Fisik Lelah", "Semuanya", "Cuma Bosan")
        ),
        ContextPrompt(
            id = "sad_trigger",
            question = "Ada kejadian kecil yang cukup ngaruh hari ini?",
            options = listOf("Komentar Orang", "Kesalahan Kecil", "Kabar Buruk", "Macet/Telat", "Cuaca")
        ),
        ContextPrompt(
            id = "sad_source",
            question = "Hari ini berat karena diri sendiri atau situasi?",
            options = listOf("Diri Sendiri", "Lingkungan", "Orang Lain", "Nasib", "Ga Jelas")
        )
    )

    // 2. KATEGORI: NEUTRAL (2)
    // Fokus: Observasi, Kestabilan, Rutinitas
    private val neutralPrompts = listOf(
        ContextPrompt(
            id = "neu_vibe",
            question = "Hari ini lebih kerasa datar atau cukup oke?",
            options = listOf("Datar Banget", "Cukup Oke", "Membosankan", "Tenang", "Biasa Aja")
        ),
        ContextPrompt(
            id = "neu_energy",
            question = "Energi kamu hari ini lebih ke habis atau stabil?",
            options = listOf("Stabil", "Agak Habis", "Lowbat", "Penuh", "Naik Turun")
        ),
        ContextPrompt(
            id = "neu_flow",
            question = "Hari ini lewat aja atau ada momen tertentu?",
            options = listOf("Lewat Gitu Aja", "Ada Momen Dikit", "Rutinitas", "Menunggu Besok")
        ),
        ContextPrompt(
            id = "neu_head",
            question = "Kepalamu lagi penuh atau kosong?",
            options = listOf("Kosong", "Lumayan Penuh", "Flowing", "Bengong", "Fokus")
        )
    )

    // 3. KATEGORI: HAPPY (3)
    // Fokus: Pemicu Positif, Syukur Ringan
    private val happyPrompts = listOf(
        ContextPrompt(
            id = "hap_source",
            question = "Apa yang bikin perasaanmu cukup oke hari ini?",
            options = listOf("Makanan Enak", "Istirahat Cukup", "Chat Teman", "Cuaca Bagus", "Hobi")
        ),
        ContextPrompt(
            id = "hap_moment",
            question = "Momen kecil apa yang bikin kamu senyum?",
            options = listOf("Lagu Favorit", "Hewan Lucu", "Jalan-jalan", "Kabar Baik", "Interaksi")
        ),
        ContextPrompt(
            id = "hap_origin",
            question = "Perasaan ini datang dari diri sendiri atau kejadian?",
            options = listOf("Diri Sendiri", "Kejadian", "Orang Lain", "Suasana", "Tuhan")
        ),
        ContextPrompt(
            id = "hap_memory",
            question = "Hal apa yang pengen kamu ingat dari hari ini?",
            options = listOf("Ketenangan", "Tawa", "Rasa Sukses", "Kehangatan", "Pemandangan")
        )
    )

    // 4. KATEGORI: EXCITED (4)
    // Fokus: Momentum, Antusiasme, Progress
    private val excitedPrompts = listOf(
        ContextPrompt(
            id = "exc_spark",
            question = "Hal apa yang paling bikin kamu semangat hari ini?",
            options = listOf("Project Baru", "Mau Ketemu Orang", "Gajian/Uang", "Liburan", "Ide Muncul")
        ),
        ContextPrompt(
            id = "exc_energy",
            question = "Energi ini muncul karena apa?",
            options = listOf("Tidur Cukup", "Kopi/Kafein", "Motivasi", "Dukungan", "Deadline Selesai")
        ),
        ContextPrompt(
            id = "exc_news",
            question = "Ada kabar baik atau progress kecil?",
            options = listOf("Progress Kerja", "Kabar Teman", "Pencapaian Diri", "Belanja", "Lulus/Goal")
        ),
        ContextPrompt(
            id = "exc_part",
            question = "Bagian mana dari hari ini yang paling ngena?",
            options = listOf("Pagi Hari", "Siang Tadi", "Sore Santai", "Malam Ini", "Sepanjang Hari")
        )
    )

    // --- FUNGSI PENGAMBILAN PROMPT ---
    // Mengambil 1 pertanyaan acak SESUAI MOOD yang dipilih
    fun getPromptForMood(moodIndex: Int): ContextPrompt {
        return when (moodIndex) {
            0, 1 -> sadPrompts.random() // Sad (0) & Upset (1)
            2 -> neutralPrompts.random() // Neutral (2)
            3 -> happyPrompts.random()   // Happy (3)
            4 -> excitedPrompts.random() // Excited (4)
            else -> neutralPrompts.random() // Fallback
        }
    }
}