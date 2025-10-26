package com.example.lifesaga.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lifesaga.navigation.Screen
import com.example.lifesaga.ui.theme.luckiestGuyFontFamily
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // Этот эффект запустится один раз при появлении экрана
    LaunchedEffect(key1 = true) {
        delay(3000) // Ждем 3 секунды
        navController.navigate(Screen.MAIN_MENU.name) {
            // Удаляем заставку из истории навигации
            popUpTo(Screen.SPLASH.name) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "LifeSaga",
            fontSize = 64.sp,
            fontFamily = luckiestGuyFontFamily,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            style = LocalTextStyle.current.copy(
                shadow = Shadow(Color.Black.copy(alpha = 0.5f), Offset(5f,5f), blurRadius = 8f)
            )
        )
    }
}
