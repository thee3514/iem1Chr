package com.iem1chr.launcher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.iem1chr.launcher.ui.theme.iem1ChrLauncherTheme
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "launcher_prefs")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            iem1ChrLauncherTheme {
                LauncherRoot()
            }
        }
    }
}

@Composable
private fun LauncherRoot(
    viewModel: LauncherViewModel = viewModel(factory = LauncherViewModel.Factory(LocalContext.current))
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    var showDrawer by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    val launcherApps = remember {
        listOf(
            LauncherApp("Phone", "com.android.dialer"),
            LauncherApp("Messages", "com.android.mms"),
            LauncherApp("Camera", "com.android.camera2"),
            LauncherApp("Browser", "com.android.chrome"),
            LauncherApp("Gallery", "com.android.gallery3d"),
            LauncherApp("Settings", "com.android.settings"),
            LauncherApp("Clock", "com.android.deskclock"),
            LauncherApp("Music", "com.android.music"),
            LauncherApp("Maps", "com.google.android.apps.maps")
        )
    }

    val wallpaperColors = listOf(
        listOf(Color(0xFF0F172A), Color(0xFF111827), Color(0xFF1F2937)),
        listOf(Color(0xFF0B1120), Color(0xFF1E293B), Color(0xFF111827)),
        listOf(Color(0xFF3B0764), Color(0xFF312E81), Color(0xFF0F172A)),
        listOf(Color(0xFF052E16), Color(0xFF14532D), Color(0xFF064E3B))
    )

    val gradient = wallpaperColors.getOrElse(state.wallpaperIndex) { wallpaperColors.first() }

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradient))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 30.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "iem1Chr",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Launcher",
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFFCBD5E1))
                    )
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize().padding(bottom = 90.dp)
                ) {
                    items(launcherApps) { app ->
                        AppTile(
                            app = app,
                            onClick = {
                                val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                                if (launchIntent != null) {
                                    context.startActivity(launchIntent)
                                }
                            }
                        )
                    }
                }
            }

            Box(modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
                Column(horizontalAlignment = Alignment.End) {
                    Button(
                        onClick = { showDrawer = !showDrawer },
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8))
                    ) {
                        Icon(Icons.Default.Apps, contentDescription = null)
                        Text("Apps")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { showSettings = !showSettings },
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                        Text("Settings")
                    }
                }
            }

            if (showDrawer) {
                AppDrawer(
                    apps = launcherApps,
                    onDismiss = { showDrawer = false },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            if (showSettings) {
                SettingsSheet(
                    state = state,
                    onThemeToggle = { viewModel.toggleTheme() },
                    onWallpaperNext = { viewModel.nextWallpaper() },
                    onDismiss = { showSettings = false },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
private fun AppTile(app: LauncherApp, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.height(110.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.9f)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.background(Color(0xFF60A5FA), RoundedCornerShape(14.dp)).padding(12.dp)
            ) {
                Text(text = app.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = app.name, color = Color.White, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun AppDrawer(apps: List<LauncherApp>, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(16.dp).fillMaxSize(0.88f),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.96f))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Text(
                text = "App drawer",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(apps) { app ->
                    AppTile(app = app, onClick = { onDismiss() })
                }
            }
        }
    }
}

@Composable
private fun SettingsSheet(
    state: LauncherUiState,
    onThemeToggle: () -> Unit,
    onWallpaperNext: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(16.dp).fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(alpha = 0.96f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Launcher settings", color = Color.White, fontWeight = FontWeight.Bold)
                Button(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
                    Text("Close")
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = Color(0xFF93C5FD))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Dark mode", color = Color.White)
                }
                Switch(checked = state.darkModeEnabled, onCheckedChange = { onThemeToggle() })
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onWallpaperNext,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Change wallpaper")
            }
        }
    }
}

data class LauncherApp(
    val name: String,
    val packageName: String
)

data class LauncherUiState(
    val darkModeEnabled: Boolean = true,
    val wallpaperIndex: Int = 0
)

class LauncherViewModel(private val context: android.content.Context) : ViewModel() {
    private val prefs = context.dataStore

    private val darkModeKey = booleanPreferencesKey("dark_mode")
    private val wallpaperKey = stringPreferencesKey("wallpaper_index")

    private val _state = MutableStateFlow(LauncherUiState())
    val state: StateFlow<LauncherUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val darkMode = prefs.data.map { it[darkModeKey] ?: true }.first()
            val wallpaperIndex = prefs.data.map { it[wallpaperKey]?.toIntOrNull() ?: 0 }.first()
            _state.value = LauncherUiState(
                darkModeEnabled = darkMode,
                wallpaperIndex = wallpaperIndex
            )
        }
    }

    fun toggleTheme() {
        val enabled = !_state.value.darkModeEnabled
        viewModelScope.launch {
            prefs.edit { it[darkModeKey] = enabled }
            _state.value = _state.value.copy(darkModeEnabled = enabled)
        }
    }

    fun nextWallpaper() {
        val next = (_state.value.wallpaperIndex + 1) % 4
        viewModelScope.launch {
            prefs.edit { it[wallpaperKey] = next.toString() }
            _state.value = _state.value.copy(wallpaperIndex = next)
        }
    }

    companion object {
        fun Factory(context: android.content.Context): androidx.lifecycle.ViewModelProvider.Factory =
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LauncherViewModel(context) as T
                }
            }
    }
}
