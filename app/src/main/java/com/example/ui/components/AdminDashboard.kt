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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.JobPosting
import com.example.data.Part
import com.example.data.UserProfile
import com.example.ui.RepairViewModel
import com.example.ui.theme.*

@Composable
fun AdminDashboardView(
    viewModel: RepairViewModel,
    modifier: Modifier = Modifier
) {
    val requests by viewModel.allRepairRequests.collectAsState()
    val listings by viewModel.allListings.collectAsState()
    val orders by viewModel.allOrders.collectAsState()
    val parts by viewModel.allParts.collectAsState()
    val jobPostings by viewModel.allJobPostings.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val allUserProfiles by viewModel.allUserProfiles.collectAsState()

    var activeTab by viewModel.adminSelectedTab

    // Default global surcharge setting
    var baseServiceChargePct by remember { mutableStateOf("15") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {
        // Title Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "ADMINISTRATIVE CONSOLE", color = TealPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "Superadmin Control Deck", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            RoleBadge(role = "ADMIN")
        }

        // Admin Tabs Row
        ScrollableTabRow(
            selectedTabIndex = activeTab,
            containerColor = NavySurface,
            contentColor = TealPrimary,
            edgePadding = 8.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = TealPrimary
                )
            }
        ) {
            val tabs = listOf("Overview", "Applications", "Users", "Parts Editor", "Settings", "Careers Portal")
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = activeTab == index,
                    onClick = { activeTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (activeTab == index) TealPrimary else GrayText
                        )
                    }
                )
            }
        }

        // Tab Content Router
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val pendingCount = allUserProfiles.count { it.technicianStatus == "Pending" || it.repairMasterStatus == "Pending" }
            when (activeTab) {
                0 -> AdminOverviewPanel(
                    userCount = allUserProfiles.count { it.id != "current_user" },
                    repairCount = requests.size,
                    activeCount = requests.count { it.statusStep in 2..10 },
                    completedCount = requests.count { it.statusStep == 11 },
                    partsCount = parts.size,
                    listingsCount = listings.size,
                    ordersCount = orders.size,
                    pendingApplicationsCount = pendingCount
                )
                1 -> AdminApplicationsPanel(
                    profilesList = allUserProfiles,
                    onApprove = { phone, type -> viewModel.approveApplication(phone, type) },
                    onReject = { phone, type -> viewModel.rejectApplication(phone, type) }
                )
                2 -> AdminUsersPanel(
                    usersList = allUserProfiles,
                    onRolesChanged = { phone, newRoles -> viewModel.updateUserRolesList(phone, newRoles) }
                )
                3 -> AdminPartsPanel(
                    parts = parts,
                    onAddPart = { name, price, cat -> viewModel.addPart(name, price, cat) },
                    onDeletePart = { viewModel.deletePart(it) },
                    onUpdatePart = { id, name, price, cat -> viewModel.updatePart(id, name, price, cat) }
                )
                4 -> AdminSettingsPanel(
                    serviceChargePct = baseServiceChargePct,
                    onUpdateCharge = { baseServiceChargePct = it }
                )
                5 -> AdminCareersPanel(
                    postings = jobPostings,
                    onCreatePosting = { t, r, l, d -> viewModel.createJobPosting(t, r, l, d) },
                    onClosePosting = { viewModel.closeJobPosting(it) },
                    onDeletePosting = { viewModel.deleteJobPosting(it) }
                )
            }
        }
    }
}

@Composable
fun AdminOverviewPanel(
    userCount: Int,
    repairCount: Int,
    activeCount: Int,
    completedCount: Int,
    partsCount: Int,
    listingsCount: Int,
    ordersCount: Int,
    pendingApplicationsCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner alerts for pending items
        if (pendingApplicationsCount > 0) {
            Card(
                colors = CardDefaults.cardColors(containerColor = AmberAccent.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, AmberAccent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = AmberAccent)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("PENDING PARTNER REGISTRATIONS", color = AmberAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("You have partner applicant forms waiting for background credentials verification.", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }

        Text("Platform Metrics Summary", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        // Metrics Grid (8 tiles)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(title = "Registered Users", value = "$userCount", icon = Icons.Filled.Person, iconColor = TealPrimary, modifier = Modifier.weight(1f))
                StatCard(title = "Total Repair Tickets", value = "$repairCount", icon = Icons.Filled.List, iconColor = Color.White, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(title = "Active Repairs", value = "$activeCount", icon = Icons.Filled.Build, iconColor = AmberAccent, modifier = Modifier.weight(1f))
                StatCard(title = "Completed Handbacks", value = "$completedCount", icon = Icons.Filled.CheckCircle, iconColor = AccentGreen, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(title = "Parts Catalog", value = "$partsCount items", icon = Icons.Filled.Settings, iconColor = TealPrimary, modifier = Modifier.weight(1f))
                StatCard(title = "Marketplace Listings", value = "$listingsCount Units", icon = Icons.Filled.ShoppingCart, iconColor = Color.White, modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(title = "Sold Devices", value = "$ordersCount Sales", icon = Icons.Filled.Check, iconColor = AccentGreen, modifier = Modifier.weight(1f))
                StatCard(title = "Pending Applications", value = "$pendingApplicationsCount Pending", icon = Icons.Filled.Warning, iconColor = AmberAccent, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun AdminApplicationsPanel(
    profilesList: List<com.example.data.UserProfile>,
    onApprove: (String, String) -> Unit,
    onReject: (String, String) -> Unit
) {
    val pendingTechnicians = remember(profilesList) {
        profilesList.filter { it.technicianStatus == "Pending" }
    }
    val pendingMasters = remember(profilesList) {
        profilesList.filter { it.repairMasterStatus == "Pending" }
    }
    val approvedProfiles = remember(profilesList) {
        profilesList.filter { it.technicianStatus == "Approved" || it.repairMasterStatus == "Approved" }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Partner Applicant Backlog", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
        }

        if (pendingTechnicians.isEmpty() && pendingMasters.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No partner registrations currently pending verification.", color = GrayText, fontSize = 13.sp)
                    }
                }
            }
        }

        items(pendingTechnicians) { profile ->
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, AmberAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(text = profile.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Applying for: Certified Technician Node", color = TealPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Contact: ${profile.phone}", color = GrayText, fontSize = 11.sp)
                            Text(text = "City: ${profile.city}", color = GrayText, fontSize = 11.sp)
                        }
                        Surface(color = AmberAccent.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                            Text("PENDING REVIEW", color = AmberAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onApprove(profile.phone, "Technician") },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_approve_tech_${profile.phone}")
                        ) {
                            Text("Verify & Approve", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Button(
                            onClick = { onReject(profile.phone, "Technician") },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_reject_tech_${profile.phone}")
                        ) {
                            Text("Reject", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        items(pendingMasters) { profile ->
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, AmberAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(text = profile.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Applying for: Repair Master Affiliate Shop Owner", color = AccentGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Contact: ${profile.phone}", color = GrayText, fontSize = 11.sp)
                            Text(text = "City: ${profile.city}", color = GrayText, fontSize = 11.sp)
                        }
                        Surface(color = AmberAccent.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                            Text("PENDING REVIEW", color = AmberAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onApprove(profile.phone, "Repair Master") },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_approve_master_${profile.phone}")
                        ) {
                            Text("Verify & Approve", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Button(
                            onClick = { onReject(profile.phone, "Repair Master") },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_reject_master_${profile.phone}")
                        ) {
                            Text("Reject", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        if (approvedProfiles.isNotEmpty()) {
            item {
                Text(text = "Approved Partnerships Log", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            }
            items(approvedProfiles) { approvedProf ->
                Card(colors = CardDefaults.cardColors(containerColor = NavySurface), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        if (approvedProf.technicianStatus == "Approved") {
                            Text(text = "✔ ${approvedProf.name} (${approvedProf.phone}) - Approved as Certified Technician Specialist", color = AccentGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        if (approvedProf.repairMasterStatus == "Approved") {
                            Text(text = "✔ ${approvedProf.name} (${approvedProf.phone}) - Approved as Retail Repair Master Lab Partner", color = AccentGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun getRoleBadgeColor(role: String): Color {
    return when (role.trim().uppercase()) {
        "ADMIN" -> AccentRed
        "COORDINATOR" -> AmberAccent
        "TECHNICIAN" -> TealPrimary
        "REPAIRMASTER" -> AccentGreen
        "CUSTOMER" -> Color(0xFF42A5F5)
        "MARKETPLACE_BUYER" -> Color(0xFFAB47BC)
        else -> Color.Gray
    }
}

@Composable
fun RoleAssignmentDialog(
    user: UserProfile,
    onRolesSaved: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val allPossibleRoles = listOf("CUSTOMER", "TECHNICIAN", "REPAIRMASTER", "COORDINATOR", "ADMIN", "MARKETPLACE_BUYER")
    val initialRoles = remember(user.role) {
        user.role.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }
    val selectedRoles = remember { mutableStateListOf<String>().apply { addAll(initialRoles) } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Assign Roles: ${user.name}",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 16.sp
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Permit multiple organizational roles to this user. They can switch dynamically.",
                    color = GrayText,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                allPossibleRoles.forEach { role ->
                    val isChecked = selectedRoles.contains(role)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                if (isChecked) {
                                    if (selectedRoles.size > 1) { // keep at least one role
                                        selectedRoles.remove(role)
                                    }
                                } else {
                                    selectedRoles.add(role)
                                }
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                if (checked) {
                                    if (!selectedRoles.contains(role)) selectedRoles.add(role)
                                } else {
                                    if (selectedRoles.size > 1) {
                                        selectedRoles.remove(role)
                                    }
                                }
                            },
                            colors = CheckboxDefaults.colors(checkedColor = TealPrimary)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = role,
                            color = if (isChecked) TealPrimary else Color.White,
                            fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onRolesSaved(selectedRoles.toList())
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text("Save Changes", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = GrayText)
            }
        },
        containerColor = NavySurface
    )
}

@Composable
fun AdminUsersPanel(
    usersList: List<UserProfile>,
    onRolesChanged: (String, List<String>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    // Filter out internal template profiles like "current_user"
    val filteredUsers = usersList.filter { 
        it.id != "current_user" && 
        (it.name.contains(searchQuery, ignoreCase = true) || it.phone.contains(searchQuery))
    }

    var selectedUserForRoles by remember { mutableStateOf<UserProfile?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Registered Users List (Real-Time Database)", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("Assign multiple official roles to newly hired admins or employees securely here.", color = GrayText, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp, bottom = 6.dp))
        }
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name or phone number...", color = Color.Gray, fontSize = 13.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = GrayBorder
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(filteredUsers.size) { index ->
            val user = filteredUsers[index]

            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, if (user.role.contains("ADMIN")) TealPrimary.copy(alpha = 0.5f) else GrayBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = user.name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            if (user.role.contains("ADMIN")) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = AmberAccent.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(0.5.dp, AmberAccent)
                                ) {
                                    Text("ADMIN OWNER", color = AmberAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                }
                            }
                        }
                        Text(text = "Phone: ${user.phone}", color = GrayText, fontSize = 11.sp)
                        Text(text = "City: ${user.city} | Email: ${user.email}", color = GrayText, fontSize = 10.sp)

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val roles = user.role.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            roles.forEach { r ->
                                Surface(
                                    color = getRoleBadgeColor(r).copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(0.5.dp, getRoleBadgeColor(r))
                                ) {
                                    Text(
                                        text = r,
                                        color = getRoleBadgeColor(r),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Role Clickable selector
                    Box {
                        Surface(
                            modifier = Modifier
                                .clickable { selectedUserForRoles = user }
                                .testTag("change_role_dropdown_${index}"),
                            color = TealPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Manage", color = TealPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedUserForRoles != null) {
        RoleAssignmentDialog(
            user = selectedUserForRoles!!,
            onRolesSaved = { chosenRoles ->
                onRolesChanged(selectedUserForRoles!!.phone, chosenRoles)
                selectedUserForRoles = null
            },
            onDismiss = { selectedUserForRoles = null }
        )
    }
}

@Composable
fun AdminPartsPanel(
    parts: List<Part>,
    onAddPart: (String, Double, String) -> Unit,
    onDeletePart: (Long) -> Unit,
    onUpdatePart: (Long, String, Double, String) -> Unit = { _, _, _, _ -> }
) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Display") }
    var searchQuery by remember { mutableStateOf("") }
    var editingPartId by remember { mutableStateOf<Long?>(null) }

    val categories = listOf("Display", "Battery", "Camera", "Port", "Chassis", "Audio", "Accessory")
    val filteredParts = parts.filter { it.name.contains(searchQuery, ignoreCase = true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Form to Add/Edit Part
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, if (editingPartId != null) AmberAccent else TealPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (editingPartId != null) "Edit Spare Part Details" else "Add Spare Parts to Inventory",
                        color = if (editingPartId != null) AmberAccent else TealPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("e.g. OEM OLED Panel (iPhone 14)", color = Color.Gray, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = if (editingPartId != null) AmberAccent else TealPrimary,
                            unfocusedBorderColor = GrayBorder
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_part_name_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            placeholder = { Text("Price (e.g. 119.00)", color = Color.Gray, fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = if (editingPartId != null) AmberAccent else TealPrimary,
                                unfocusedBorderColor = GrayBorder
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("admin_part_price_input")
                        )

                        // Category drop selection
                        var expandedCat by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.weight(1f)) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .clickable { expandedCat = true },
                                color = NavyDark,
                                border = BorderStroke(1.dp, GrayBorder),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = category, color = Color.White, fontSize = 13.sp)
                                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = GrayText)
                                }
                            }
                            DropdownMenu(
                                expanded = expandedCat,
                                onDismissRequest = { expandedCat = false },
                                modifier = Modifier.background(NavyLightSurface)
                            ) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat, color = Color.White, fontSize = 12.sp) },
                                        onClick = {
                                            category = cat
                                            expandedCat = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (editingPartId != null) {
                            OutlinedButton(
                                onClick = {
                                    editingPartId = null
                                    name = ""
                                    price = ""
                                    category = "Display"
                                },
                                border = BorderStroke(1.dp, Color.Gray),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                            ) {
                                Text("Cancel", color = Color.White, fontSize = 12.sp)
                            }
                        }

                        Button(
                            onClick = {
                                val doublePrice = price.toDoubleOrNull()
                                if (name.isNotBlank() && doublePrice != null) {
                                    if (editingPartId != null) {
                                        onUpdatePart(editingPartId!!, name, doublePrice, category)
                                    } else {
                                        onAddPart(name, doublePrice, category)
                                    }
                                    editingPartId = null
                                    name = ""
                                    price = ""
                                    category = "Display"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (editingPartId != null) AmberAccent else TealPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(if (editingPartId != null) 2f else 1f)
                                .height(40.dp)
                                .testTag("admin_submit_part_button")
                        ) {
                            Text(
                                text = if (editingPartId != null) "Save Part Changes" else "Add to Spare parts catalogue",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Search and lists
        item {
            Text(text = "Parts Catalog Listing", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search parts catalog...", color = Color.Gray, fontSize = 13.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = GrayBorder
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (filteredParts.isEmpty()) {
            item {
                Text("No parts matched your query.", color = GrayText, fontSize = 12.sp)
            }
        } else {
            items(filteredParts) { part ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, GrayBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = part.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${part.category} • ₹${String.format("%.2f", part.price)}", color = TealPrimary, fontSize = 11.sp)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = {
                                    editingPartId = part.id
                                    name = part.name
                                    price = part.price.toString()
                                    category = part.category
                                },
                                modifier = Modifier
                                    .background(AmberAccent.copy(alpha = 0.12f), CircleShape)
                                    .size(36.dp)
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit Part", tint = AmberAccent, modifier = Modifier.size(16.dp))
                            }

                            IconButton(
                                onClick = { onDeletePart(part.id) },
                                modifier = Modifier
                                    .background(AccentRed.copy(alpha = 0.12f), CircleShape)
                                    .size(36.dp)
                                    .testTag("admin_delete_part_${part.id}")
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = AccentRed, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSettingsPanel(
    serviceChargePct: String,
    onUpdateCharge: (String) -> Unit
) {
    var feeVal by remember { mutableStateOf(serviceChargePct) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Global Surcharge Configurations", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)

        Card(
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = BorderStroke(1.dp, GrayBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Platform Administrative Surcharge Fee (%)",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "This percentage fee is automatically appended to technician diagnostics invoices to fund administrative escrow accounts.",
                    color = GrayText,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = feeVal,
                        onValueChange = { feeVal = it },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = GrayBorder
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .width(80.dp)
                            .testTag("admin_service_charge_input")
                    )

                    Button(
                        onClick = { onUpdateCharge(feeVal) },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Save Changes", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminCareersPanel(
    postings: List<JobPosting>,
    onCreatePosting: (String, String, String, String) -> Unit,
    onClosePosting: (Long) -> Unit,
    onDeletePosting: (Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var roleType by remember { mutableStateOf("Technician") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val rolesList = listOf("Technician", "Repair Master")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, TealPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Publish Partner Job Opening", color = TealPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Job Title", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("e.g. Master Microsoldering Tech", color = Color.Gray, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = GrayBorder
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_job_title_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1.0f)) {
                            Text("Partner Role Type", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row {
                                rolesList.forEach { r ->
                                    val isSelected = roleType == r
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isSelected) TealPrimary else NavyLightSurface)
                                            .clickable { roleType = r }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = r, color = if (isSelected) Color.Black else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1.0f)) {
                            Text("Location", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = location,
                                onValueChange = { location = it },
                                placeholder = { Text("Nagpur, MH", color = Color.Gray, fontSize = 12.sp) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = TealPrimary,
                                    unfocusedBorderColor = GrayBorder
                                ),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("admin_job_location_input")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Job Description", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Outline credentials, schedules, motherboard tools...", color = Color.Gray, fontSize = 12.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = GrayBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .testTag("admin_job_desc_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank() && location.isNotBlank() && description.isNotBlank()) {
                                onCreatePosting(title, roleType, location, description)
                                title = ""
                                location = ""
                                description = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("admin_submit_job_button")
                    ) {
                        Text("Publish Opportunity Listing", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Text(text = "Active Job Openings", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
        }

        if (postings.isEmpty()) {
            item {
                Text("No career openings published.", color = GrayText, fontSize = 12.sp)
            }
        } else {
            items(postings) { posting ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, if (posting.isClosed) GrayBorder else TealPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(text = posting.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text(text = "${posting.roleType} • ${posting.location}", color = TealPrimary, fontSize = 11.sp)
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (!posting.isClosed) {
                                    IconButton(
                                        onClick = { onClosePosting(posting.id) },
                                        modifier = Modifier.background(AmberAccent.copy(alpha = 0.12f), CircleShape)
                                    ) {
                                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = AmberAccent, modifier = Modifier.size(16.dp))
                                    }
                                }
                                IconButton(
                                    onClick = { onDeletePosting(posting.id) },
                                    modifier = Modifier.background(AccentRed.copy(alpha = 0.12f), CircleShape)
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = AccentRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = posting.description, color = GrayText, fontSize = 12.sp, maxLines = 2)
                    }
                }
            }
        }
    }
}
