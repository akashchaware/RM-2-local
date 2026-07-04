package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.R
import com.example.ui.RepairViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

@Composable
fun LoginView(
    viewModel: RepairViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Sign In, 1 = Sign Up
    var nameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var cityInput by remember { mutableStateOf("Nagpur") }
    var selectedRole by remember { mutableStateOf("CUSTOMER") }
    var passcode by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var roleExpanded by remember { mutableStateOf(false) }

    // Google Sign-In Simulation States
    var showGooglePicker by remember { mutableStateOf(false) }
    var showGoogleCompleteForm by remember { mutableStateOf(false) }
    var googleEmail by remember { mutableStateOf("") }
    var googleName by remember { mutableStateOf("") }
    var googlePhone by remember { mutableStateOf("") }
    var googleCity by remember { mutableStateOf("Nagpur") }

    val roles = listOf(
        Pair("CUSTOMER", "📱 Customer Dashboard"),
        Pair("MARKETPLACE_BUYER", "🛒 Marketplace Buyer"),
        Pair("TECHNICIAN", "🔧 Nagpur Service Technician"),
        Pair("REPAIRMASTER", "🏪 Authorized Repair Master"),
        Pair("COORDINATOR", "📋 Nagpur Operations Coordinator"),
        Pair("ADMIN", "⚙️ Hub Admin Panel")
    )

    val googleAccounts = listOf(
        Pair("akash.chaware.23n@gmail.com", "Akash Chaware"),
        Pair("nagpur.repair.partner@gmail.com", "Nagpur Partner"),
        Pair("test.guest.tester@gmail.com", "QA Guest Tester")
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Logo & Brand Header
            Image(
                painter = painterResource(id = R.drawable.img_hero_banner_1782690134468),
                contentDescription = "RepairMaster Brand Banner",
                modifier = Modifier
                    .height(64.dp)
                    .fillMaxWidth(0.9f)
                    .padding(bottom = 8.dp)
                    .testTag("login_app_logo"),
                contentScale = ContentScale.Fit
            )

            // Message alerts
            if (errorMessage != null || successMessage != null) {
                Surface(
                    color = if (errorMessage != null) AccentRed.copy(alpha = 0.12f) else AccentGreen.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (errorMessage != null) AccentRed else AccentGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (errorMessage != null) Icons.Filled.Warning else Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = if (errorMessage != null) AccentRed else AccentGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = errorMessage ?: successMessage ?: "",
                            color = if (errorMessage != null) AccentRed else AccentGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { errorMessage = null; successMessage = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.Gray, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // Collapsible Demo Accounts Info Card
            var showDemoAccounts by remember { mutableStateOf(false) }
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Info, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Preseeded Demo Accounts (Nagpur)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        TextButton(
                            onClick = { showDemoAccounts = !showDemoAccounts },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.height(24.dp)
                        ) {
                            Text(if (showDemoAccounts) "Hide" else "Show", color = TealPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (showDemoAccounts) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                Triple("Admin (Dashboard)", "+91 95225 02707", "admin@rm2024"),
                                Triple("Customer (Self-Repair & Requests)", "+91 98230 12345", "12345"),
                                Triple("Technician (Job Executions)", "+91 98230 55555", "tech@rm2024"),
                                Triple("Repair Master (Retail Affiliate)", "+91 98230 77777", "master@rm2024")
                            ).forEach { (role, phone, pwd) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable {
                                            phoneInput = phone
                                            passwordInput = pwd
                                        }
                                        .padding(vertical = 4.dp, horizontal = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(role, color = AmberAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text("Ph: $phone", color = Color.White, fontSize = 11.sp)
                                    }
                                    Text("Pwd: $pwd [Tap to Fill]", color = TealPrimary, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                                }
                                HorizontalDivider(color = GrayBorder, thickness = 0.5.dp)
                            }
                            Text("Note: You can log in using just the 10-digit phone number (e.g. 9522502707) or tap any demo account row above to instantly auto-fill!", color = GrayText, fontSize = 10.sp, lineHeight = 13.sp)
                        }
                    }
                }
            }

            // Security warning
            Surface(
                color = NavySurface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, GrayBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.Transparent,
                        contentColor = TealPrimary,
                        divider = { HorizontalDivider(color = GrayBorder, thickness = 0.5.dp) }
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0; errorMessage = null; successMessage = null },
                            text = { Text("SIGN IN", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1; errorMessage = null; successMessage = null },
                            text = { Text("SIGN UP (NEW USER)", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedTab == 0) {
                        // SIGN IN SCREEN - PHONE & PASSWORD ONLY
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Nagpur Mobile input
                            Column {
                                Text(
                                    text = "Nagpur Mobile Number",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                OutlinedTextField(
                                    value = phoneInput,
                                    onValueChange = { phoneInput = it; errorMessage = null },
                                    placeholder = { Text("e.g. +91 98230 12345", color = Color.Gray, fontSize = 13.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = TealPrimary,
                                        unfocusedBorderColor = GrayBorder
                                    ),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_phone_input")
                                )
                            }

                            // Password Input
                            Column {
                                Text(
                                    text = "Password",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                OutlinedTextField(
                                    value = passwordInput,
                                    onValueChange = { passwordInput = it; errorMessage = null },
                                    placeholder = { Text("Enter your password", color = Color.Gray, fontSize = 13.sp) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = TealPrimary,
                                        unfocusedBorderColor = GrayBorder
                                    ),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_password_input")
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Submit Button
                            Button(
                                onClick = {
                                    val trimmedPhone = phoneInput.trim()
                                    val trimmedPassword = passwordInput.trim()
                                    if (trimmedPhone.isBlank()) {
                                        errorMessage = "Please enter your Nagpur Mobile Number!"
                                        return@Button
                                    }
                                    if (trimmedPassword.isBlank()) {
                                        errorMessage = "Please enter your Password!"
                                        return@Button
                                    }
                                    viewModel.signInUser(
                                        phone = trimmedPhone,
                                        passwordEntered = trimmedPassword,
                                        onSuccess = {
                                            errorMessage = null
                                            successMessage = "Signed in successfully!"
                                        },
                                        onError = { error ->
                                            errorMessage = error
                                        }
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("login_submit_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Verify & Secure Sign-In", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    } else {
                        // SIGN UP SCREEN (For Customers & Employees - includes Name, Phone, City, Password, Confirm Password, and Role selection)
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                text = "Create a secure Nagpur Doorstep Repair account. Fill in details to get authorized.",
                                color = GrayText,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            // Name input
                            Column {
                                Text(
                                    text = "Full Name",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                OutlinedTextField(
                                    value = nameInput,
                                    onValueChange = { nameInput = it; errorMessage = null },
                                    placeholder = { Text("e.g. Ramesh Deshmukh", color = Color.Gray, fontSize = 13.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = TealPrimary,
                                        unfocusedBorderColor = GrayBorder
                                    ),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_username_input")
                                )
                            }

                            // Nagpur Mobile input
                            Column {
                                Text(
                                    text = "Nagpur Mobile Number (Strictly Required)",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                OutlinedTextField(
                                    value = phoneInput,
                                    onValueChange = { phoneInput = it; errorMessage = null },
                                    placeholder = { Text("e.g. +91 98230 12345", color = Color.Gray, fontSize = 13.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = TealPrimary,
                                        unfocusedBorderColor = GrayBorder
                                    ),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_phone_signup_input")
                                )
                            }

                            // City input
                            Column {
                                Text(
                                    text = "Operating City (Currently Doorstep only in Nagpur)",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                OutlinedTextField(
                                    value = cityInput,
                                    onValueChange = { cityInput = it; errorMessage = null },
                                    placeholder = { Text("e.g. Nagpur", color = Color.Gray, fontSize = 13.sp) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = TealPrimary,
                                        unfocusedBorderColor = GrayBorder
                                    ),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_city_signup_input")
                                )
                            }

                            // Role Selection - Locked to CUSTOMER for direct signup
                            Column {
                                Text(
                                    text = "Account Role",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                                    border = BorderStroke(1.dp, GrayBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Filled.Person, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("📱 Customer Dashboard", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text("Employees must apply via the Careers section.", color = GrayText, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }

                            // Password
                            Column {
                                Text(
                                    text = "Create Password",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                OutlinedTextField(
                                    value = passwordInput,
                                    onValueChange = { passwordInput = it; errorMessage = null },
                                    placeholder = { Text("Choose a password", color = Color.Gray, fontSize = 13.sp) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = TealPrimary,
                                        unfocusedBorderColor = GrayBorder
                                    ),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_signup_password_input")
                                )
                            }

                            // Confirm Password
                            Column {
                                Text(
                                    text = "Confirm Password",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                OutlinedTextField(
                                    value = confirmPasswordInput,
                                    onValueChange = { confirmPasswordInput = it; errorMessage = null },
                                    placeholder = { Text("Re-enter your password", color = Color.Gray, fontSize = 13.sp) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = TealPrimary,
                                        unfocusedBorderColor = GrayBorder
                                    ),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("login_signup_confirm_password_input")
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Submit Button
                            Button(
                                onClick = {
                                    if (nameInput.isBlank() || phoneInput.isBlank() || cityInput.isBlank() || passwordInput.isBlank() || confirmPasswordInput.isBlank()) {
                                        errorMessage = "Please fill in all details!"
                                        return@Button
                                    }
                                    if (passwordInput != confirmPasswordInput) {
                                        errorMessage = "Passwords do not match!"
                                        return@Button
                                    }
                                    viewModel.registerUser(
                                        name = nameInput,
                                        phone = phoneInput,
                                        city = cityInput,
                                        role = selectedRole,
                                        password = passwordInput,
                                        confirmPassword = confirmPasswordInput,
                                        onSuccess = {
                                            errorMessage = null
                                            successMessage = "Account registered and logged in!"
                                        },
                                        onError = { error ->
                                            errorMessage = error
                                        }
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("login_signup_submit_button")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Register Nagpur Account", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    // Security Divider
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(color = GrayBorder, modifier = Modifier.weight(1f))
                        Text(
                            text = "SECURE SINGLE SIGN-ON",
                            color = GrayText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        HorizontalDivider(color = GrayBorder, modifier = Modifier.weight(1f))
                    }

                    // Google Identity Button (Simulated)
                    OutlinedButton(
                        onClick = { showGooglePicker = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("google_sso_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Draw simulated Google multicolor icon
                            Surface(
                                color = Color.White,
                                shape = CircleShape,
                                modifier = Modifier.size(16.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "G",
                                        color = Color.Black,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedTab == 0) "Continue with Google" else "Sign Up with Google",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }

    // Google Account Picker Dialog
    if (showGooglePicker) {
        AlertDialog(
            onDismissRequest = { showGooglePicker = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("G", color = Color.White, fontWeight = FontWeight.Black, modifier = Modifier.padding(end = 8.dp))
                    Text("Choose Google Account", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = NavySurface,
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Authorized by Google Identity Services. Select your account to sign in securely to Nagpur doorstep hub:",
                        color = GrayText,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    googleAccounts.forEach { (email, name) ->
                        Surface(
                            onClick = {
                                showGooglePicker = false
                                googleEmail = email
                                googleName = name
                                
                                viewModel.handleGoogleAuth(
                                    email = email,
                                    name = name,
                                    phoneIfSignUp = "",
                                    cityIfSignUp = "",
                                    onSuccess = { needMoreInfo ->
                                        if (needMoreInfo) {
                                            // Open incomplete register form to collect phone/city
                                            showGoogleCompleteForm = true
                                        } else {
                                            successMessage = "Google Authentication Successful! Welcome, $name."
                                        }
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                    }
                                )
                            },
                            color = NavyDark,
                            border = BorderStroke(1.dp, GrayBorder),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(TealPrimary.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = name.take(1).uppercase(),
                                        color = TealPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(text = email, color = GrayText, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showGooglePicker = false }) {
                    Text("Cancel", color = GrayText)
                }
            }
        )
    }

    // Google Sign-Up Completer Dialog (collects ONLY Phone and City)
    if (showGoogleCompleteForm) {
        AlertDialog(
            onDismissRequest = { showGoogleCompleteForm = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = TealPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Complete Google Sign-Up", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = NavySurface,
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Nice to meet you, $googleName! Since this is your first time using Google Sign-In, please complete registration below. We only require your Nagpur Phone Number and City.",
                        color = GrayText,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    // Display Name (Read-Only)
                    Column {
                        Text(text = "Google Account Name", color = GrayText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = googleName, color = Color.White, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
                    }

                    // Display Email (Read-Only)
                    Column {
                        Text(text = "Google Email Address", color = GrayText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(text = googleEmail, color = Color.White, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
                    }

                    // Nagpur Mobile input
                    Column {
                        Text(
                            text = "Nagpur Mobile Number (Required)",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = googlePhone,
                            onValueChange = { googlePhone = it },
                            placeholder = { Text("e.g. +91 98230 12345", color = Color.Gray, fontSize = 13.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = TealPrimary,
                                unfocusedBorderColor = GrayBorder
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("google_complete_phone_input")
                        )
                    }

                    // City input
                    Column {
                        Text(
                            text = "City ( Nagpur only )",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = googleCity,
                            onValueChange = { googleCity = it },
                            placeholder = { Text("e.g. Nagpur", color = Color.Gray, fontSize = 13.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = TealPrimary,
                                unfocusedBorderColor = GrayBorder
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("google_complete_city_input")
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (googlePhone.isBlank() || googleCity.isBlank()) {
                            errorMessage = "Mobile number and City are strictly required!"
                            return@Button
                        }
                        showGoogleCompleteForm = false
                        viewModel.handleGoogleAuth(
                            email = googleEmail,
                            name = googleName,
                            phoneIfSignUp = googlePhone,
                            cityIfSignUp = googleCity,
                            onSuccess = { _ ->
                                successMessage = "Google account registration completed successfully! Welcome, $googleName."
                            },
                            onError = { error ->
                                errorMessage = error
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Register & Enter Hub", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoogleCompleteForm = false }) {
                    Text("Cancel", color = GrayText)
                }
            }
        )
    }
}
