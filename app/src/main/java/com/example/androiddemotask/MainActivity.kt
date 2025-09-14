package com.example.androiddemotask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.androiddemotask.presentation.navigation.AppNavigation
import com.example.androiddemotask.presentation.navigation.BottomNavigationBar
import com.example.androiddemotask.presentation.theme.ThemeViewModel
import com.example.androiddemotask.ui.theme.AndroidDemotaskTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val themeViewModel: ThemeViewModel = hiltViewModel()
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
    
    AndroidDemotaskTheme(darkTheme = isDarkTheme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.fillMaxSize()
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.weight(1f)
                ) {
                    AppNavigation(navController = navController)
                }
                BottomNavigationBar(navController = navController)
            }
        }
    }
}