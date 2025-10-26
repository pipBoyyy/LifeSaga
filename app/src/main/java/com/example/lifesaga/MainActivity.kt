package com.example.lifesaga

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.lifesaga.navigation.NavGraph // Мы будем использовать NavGraph, который создадим далее
import com.example.lifesaga.ui.theme.LifeSagaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifeSagaTheme {
                // Вся логика навигации будет внутри этой функции
                NavGraph()
            }
        }
    }
}
