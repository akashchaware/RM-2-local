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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.RepairViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

@Composable
fun UserProfileView(
    viewModel: RepairViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val allUserProfiles by viewModel.allUserProfiles.collectAsState()
    val dbProfile = allUserProfiles.find { it.phone == userProfile.phone }
    val assignedRolesList = dbProfile?.role?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: listOf(userProfile.role)

    var name by remember { mutableStateOf(userProfile.name) }
    var phone by remember { mutableStateOf(userProfile.phone) }
    var isEditSavedMsg by remember { mutableStateOf(false) }
    var showRoleSwitcherDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "CLIENT ACCOUNT", color = TealPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "My Profile Hub", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            RoleBadge(role = userProfile.role)
        }

        // 1. Personal Settings Card
        Card(
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = BorderStroke(1.dp, GrayBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Personal Profile Credentials",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Name Input
                Text("Recipient Display Name", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        isEditSavedMsg = false
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealPrimary,
                        unfocusedBorderColor = GrayBorder
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("profile_name_input")
                )

                // Phone Input
                Text("Contact Mobile Number", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        isEditSavedMsg = false
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealPrimary,
                        unfocusedBorderColor = GrayBorder
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .testTag("profile_phone_input")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isEditSavedMsg) {
                        Text(text = "✔ Saved successfully", color = AccentGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    Button(
                        onClick = {
                            viewModel.updateProfile(name, phone)
                            isEditSavedMsg = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("save_profile_button")
                    ) {
                        Text("Save Credentials", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 2. Partnerships and Job applications Status
        Card(
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = BorderStroke(1.dp, GrayBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Registered Contractor Node Statuses",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val techStatusColor = when (userProfile.technicianStatus) {
                    "Approved" -> AccentGreen
                    "Pending" -> AmberAccent
                    "Rejected" -> AccentRed
                    else -> GrayText
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Certified Repair Specialist Node", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("Required for on-site diagnosis and repair quotas", color = GrayText, fontSize = 11.sp)
                    }
                    Text(
                        text = userProfile.technicianStatus,
                        color = techStatusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = GrayBorder.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))

                val masterStatusColor = when (userProfile.repairMasterStatus) {
                    "Approved" -> AccentGreen
                    "Pending" -> AmberAccent
                    "Rejected" -> AccentRed
                    else -> GrayText
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("RepairMaster Authorized Workshop Partner", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("Required for store refurbished phone sales listing", color = GrayText, fontSize = 11.sp)
                    }
                    Text(
                        text = userProfile.repairMasterStatus,
                        color = masterStatusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 2.5 DYNAMIC ROLE SWITCHER (For multi-role users)
        if (assignedRolesList.size > 1) {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, TealPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, tint = TealPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ACTIVE DESK ROLE SELECTOR",
                            color = TealPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "You are permitted multiple active roles. Tap any button to instantly switch your console dashboard desk.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        assignedRolesList.forEach { role ->
                            val isActive = userProfile.role.uppercase() == role.uppercase()
                            Button(
                                onClick = {
                                    if (!isActive) {
                                        viewModel.switchRole(role, "", {}, {})
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isActive) TealPrimary else NavyDark,
                                    contentColor = if (isActive) Color.Black else Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isActive) TealPrimary else GrayBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = role,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. SECURE ROLE SWITCHING BOARD (Milestone 3 requirement - Secured for Admins Only)
        if (userProfile.role == "ADMIN") {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, AmberAccent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Lock, contentDescription = null, tint = AmberAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SECURE TESTING DECK",
                            color = AmberAccent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "A sandbox controller is embedded for QA and product testers to freely cross between Customer, Technician, Repair Master, and Admin views instantly.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showRoleSwitcherDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_role_switcher_button")
                    ) {
                        Text("Trigger Role Switcher Dialog", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    var showPurgeConfirmation by remember { mutableStateOf(false) }

                    Button(
                        onClick = { showPurgeConfirmation = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("purge_test_data_button")
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Wipe Test Data & Launch Clean", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    if (showPurgeConfirmation) {
                        val context = androidx.compose.ui.platform.LocalContext.current
                        AlertDialog(
                            onDismissRequest = { showPurgeConfirmation = false },
                            title = { Text("Wipe All Test Data?", color = Color.White, fontWeight = FontWeight.Bold) },
                            text = {
                                Text(
                                    "This will instantly delete all temporary repair tickets, parts, listings, and customer reviews to prepare RepairMaster for dynamic production launch. Essential system user profiles are preserved.",
                                    color = GrayText
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        viewModel.clearAllDataAndPrepareLaunch {
                                            showPurgeConfirmation = false
                                            android.widget.Toast.makeText(context, "Database purged successfully! Ready for launch.", android.widget.Toast.LENGTH_LONG).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                                ) {
                                    Text("Confirm Wipe", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showPurgeConfirmation = false }) {
                                    Text("Cancel", color = Color.White)
                                }
                            },
                            containerColor = NavySurface
                        )
                    }
                }
            }
        }

        // 4. SESSION SECURITY & LOGOUT
        Card(
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = BorderStroke(1.dp, AccentRed.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.ExitToApp, contentDescription = null, tint = AccentRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ACCOUNT SESSION",
                        color = AccentRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Sign out of your active Nagpur credentials session. This will safely secure the workstation context until the next authentication challenge.",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.isLoggedIn.value = false
                        viewModel.navigateTo(Screen.Landing)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("logout_button")
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log Out from RepairMaster", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showRoleSwitcherDialog) {
        RoleSwitcherDialog(
            currentRole = userProfile.role,
            onRoleSelected = { chosenRole, code ->
                viewModel.switchRole(
                    chosenRole,
                    code,
                    onSuccess = { showRoleSwitcherDialog = false },
                    onError = { /* Error handled inside dialog form state */ }
                )
            },
            onDismiss = { showRoleSwitcherDialog = false }
        )
    }
}
