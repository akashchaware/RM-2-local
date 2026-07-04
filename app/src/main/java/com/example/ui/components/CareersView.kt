package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.JobPosting
import com.example.ui.RepairViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

@Composable
fun CareersView(
    viewModel: RepairViewModel,
    modifier: Modifier = Modifier
) {
    val postings by viewModel.allJobPostings.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var selectedPostingForApply by remember { mutableStateOf<JobPosting?>(null) }
    var applicationSubmittedCode by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.Landing) },
                    modifier = Modifier.background(NavySurface, CircleShape)
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "JOIN THE NETWORK", color = TealPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Careers & Partnerships", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Info Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, GrayBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Become a Certified Service Partner", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "We are onboarding certified smartphone technicians and local retail repair master workshops in major cities. Earn premium high-density repair volumes and list refurbished device stock with locked escrow payouts.",
                        color = GrayText,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // List open postings
        item {
            Text(text = "Open Opportunities", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        val openPostings = postings.filter { !it.isClosed }

        if (openPostings.isEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = NavySurface), modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No active job postings published yet.", color = GrayText, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(openPostings) { job ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, GrayBorder),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = job.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(text = "${job.roleType} • ${job.location}", color = TealPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { selectedPostingForApply = job },
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                modifier = Modifier
                                    .height(28.dp)
                                    .testTag("career_apply_button_${job.id}")
                            ) {
                                Text("Apply Now", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = job.description, color = GrayText, fontSize = 12.sp, lineHeight = 16.sp)
                    }
                }
            }
        }
    }

    // Interactive Apply Modal Form
    if (selectedPostingForApply != null) {
        val targetJob = selectedPostingForApply!!
        var applicantName by remember { mutableStateOf(userProfile.name) }
        var applicantPhone by remember { mutableStateOf(userProfile.phone) }
        var applicantCity by remember { mutableStateOf(userProfile.city.ifBlank { "Nagpur" }) }
        var applicantPassword by remember { mutableStateOf("") }
        var coverNote by remember { mutableStateOf("") }
        var errorMsg by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { selectedPostingForApply = null },
            title = { Text("Apply: ${targetJob.title}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp) },
            text = {
                Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                    Text(
                        text = "Submit your credentials to initiate technical verification. Admins will review details.",
                        color = GrayText,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Applicant Name
                    Text("Your Full Name", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = applicantName,
                        onValueChange = { applicantName = it },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = GrayBorder
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .testTag("applicant_name_input")
                    )

                    // Phone
                    Text("Your Phone Number", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = applicantPhone,
                        onValueChange = { applicantPhone = it },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = GrayBorder
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .testTag("applicant_phone_input")
                    )

                    // City
                    Text("Operating City", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = applicantCity,
                        onValueChange = { applicantCity = it },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = GrayBorder
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .testTag("applicant_city_input")
                    )

                    // Password
                    Text("Choose Password (for Sign In on Approval)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = applicantPassword,
                        onValueChange = { applicantPassword = it },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = GrayBorder
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .testTag("applicant_password_input")
                    )

                    // Cover Note
                    Text("Credentials or Experience Cover Note", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = coverNote,
                        onValueChange = { coverNote = it },
                        placeholder = { Text("List certifications, micro-soldering experience, or retail shop tools...", color = Color.Gray, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = GrayBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .testTag("applicant_note_input")
                    )

                    if (errorMsg != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = errorMsg!!, color = AccentRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (applicantName.isBlank() || applicantPhone.isBlank() || applicantCity.isBlank() || applicantPassword.isBlank() || coverNote.isBlank()) {
                            errorMsg = "All details (including operating city and sign-in password) are required to evaluate credentials!"
                        } else {
                            viewModel.applyForRole(targetJob.roleType, applicantName, applicantPhone, applicantCity, applicantPassword)
                            selectedPostingForApply = null
                            applicationSubmittedCode = targetJob.roleType
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    modifier = Modifier.testTag("submit_job_application_button")
                ) {
                    Text("Submit Application", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPostingForApply = null }) { Text("Cancel", color = GrayText) }
            },
            containerColor = NavySurface
        )
    }

    // Success dialog
    if (applicationSubmittedCode != null) {
        val roleType = applicationSubmittedCode!!
        AlertDialog(
            onDismissRequest = { applicationSubmittedCode = null },
            title = { Text("Application Transmitted!", color = AccentGreen, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Your partnership application for $roleType has been submitted. Switch your active role to Admin (passcode admin@rm2024) and check the 'Applications' tab to evaluate and approve yourself instantly!",
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { applicationSubmittedCode = null },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Understood", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = NavySurface
        )
    }
}
