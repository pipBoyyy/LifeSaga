// В файле ui/screens/AssetsScreen.kt
package com.example.lifesaga.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lifesaga.data.Asset
import com.example.lifesaga.data.AssetCategory
import com.example.lifesaga.data.Character
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AssetsScreen(
    character: Character,
    availableAssets: List<Asset>,
    onBuyAsset: (Asset) -> Unit,
    onBack: () -> Unit
) {
    val categories = AssetCategory.values().toList()
    // Новый PagerState из androidx.compose.foundation.pager
    val pagerState = rememberPagerState(pageCount = { categories.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Имущество") })
        },
        bottomBar = {
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text("Назад")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            MyAssetsSection(character.assets)
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            TabRow(
                // API TabRow не изменился, он сам подхватит нужный индикатор
                selectedTabIndex = pagerState.currentPage,
            ) {
                categories.forEachIndexed { index, category ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                        text = { Text(category.displayName) }
                    )
                }
            }

            // Новый HorizontalPager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val category = categories[page]
                val assetsInCategory = availableAssets.filter { it.category == category }
                ShopCategoryPage(
                    assets = assetsInCategory,
                    character = character,
                    onBuyAsset = onBuyAsset
                )
            }
        }
    }
}


// --- Остальные функции остаются без изменений ---

@Composable
fun MyAssetsSection(myAssets: List<Asset>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Моё имущество", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        if (myAssets.isEmpty()) {
            Text("У вас пока нет никакого имущества.")
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)) {
                items(myAssets) { asset ->
                    AssetCard(asset = asset, canBuy = false, onBuyClick = {})
                }
            }
        }
    }
}

@Composable
fun ShopCategoryPage(
    assets: List<Asset>,
    character: Character,
    onBuyAsset: (Asset) -> Unit
) {
    if (assets.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Text("В этой категории пока ничего нет.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(assets) { asset ->
                val canAfford = character.money >= asset.price
                AssetCard(
                    asset = asset,
                    canBuy = canAfford && !character.assets.contains(asset),
                    onBuyClick = { onBuyAsset(asset) }
                )
            }
        }
    }
}

@Composable
fun AssetCard(
    asset: Asset,
    canBuy: Boolean,
    onBuyClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(asset.name, style = MaterialTheme.typography.bodyLarge)
                Text("Цена: ${asset.price}$", fontSize = 12.sp)
                Text("Расходы/год: ${asset.annualCost}$", fontSize = 12.sp)
                Text("Счастье: +${asset.happinessBoost}", fontSize = 12.sp)
            }
            if (canBuy) {
                Button(onClick = onBuyClick) {
                    Text("Купить")
                }
            }
        }
    }
}
