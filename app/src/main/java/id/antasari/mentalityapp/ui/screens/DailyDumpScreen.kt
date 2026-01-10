package id.antasari.mentalityapp.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import id.antasari.mentalityapp.ui.navigation.Screen
import id.antasari.mentalityapp.ui.theme.MainGradient
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import id.antasari.mentalityapp.ui.theme.SoftNeonPink
import id.antasari.mentalityapp.ui.viewmodel.MoodViewModel
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.Search
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp



// Helper untuk bikin file foto sementara
fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName,
        ".jpg",
        externalCacheDir
    )
}
@Composable
fun DailyDumpScreen(
    navController: NavController,
    viewModel: MoodViewModel
) {
    // 1. STATE & DATA
    var text by remember { mutableStateOf("") }

// 🔥 UBAH 1: Data Asli & Logika Search
    val originalJournalList by viewModel.activeJournals.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    var isSearchActive by remember { mutableStateOf(false) }

// Filter data berdasarkan ketikan user
    val filteredJournals = remember(searchQuery, originalJournalList) {
        if (searchQuery.isBlank()) {
            originalJournalList
        } else {
            originalJournalList.filter { journal ->
                journal.title.contains(searchQuery, ignoreCase = true) ||
                        journal.content.contains(searchQuery, ignoreCase = true)
            }
        }
    }


    // 🔥 PENTING: State Foto harus di sini, jangan di dalam Card!
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    // Animasi Burn
    var isBurning by remember { mutableStateOf(false) }

    // Pop-up Dialog Judul
    var showTitleDialog by remember { mutableStateOf(false) }
    var titleInput by remember { mutableStateOf("") }

    // 2. STATE KAMERA
    val context = LocalContext.current
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // LAUNCHER UNTUK BUKA GALERI
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        if (uris.isNotEmpty()) {
            // 🔥 LOGIKA APPEND: Tambahkan yang baru ke list yang lama
            selectedImageUris = selectedImageUris + uris
        }
    }

    // 4. LAUNCHER KAMERA 📸
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempPhotoUri != null) {
            // Masukkan hasil foto kamera ke list
            selectedImageUris = selectedImageUris + tempPhotoUri!!
        }
    }

    // 1. TENTUKAN TINGGI CARD (DETERMINISTIK)
    val targetHeight = if (selectedImageUris.isNotEmpty()) 380.dp else 280.dp
    val cardHeight by animateDpAsState(
        targetValue = targetHeight,
        label = "cardHeightAnimation"
    )


    LaunchedEffect(isBurning) {
        if (isBurning) {
            delay(2500)
            text = ""
            selectedImageUris = emptyList()
            isBurning = false
        }
    }

    val saveButtonBrush = Brush.horizontalGradient(listOf(Color(0xFFD6A4FF), Color(0xFFFF85A2)))
    val cardColors =
        listOf(Color(0xFFFFF0F5), Color(0xFFE0F7FA), Color(0xFFF3E5F5), Color(0xFFFFF8E1))

    Box(modifier = Modifier.fillMaxSize().background(MainGradient)) {

        // --- LAYOUT UTAMA: STAGGERED GRID (MASONRY) ---
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2), // 2 Kolom
            verticalItemSpacing = 16.dp,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(
                top = 40.dp,
                start = 24.dp,
                end = 24.dp,
                bottom = 120.dp
            ),
            modifier = Modifier.fillMaxSize()
        ) {

            // ITEM 1: HEADER & INPUT (Full Span)
            item(span = StaggeredGridItemSpan.FullLine) {
                Column {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Vent Here",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = SoftNeonPink,
                                fontFamily = PoppinsFamily
                            )
                            Text(
                                text = "Let it all out, no judgment",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontFamily = PoppinsFamily
                            )
                        }

                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // UI CARD
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(cardHeight), // 🔥 TINGGI EKSPLISIT SESUAI STATE
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            // --- BAGIAN 1: FOTO (Hanya muncul jika ada foto) ---
                            AnimatedVisibility(visible = selectedImageUris.isNotEmpty()) {
                                LazyRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(selectedImageUris) { uri ->
                                        Box(
                                            modifier = Modifier.width(80.dp).fillMaxHeight()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color.Gray.copy(0.1f))
                                        ) {
                                            AsyncImage(
                                                model = uri,
                                                contentDescription = null,
                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            IconButton(
                                                onClick = {
                                                    selectedImageUris =
                                                        selectedImageUris.toMutableList()
                                                            .apply { remove(uri) }
                                                },
                                                modifier = Modifier.align(Alignment.TopEnd)
                                                    .padding(2.dp).size(20.dp)
                                                    .background(Color.Black.copy(0.5f), CircleShape)
                                            ) {
                                                Icon(
                                                    Icons.Rounded.Close,
                                                    "Remove",
                                                    tint = Color.White,
                                                    modifier = Modifier.padding(2.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // --- BAGIAN 2: INPUT TEKS (AUTO SCROLL & SCROLLBAR CHROME) ---
                            val scrollState = rememberScrollState()
                            val bringIntoViewRequester = remember { BringIntoViewRequester() }
                            val coroutineScope = rememberCoroutineScope()

                            Box(
                                modifier = Modifier
                                    .weight(1f) // 🔥 AMBIL SISA RUANG YANG ADA
                                    .fillMaxWidth()
                                    .simpleVerticalScrollbar(scrollState) // Scrollbar di kanan
                                    .verticalScroll(scrollState)
                            ) {
                                if (text.isEmpty()) {
                                    Text(
                                        text = "What's messy in your head right now?",
                                        color = Color.LightGray,
                                        fontSize = 16.sp,
                                        fontFamily = PoppinsFamily
                                    )
                                }

                                BasicTextField(
                                    value = text,
                                    onValueChange = { text = it },
                                    textStyle = TextStyle(
                                        fontFamily = PoppinsFamily,
                                        fontSize = 16.sp,
                                        color = Color.Black
                                    ),
                                    // Logika Auto Scroll ke Kursor
                                    onTextLayout = { textLayoutResult ->
                                        val cursorRect = textLayoutResult.getCursorRect(text.length)
                                        coroutineScope.launch {
                                            bringIntoViewRequester.bringIntoView(cursorRect)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 8.dp) // Jarak biar ga ketutup scrollbar
                                        .bringIntoViewRequester(bringIntoViewRequester)
                                )
                            }

                            // --- BAGIAN 3: FOOTER ---
                            Column(modifier = Modifier.fillMaxWidth()) {
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                    thickness = 0.5.dp,
                                    color = Color.LightGray.copy(0.5f)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "${text.length} chars",
                                        fontSize = 10.sp,
                                        color = Color.Gray,
                                        fontFamily = PoppinsFamily
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        IconButton(
                                            onClick = {
                                                val file = context.createImageFile()
                                                val uri = FileProvider.getUriForFile(
                                                    context,
                                                    "${context.packageName}.provider",
                                                    file
                                                )
                                                tempPhotoUri = uri
                                                cameraLauncher.launch(uri)
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Rounded.CameraAlt,
                                                "Take Photo",
                                                tint = SoftNeonPink
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(
                                            onClick = {
                                                galleryLauncher.launch(
                                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                )
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Rounded.AddPhotoAlternate,
                                                "Gallery",
                                                tint = SoftNeonPink
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.weight(1f).height(50.dp)
                                .clip(RoundedCornerShape(50)).background(saveButtonBrush)
                                .clickable {
                                    if (text.isNotBlank() || selectedImageUris.isNotEmpty()) {
                                        showTitleDialog = true
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Rounded.Save,
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Save to Journal",
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = PoppinsFamily
                                )
                            }
                        }
                        Box(
                            modifier = Modifier.weight(1f).height(50.dp)
                                .clip(RoundedCornerShape(50))
                                .border(1.dp, Color(0xFFFF7043), RoundedCornerShape(50))
                                .clickable {
                                    if (text.isNotEmpty() || selectedImageUris.isNotEmpty()) {
                                        isBurning = true
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Rounded.LocalFireDepartment,
                                    null,
                                    tint = Color(0xFFFF7043),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Burn It",
                                    color = Color(0xFFFF7043),
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = PoppinsFamily
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    if (originalJournalList.isNotEmpty()) {

                        // Gunakan Animasi biar perpindahannya halus (Soft Aesthetic)
                        androidx.compose.animation.Crossfade(targetState = isSearchActive, label = "SearchAnimation") { active ->

                            if (active) {
                                // ------------------------------------------------
                                // MODE 1: TAMPILAN SEARCH BAR (Judul Hilang)
                                // ------------------------------------------------
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = {
                                        Text("Search memories...", color = Color.Gray.copy(0.7f), fontFamily = PoppinsFamily, fontSize = 14.sp)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            androidx.compose.material.icons.Icons.Rounded.Search,
                                            contentDescription = null,
                                            tint = SoftNeonPink
                                        )
                                    },
                                    trailingIcon = {
                                        // Tombol CLOSE (X) -> Kembali ke Mode Judul
                                        IconButton(onClick = {
                                            isSearchActive = false // Matikan mode cari
                                            searchQuery = ""       // Kosongkan teks pencarian
                                        }) {
                                            Icon(Icons.Rounded.Close, null, tint = Color.Gray)
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp), // Sedikit lebih tinggi biar enak ditekan
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = SoftNeonPink,
                                        unfocusedBorderColor = SoftNeonPink, // Biar tetap pink
                                        cursorColor = SoftNeonPink,
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White
                                    ),
                                    textStyle = TextStyle(
                                        fontFamily = PoppinsFamily,
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
                                )
                            } else {
                                // ------------------------------------------------
                                // MODE 2: TAMPILAN JUDUL + ICON SEARCH
                                // ------------------------------------------------
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween, // Judul kiri, Icon kanan
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Judul dan Sub-judul
                                    Column(modifier = Modifier.weight(1f)) { // Weight biar dia ambil sisa ruang kiri
                                        Text(
                                            "Your Journal",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = PoppinsFamily
                                        )
                                        Text(
                                            "Previous thoughts & reflections",
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                            fontFamily = PoppinsFamily
                                        )
                                    }

                                    // Tombol Icon Kaca Pembesar
                                    IconButton(
                                        onClick = { isSearchActive = true }, // Aktifkan mode cari
                                        modifier = Modifier
                                            .background(Color.White, CircleShape) // Background putih bulat
                                            .border(1.dp, Color.LightGray.copy(0.3f), CircleShape) // Garis tipis
                                            .size(40.dp)
                                    ) {
                                        Icon(
                                            androidx.compose.material.icons.Icons.Rounded.Search,
                                            contentDescription = "Search",
                                            tint = SoftNeonPink
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // ITEM LIST (Grid Masonry)
            itemsIndexed(filteredJournals) { index, journal ->
                val bgColor = cardColors[index % cardColors.size]
                val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
                val dateStr = dateFormat.format(Date(journal.timestamp))

                // Logic Judul Default
                val displayTitle =
                    if (journal.title.isBlank()) "Journal #${originalJournalList.size - index}" else journal.title

                JournalItemCard(
                    title = displayTitle,
                    content = journal.content,
                    date = dateStr,
                    bgColor = bgColor,
                    onClick = {
                        // Aksi kalau KARTU diklik -> Buka Detail
                        viewModel.selectedJournal = journal
                        navController.navigate(Screen.JournalDetail.route)
                    },
                    // 🔥 UPDATE DI SINI: PANGGIL FUNGSI HAPUS 🔥
                    onDeleteClick = {
                        viewModel.deleteJournal(journal)
                    },

                    onArchiveClick = {
                        viewModel.toggleArchiveStatus(journal)
                    }

                )
            }
        }

        // --- DIALOG JUDUL ---
        if (showTitleDialog) {
            AlertDialog(
                onDismissRequest = { showTitleDialog = false },
                containerColor = Color.White,
                title = {
                    Text(
                        "Give it a title?",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = SoftNeonPink

                    )
                },
                text = {
                    Column {
                        Text(
                            "Optional. We'll generate a default one if you skip.",
                            fontFamily = PoppinsFamily,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            placeholder = { Text("Enter title...", color = Color.LightGray) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SoftNeonPink,
                                cursorColor = SoftNeonPink,

                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.addJournal(
                                context = context,           // 1. Kirim Context (buat izin simpan file)
                                title = titleInput,          // 2. Kirim Judul
                                content = text,              // 3. Kirim Isi curhatan
                                uris = selectedImageUris
                            )     // 4. Kirim List Foto

                            // 🔥 RESET UI SETELAH SAVE
                            text = ""                    // Kosongkan teks
                            titleInput = ""              // Kosongkan judul
                            selectedImageUris = emptyList() // Kosongkan list foto (PENTING!)

                            showTitleDialog = false      // Tutup dialog
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SoftNeonPink),
                        shape = RoundedCornerShape(8.dp)
                    ) { Text("Save", fontFamily = PoppinsFamily, color = Color.White) }
                },
                dismissButton = {
                    TextButton(onClick = { showTitleDialog = false }) {
                        Text("Cancel", fontFamily = PoppinsFamily, color = Color.Gray)
                    }
                }
            )
        }

        // 🔥 LOGIKA PEMBAKARAN (Tambahkan ini)
        LaunchedEffect(isBurning) {
            if (isBurning) {
                delay(3000) // Tunggu 3 detik (durasi animasi api)

                // Setelah 3 detik, hapus semua data
                text = ""
                titleInput = ""
                selectedImageUris = emptyList() // <--- FOTO JUGA DIHAPUS

                isBurning = false // Matikan animasi
            }
        }

        // --- ANIMASI BURN ---
        AnimatedVisibility(
            isBurning,
            enter = fadeIn(tween(800)),
            exit = fadeOut(tween(800)),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(0.9f)))
        }
        if (isBurning) {
            val scale by animateFloatAsState(
                if (isBurning) 1f else 0f,
                spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
            )
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.scale(scale)
                ) {
                    Icon(
                        Icons.Rounded.LocalFireDepartment,
                        null,
                        tint = Color(0xFFFF5722),
                        modifier = Modifier.size(150.dp)
                    )
                    Text(
                        "Letting go...",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PoppinsFamily
                    )
                }
            }
        }
    }
}

    // 🔥 CARD SESUAI DESAIN UPLOAD + TITIK TIGA CLICKABLE 🔥
    @Composable
    fun JournalItemCard(
        title: String,
        content: String,
        date: String,
        bgColor: Color,
        modifier: Modifier = Modifier,
        onClick: () -> Unit,
        onDeleteClick: () -> Unit,
        onArchiveClick: () -> Unit,
        archiveLabel: String = "Archive"

    ) {
        // State untuk Popup Menu
        var showMenu by remember { mutableStateOf(false) }

        Card(
            modifier = modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 100.dp) // Minimal tinggi, tapi bisa memanjang (Masonry)
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = bgColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    // Judul
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = PoppinsFamily,
                        color = Color.Black.copy(0.9f),
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    // Isi
                    Text(
                        text = content,
                        fontSize = 12.sp,
                        color = Color.Black.copy(0.7f),
                        fontFamily = PoppinsFamily,
                        lineHeight = 16.sp,
                        maxLines = 5, overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(date, fontSize = 10.sp, color = Color.Gray, fontFamily = PoppinsFamily)

                    // --- BAGIAN POPUP MENU ---
                    Box { // Bungkus dalam Box agar menu muncul pas di icon
                        IconButton(
                            onClick = { showMenu = true }, // Klik titik 3 -> Buka Menu
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.MoreHoriz,
                                contentDescription = "More Options",
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Menu Popup Kecil
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }, // Klik luar -> Tutup
                            containerColor = Color.White,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            // 🔥 2. MENU ARCHIVE (BARU)
                            DropdownMenuItem(
                                text = {
                                    Text(archiveLabel, fontFamily = PoppinsFamily)
                                },
                                onClick = {
                                    showMenu = false
                                    onArchiveClick() // Panggil aksi arsip
                                },
                                leadingIcon = {
                                    Icon(
                                        androidx.compose.material.icons.Icons.Rounded.Inventory2,
                                        null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            )

                            // Divider Tipis (Opsional, biar rapi)
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = Color.LightGray.copy(0.5f)
                            )

                            // MENU DELETE (YANG LAMA)
                            DropdownMenuItem(
                                text = {
                                    Text("Delete", fontFamily = PoppinsFamily, color = Color.Red)
                                },
                                onClick = {
                                    showMenu = false
                                    onDeleteClick()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Rounded.Delete,
                                        null,
                                        tint = Color.Red,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // ---------------------------------------------------------
// 🔥 FUNGSI PEMBUAT SCROLLBAR (WAJIB ADA DI PALING BAWAH)
// ---------------------------------------------------------
    fun Modifier.simpleVerticalScrollbar(
        state: ScrollState,
        width: Dp = 4.dp
    ): Modifier = drawWithContent {
        drawContent() // Gambar konten teks dulu

        // Hitung dimensi scrollbar
        val firstVisibleElementIndex = state.value
        val elementHeight = this.size.height + state.maxValue
        val scrollbarFixedHeight = this.size.height * (this.size.height / elementHeight)
        val scrollbarOffsetY = firstVisibleElementIndex * (this.size.height / elementHeight)

        // Hanya gambar jika konten lebih panjang dari layar (perlu di-scroll)
        if (state.maxValue > 0) {
            drawRoundRect(
                color = Color.LightGray, // Warna Scrollbar
                topLeft = Offset(this.size.width - width.toPx(), scrollbarOffsetY),
                size = Size(width.toPx(), scrollbarFixedHeight),
                cornerRadius = CornerRadius(x = 2.dp.toPx(), y = 2.dp.toPx())
            )
        }
    }