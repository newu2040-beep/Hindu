package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.data.AppRepository
import com.example.data.local.AppDatabase
import com.example.ui.screens.MainAppContent
import com.example.ui.theme.DivineHarmonyTheme
import com.example.ui.theme.ThemeManager
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Initialize standard persistent database safely
    val database = Room.databaseBuilder(
      applicationContext,
      AppDatabase::class.java,
      "hinduss_offline_database"
    ).fallbackToDestructiveMigration().build()
    
    val repository = AppRepository(database.appDao())
    
    // ViewModel setup utilizing clean manual factory constructor-injection
    val viewModel: MainViewModel by viewModels {
      object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          @Suppress("UNCHECKED_CAST")
          return MainViewModel(application, repository) as T
        }
      }
    }

    enableEdgeToEdge()
    
    setContent {
      val selectedPreset by viewModel.themePreset.collectAsState()
      val selectedMode by viewModel.themeMode.collectAsState()
      val currentLang by viewModel.lang.collectAsState()
      val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsState()

      val themeManager = remember(selectedPreset, selectedMode) {
        ThemeManager(selectedPreset, selectedMode)
      }

      DivineHarmonyTheme(themeManager = themeManager) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = androidx.compose.material3.MaterialTheme.colorScheme.background
        ) {
          MainAppContent(
            viewModel = viewModel,
            onboardingCompleted = isOnboardingCompleted,
            currentLang = currentLang,
            themePreset = selectedPreset,
            themeMode = selectedMode
          )
        }
      }
    }
  }
}
