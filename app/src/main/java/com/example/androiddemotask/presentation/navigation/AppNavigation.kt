package com.example.androiddemotask.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.androiddemotask.presentation.home.HomeScreen
import com.example.androiddemotask.presentation.messages.MessagesScreen
import com.example.androiddemotask.presentation.profile.ProfileScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route
    ) {
        composable(BottomNavItem.Home.route) {
            HomeScreen()
        }
        composable(BottomNavItem.Messages.route) {
            MessagesScreen()
        }
        composable(BottomNavItem.Profile.route) {
            ProfileScreen()
        }
    }
}
