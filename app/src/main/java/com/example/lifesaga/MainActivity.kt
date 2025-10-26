package com.example.lifesaga

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lifesaga.ui.theme.LifeSagaTheme
import com.example.lifesaga.ui.theme.luckiestGuyFontFamily
import androidx.compose.ui.geometry.Offset // Для тени
import com.example.lifesaga.navigation.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LifeSagaTheme {
                LifeSagaApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeSagaApp() {
    val navController = rememberNavController()
    val gameViewModel: GameViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.MAIN_MENU.name) {
        composable(Screen.MAIN_MENU.name) {
            MainMenuScreen(
                onNewGame    = { navController.navigate(Screen.CREATE_CHARACTER.name) },
                onContinueGame = {
                    if (gameViewModel.character.value != null) {
                        navController.navigate(Screen.GAME.name)
                    } else {
                        Toast.makeText(navController.context, "Начните новую игру!", Toast.LENGTH_SHORT).show()
                    }
                },
                onSettings   = { navController.navigate(Screen.SETTINGS.name) }
            )
        }

        composable(Screen.CREATE_CHARACTER.name) {
            CharacterCreationScreen(
                onCharacterCreated = { character ->
                    gameViewModel.startGame(character)
                    navController.navigate(Screen.GAME.name) {
                        popUpTo(Screen.MAIN_MENU.name) { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.GAME.name) {
            GameScreen(
                gameViewModel = gameViewModel,
                // navController удалили, он больше не нужен
                onMainMenu    = { navController.popBackStack(Screen.MAIN_MENU.name, false) }
            )
        }

        composable(Screen.SETTINGS.name) {
            SettingsScreen(
                onBack      = { navController.popBackStack() },
                onResetGame = {
                    gameViewModel.resetGame()
                    navController.popBackStack(Screen.MAIN_MENU.name, false)
                    Toast.makeText(navController.context, "Прогресс сброшен!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun MainMenuScreen(
    onNewGame:    () -> Unit,
    onContinueGame: () -> Unit,
    onSettings:   () -> Unit
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
                shadow = Shadow(Color.Black.copy(alpha = 0.5f), Offset(5f,5f), blurRadius = 8f)
            )
        )
        Spacer(Modifier.height(40.dp))
        Button(onClick = onNewGame,    modifier = Modifier.fillMaxWidth(0.6f).padding(8.dp)) { Text("Новая игра") }
        Button(onClick = onContinueGame, modifier = Modifier.fillMaxWidth(0.6f).padding(8.dp)) { Text("Продолжить") }
        Button(onClick = onSettings,   modifier = Modifier.fillMaxWidth(0.6f).padding(8.dp)) { Text("Настройки") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterCreationScreen(
    onCharacterCreated: (Character) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf(Gender.MALE) }
    var selectedAvatarId by remember { mutableStateOf(DefaultAvatarIds.DEFAULT_MALE_AVATAR) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Создание персонажа") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад") } }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Имя персонажа") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
            Text("Пол:", style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth().padding(vertical=8.dp), Arrangement.SpaceAround) {
                Gender.entries.filter { it != Gender.UNSPECIFIED }.forEach { gender ->
                    ChoiceChip(
                        text = gender.displayName,
                        isSelected = gender == selectedGender,
                        onClick = {
                            selectedGender = gender
                            selectedAvatarId = when(gender) {
                                Gender.MALE   -> DefaultAvatarIds.DEFAULT_MALE_AVATAR
                                Gender.FEMALE -> DefaultAvatarIds.DEFAULT_FEMALE_AVATAR
                                else          -> DefaultAvatarIds.GENERAL_DEFAULT_AVATAR
                            }
                        }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Выберите аватар:")
            LazyRow(Modifier.fillMaxWidth().padding(vertical=8.dp), Arrangement.spacedBy(8.dp)) {
                val avatars = when(selectedGender) {
                    Gender.MALE -> DefaultAvatarIds.MALE_AVATARS
                    Gender.FEMALE -> DefaultAvatarIds.FEMALE_AVATARS
                    else -> emptyList()
                }
                items(avatars) { id ->
                    Image(
                        painter = painterResource(id = id),
                        contentDescription = "Аватар",
                        modifier = Modifier.size(96.dp).clip(CircleShape)
                            .border(3.dp, if (selectedAvatarId==id) MaterialTheme.colorScheme.primary else Color.Gray, CircleShape)
                            .clickable { selectedAvatarId = id },
                        contentScale=ContentScale.Crop
                    )
                }
            }
            Spacer(Modifier.height(32.dp))
            Button(onClick = {
                if(name.isNotBlank()) {
                    onCharacterCreated(Character(name, selectedGender, selectedAvatarId))
                } else Toast.makeText(context, "Введите имя!", Toast.LENGTH_SHORT).show()
            }, Modifier.fillMaxWidth().height(56.dp)) {
                Text("Начать игру")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: ()->Unit, onResetGame: ()->Unit) {
    var showDialog by remember { mutableStateOf(false) }
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Настройки") },
            navigationIcon = { IconButton(onClick=onBack){ Icon(Icons.AutoMirrored.Filled.ArrowBack,"Назад") } }
        )
    }) { paddingValues ->
        Column(Modifier.fillMaxSize().padding(paddingValues).padding(16.dp), Alignment.CenterHorizontally, Arrangement.Center) {
            Button(onClick={ showDialog=true }, colors=ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)) { Text("Сбросить прогресс") }
            Spacer(Modifier.height(20.dp))
            OutlinedButton(onClick=onBack) { Text("Назад") }
        }
        if(showDialog) {
            AlertDialog(
                onDismissRequest={ showDialog=false },
                title={ Text("Подтвердите сброс") },
                text={ Text("Действие необратимо.") },
                confirmButton={ Button(onClick={ onResetGame(); showDialog=false }) { Text("Сбросить") } },
                dismissButton={ OutlinedButton(onClick={ showDialog=false }) { Text("Отмена") } }
            )
        }
    }
}
// Вставь этот код в конец файла MainActivity.kt

@Composable
fun ChoiceChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(4.dp)
            .selectable(
                selected = isSelected,
                onClick = onClick
            ),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray)
    ) {
        Text(
            text = text,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}
