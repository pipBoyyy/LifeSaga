package com.example.lifesaga.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifesaga.ui.theme.luckiestGuyFontFamily

@Composable
fun MainMenuScreen(
    onNewGame: () -> Unit,
    onContinueGame: () -> Unit,
    onSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF8BC34A), Color(0xFF558B2F))))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "LifeSaga",
            fontSize = 60.sp,
            fontFamily = luckiestGuyFontFamily,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            style = LocalTextStyle.current.copy(
                shadow = Shadow(Color.Black.copy(alpha = 0.5f), Offset(5f, 5f), blurRadius = 8f)
            )
        )
        Spacer(Modifier.height(40.dp))
        Button(onClick = onNewGame, modifier = Modifier.fillMaxWidth(0.6f).padding(8.dp)) { Text("Новая игра") }
        Button(onClick = onContinueGame, modifier = Modifier.fillMaxWidth(0.6f).padding(8.dp)) { Text("Продолжить") }
        Button(onClick = onSettings, modifier = Modifier.fillMaxWidth(0.6f).padding(8.dp)) { Text("Настройки") }
    }
}
