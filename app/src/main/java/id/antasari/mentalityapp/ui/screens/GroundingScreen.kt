package id.antasari.mentalityapp.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.antasari.mentalityapp.ui.theme.MainGradient
import id.antasari.mentalityapp.ui.theme.PoppinsFamily

// =======================
// DATA MODEL
// =======================
data class GroundingTechnique(
    val title: String,
    val steps: List<String>,
    val accentColor: Color
)

// =======================
// SCREEN
// =======================
@Composable
fun GroundingScreen(
    navController: NavController,
    techniqueId: String
) {
    val technique = remember(techniqueId) {
        getGroundingTechnique(techniqueId)
    }

    var currentStep by remember { mutableIntStateOf(0) }
    val progress = (currentStep + 1).toFloat() / technique.steps.size

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainGradient)
    ) {

        // CLOSE BUTTON
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
                .background(Color.White.copy(alpha = 0.6f), CircleShape)
        ) {
            Icon(Icons.Rounded.Close, contentDescription = "Close", tint = Color.Gray)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // TITLE
            Text(
                text = technique.title.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = technique.accentColor.copy(alpha = 0.8f),
                fontFamily = PoppinsFamily
            )

            Spacer(modifier = Modifier.height(8.dp))

            // STEP INFO
            Text(
                text = "Step ${currentStep + 1} of ${technique.steps.size}",
                fontSize = 12.sp,
                color = Color.Gray,
                fontFamily = PoppinsFamily
            )

            Spacer(modifier = Modifier.height(40.dp))

            // STEP CONTENT
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "GroundingStep"
            ) { step ->
                Text(
                    text = technique.steps[step],
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = PoppinsFamily,
                    color = Color(0xFF37474F),
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 140.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // PROGRESS
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50)),
                color = technique.accentColor,
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )

            Spacer(modifier = Modifier.height(60.dp))

            // ACTION BUTTON
            Button(
                onClick = {
                    if (currentStep < technique.steps.lastIndex) {
                        currentStep++
                    } else {
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .height(56.dp)
                    .width(200.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = technique.accentColor)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (currentStep < technique.steps.lastIndex)
                            "Next step"
                        else
                            "Done for now",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = PoppinsFamily
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (currentStep < technique.steps.lastIndex)
                            Icons.Rounded.ArrowForward
                        else
                            Icons.Rounded.Check,
                        contentDescription = null
                    )
                }
            }

            if (currentStep > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Back",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontFamily = PoppinsFamily,
                    modifier = Modifier.clickable { currentStep-- }
                )
            }
        }
    }
}

// =======================
// DATA SOURCE
// =======================
fun getGroundingTechnique(id: String): GroundingTechnique {
    return when (id) {
        "54321" -> GroundingTechnique(
            title = "5-4-3-2-1",
            accentColor = Color(0xFF43A047),
            steps = listOf(
                "Look around and name 5 things you can see.",
                "Listen closely and name 4 things you can hear.",
                "Notice your body and name 3 things you can feel.",
                "Name 2 things you can smell.",
                "Take one slow, deep breath."
            )
        )

        "body" -> GroundingTechnique(
            title = "Body Check",
            accentColor = Color(0xFF1E88E5),
            steps = listOf(
                "Sit comfortably and press your feet into the floor.",
                "Notice the support under your body.",
                "Relax your shoulders and jaw.",
                "Take a slow breath. You are here."
            )
        )

        "temp" -> GroundingTechnique(
            title = "Temperature",
            accentColor = Color(0xFFF4511E),
            steps = listOf(
                "Hold something warm or cold.",
                "Focus on the sensation in your hand.",
                "Stay with it for a few seconds.",
                "Let go and notice the change."
            )
        )

        "name3" -> GroundingTechnique(
            title = "Name 3 Things",
            accentColor = Color(0xFF8E24AA),
            steps = listOf(
                "Look around the room.",
                "Find 3 things that are blue.",
                "Find 3 things that are round.",
                "Find 3 things that feel soft."
            )
        )

        else -> GroundingTechnique(
            title = "Grounding",
            accentColor = Color.Gray,
            steps = listOf("Take a moment to breathe.")
        )
    }
}
