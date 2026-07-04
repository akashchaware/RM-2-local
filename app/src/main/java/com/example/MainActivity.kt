package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.ui.RepairViewModel
import com.example.ui.Screen
import com.example.ui.components.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavySurface
import com.example.ui.theme.TealPrimary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialise database and repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = AppRepository(database.appDao())

        // 2. Build the UI
        setContent {
            MyApplicationTheme {
                val viewModel: RepairViewModel = viewModel()

                val currentScreen = viewModel.currentScreen.value
                val userProfile by viewModel.userProfile.collectAsState()
                val isLoggedIn = viewModel.isLoggedIn.value

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            containerColor = NavySurface,
                            tonalElevation = 8.dp
                        ) {
                            // Home/Landing tab
                            NavigationBarItem(
                                selected = currentScreen == Screen.Landing,
                                onClick = { viewModel.navigateTo(Screen.Landing) },
                                icon = { Icon(Icons.Filled.Home, contentDescription = "Home", tint = if (currentScreen == Screen.Landing) Color.Black else Color.White) },
                                label = { Text("Home", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.testTag("nav_home_tab")
                            )

                            // Dynamic Role-based Dashboard Tab
                            val (dashLabel, dashIcon) = if (isLoggedIn) {
                                when (userProfile.role) {
                                    "CUSTOMER" -> Pair("My Repairs", Icons.Filled.Build)
                                    "TECHNICIAN" -> Pair("Tech Jobs", Icons.Filled.Build)
                                    "COORDINATOR" -> Pair("Coord Desk", Icons.Filled.Warning)
                                    "REPAIRMASTER" -> Pair("Shop Console", Icons.Filled.Star)
                                    "ADMIN" -> Pair("Admin Deck", Icons.Filled.Settings)
                                    else -> Pair("My Repairs", Icons.Filled.Build)
                                }
                            } else {
                                Pair("My Dashboard", Icons.Filled.Build)
                            }

                            val isDashActive = currentScreen == Screen.CustomerDashboard ||
                                    currentScreen == Screen.TechnicianDashboard ||
                                    currentScreen == Screen.CoordinatorDashboard ||
                                    currentScreen == Screen.RepairMasterDashboard ||
                                    currentScreen == Screen.AdminDashboard ||
                                    currentScreen == Screen.RepairRequestDetail

                            NavigationBarItem(
                                selected = isDashActive,
                                onClick = {
                                    if (isLoggedIn) {
                                        val dest = when (userProfile.role) {
                                            "CUSTOMER" -> Screen.CustomerDashboard
                                            "TECHNICIAN" -> Screen.TechnicianDashboard
                                            "COORDINATOR" -> Screen.CoordinatorDashboard
                                            "REPAIRMASTER" -> Screen.RepairMasterDashboard
                                            "ADMIN" -> Screen.AdminDashboard
                                            else -> Screen.CustomerDashboard
                                        }
                                        viewModel.navigateTo(dest)
                                    } else {
                                        viewModel.navigateTo(Screen.Login)
                                    }
                                },
                                icon = { Icon(dashIcon, contentDescription = dashLabel, tint = if (isDashActive) Color.Black else Color.White) },
                                label = { Text(dashLabel, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.testTag("nav_dashboard_tab")
                            )

                            // Marketplace tab
                            val isMarketActive = currentScreen == Screen.Marketplace ||
                                    currentScreen == Screen.MarketplaceDetail ||
                                    currentScreen == Screen.MarketplaceCheckout

                            NavigationBarItem(
                                selected = isMarketActive,
                                onClick = { viewModel.navigateTo(Screen.Marketplace) },
                                icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Marketplace", tint = if (isMarketActive) Color.Black else Color.White) },
                                label = { Text("Market", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.testTag("nav_market_tab")
                            )

                            // Profile/Role switcher tab
                            val isProfileActive = currentScreen == Screen.UserProfile || currentScreen == Screen.Login
                            NavigationBarItem(
                                selected = isProfileActive,
                                onClick = {
                                    if (isLoggedIn) {
                                        viewModel.navigateTo(Screen.UserProfile)
                                    } else {
                                        viewModel.navigateTo(Screen.Login)
                                    }
                                },
                                icon = { Icon(Icons.Filled.Person, contentDescription = "Profile", tint = if (isProfileActive) Color.Black else Color.White) },
                                label = { Text(if (isLoggedIn) "Profile" else "Sign In", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.testTag("nav_profile_tab")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(NavyBackground)
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentScreen,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            },
                            label = "ScreenTransition"
                        ) { targetState ->
                            when (targetState) {
                                Screen.Landing -> LandingView(viewModel = viewModel)
                                Screen.Marketplace -> MarketplaceView(viewModel = viewModel)
                                Screen.Careers -> CareersView(viewModel = viewModel)
                                Screen.Login -> LoginView(viewModel = viewModel)
                                Screen.MarketplaceDetail -> {
                                    val itemId = viewModel.selectedListingId.value
                                    if (itemId != null) {
                                        MarketplaceDetailView(viewModel = viewModel, listingId = itemId)
                                    } else {
                                        viewModel.navigateTo(Screen.Marketplace)
                                    }
                                }
                                // Protected Screens requiring login
                                else -> {
                                    if (!isLoggedIn) {
                                        LoginView(viewModel = viewModel)
                                    } else {
                                        when (targetState) {
                                            Screen.CustomerDashboard -> CustomerDashboardView(viewModel = viewModel)
                                            Screen.TechnicianDashboard -> TechnicianDashboardView(viewModel = viewModel)
                                            Screen.CoordinatorDashboard -> CoordinatorDashboardView(viewModel = viewModel)
                                            Screen.RepairMasterDashboard -> RepairMasterDashboardView(viewModel = viewModel)
                                            Screen.AdminDashboard -> AdminDashboardView(viewModel = viewModel)
                                            Screen.NewRepairRequest -> NewRepairRequestView(viewModel = viewModel)
                                            Screen.UserProfile -> UserProfileView(viewModel = viewModel)
                                            Screen.RepairRequestDetail -> {
                                                val reqId = viewModel.selectedRequestId.value
                                                if (reqId != null) {
                                                    RepairRequestDetailView(viewModel = viewModel, requestId = reqId)
                                                } else {
                                                    viewModel.navigateTo(Screen.CustomerDashboard)
                                                }
                                            }
                                            Screen.MarketplaceCheckout -> {
                                                val itemId = viewModel.selectedListingId.value
                                                if (itemId != null) {
                                                    MarketplaceCheckoutView(viewModel = viewModel, listingId = itemId)
                                                } else {
                                                    viewModel.navigateTo(Screen.Marketplace)
                                                }
                                            }
                                            else -> LandingView(viewModel = viewModel)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

