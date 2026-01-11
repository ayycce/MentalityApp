package id.antasari.mentalityapp.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import id.antasari.mentalityapp.ui.navigation.Screen
import id.antasari.mentalityapp.ui.theme.MainGradient
import id.antasari.mentalityapp.ui.theme.PoppinsFamily
import id.antasari.mentalityapp.ui.theme.SkyBlue
import id.antasari.mentalityapp.ui.theme.SoftNeonPink
import id.antasari.mentalityapp.ui.viewmodel.UserViewModel
import kotlinx.coroutines.delay

// ==========================================
// 1. SPLASH SCREEN (ANIMATED)
// ==========================================
@Composable
fun SplashScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    val isFirstRun by userViewModel.isFirstRun.collectAsState()

    // Animasi Scale untuk Logo (Bernapas)
    val scale = remember { Animatable(0.5f) }

    LaunchedEffect(key1 = true) {
        // Animasi logo membesar perlahan
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        delay(1000) // Tahan sebentar

        // Navigasi Logic
        if (isFirstRun) {
            navController.navigate(Screen.NameInput.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Splash.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.Spa, // Logo sementara (Bisa diganti gambar)
                contentDescription = null,
                tint = SoftNeonPink,
                modifier = Modifier
                    .size(80.dp)
                    .scale(scale.value) // 🔥 Animasi diterapkan di sini
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Mentality.",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PoppinsFamily,
                color = SoftNeonPink,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "A gentle space for you.",
                fontSize = 14.sp,
                color = Color.Gray,
                fontFamily = PoppinsFamily
            )
        }
    }
}

// ==========================================
// 2. NAME INPUT SCREEN (CLEAN & MODERN)
// ==========================================
@Composable
fun NameInputScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }

    // Gunakan MainGradient agar konsisten dengan Splash
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainGradient)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding() // 🔥 Agar input naik saat keyboard muncul
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Hi there,",
                fontSize = 20.sp,
                color = Color.Gray,
                fontFamily = PoppinsFamily
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "What should we\ncall you?",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PoppinsFamily,
                color = Color(0xFF37474F),
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Input Field yang Clean (Background Putih)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Your name (optional)", color = Color.LightGray, fontFamily = PoppinsFamily) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SoftNeonPink,
                    unfocusedBorderColor = Color.Transparent, // Hilangkan border biar clean
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = PoppinsFamily,
                    fontSize = 18.sp,
                    color = Color(0xFF37474F)
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    userViewModel.saveName(name)
                    navController.navigate(Screen.Welcome.route)
                })
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your identity stays on this device.",
                fontSize = 12.sp,
                color = Color.Gray.copy(alpha = 0.7f),
                fontFamily = PoppinsFamily
            )
        }

        // Tombol Continue (Floating di bawah kanan)
        Button(
            onClick = {
                userViewModel.saveName(name)
                navController.navigate(Screen.Welcome.route)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp)
                .height(56.dp)
                .width(160.dp), // Tombol agak lebar
            colors = ButtonDefaults.buttonColors(containerColor = SkyBlue),
            shape = RoundedCornerShape(50),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily)
        }
    }
}

// ==========================================
// 3. WELCOME PHILOSOPHY SCREEN (CALMING)
// ==========================================
@Composable
fun WelcomeScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    val userName by userViewModel.userName.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainGradient)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Sapaan Personal
            Text(
                text = "Hi, $userName.",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = PoppinsFamily,
                color = SoftNeonPink,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Kartu Filosofi (Agar teks lebih fokus)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "This is a space to check in\nwith yourself.",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = PoppinsFamily,
                        color = Color(0xFF37474F)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(modifier = Modifier.width(40.dp), color = SoftNeonPink.copy(0.3f))
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "No pressure.\nNo fixing.\nJust being.",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF37474F),
                        lineHeight = 28.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            Button(
                onClick = {
                    userViewModel.completeOnboarding()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.NameInput.route) { inclusive = true }
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SoftNeonPink),
                shape = RoundedCornerShape(50),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text("Start Journey", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = PoppinsFamily)
            }
        }
    }
}