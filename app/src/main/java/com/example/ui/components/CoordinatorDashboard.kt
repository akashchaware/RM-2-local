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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RepairRequest
import com.example.data.Promotion
import com.example.data.Part
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.R
import com.example.ui.RepairViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

@Composable
fun CoordinatorDashboardView(
    viewModel: RepairViewModel,
    modifier: Modifier = Modifier
) {
    val requests by viewModel.allRepairRequests.collectAsState()
    val reviews by viewModel.allReviews.collectAsState()
    val promos by viewModel.promotions.collectAsState()
    val parts by viewModel.allParts.collectAsState()
    val searchVal by viewModel.coordinatorSearch
    val activeFilter by viewModel.coordinatorFilter

    var selectedRequestForAssign by remember { mutableStateOf<RepairRequest?>(null) }
    var selectedRequestForPreQuote by remember { mutableStateOf<RepairRequest?>(null) }
    var selectedDeskJobForQuote by remember { mutableStateOf<RepairRequest?>(null) }
    var selectedJobForPickupOtp by remember { mutableStateOf<RepairRequest?>(null) }
    var selectedJobForDeliveryOtp by remember { mutableStateOf<RepairRequest?>(null) }
    var selectedSubTab by remember { mutableStateOf(0) }

    // Roster of mock certified technicians
    val techniciansRoster = listOf(
        Pair(999L, "${viewModel.userProfile.value.name} (Self)"),
        Pair(101L, "Devendra Chaudhari"),
        Pair(102L, "Rahul Deshmukh"),
        Pair(103L, "Baburao Dharaskar"),
        Pair(104L, "Sanjay Mohite")
    )

    // Derived states
    val totalCount = requests.size
    val unassignedCount = requests.count { it.statusStep == 1 }
    val activeCount = requests.count { it.statusStep in 2..10 }
    val completedCount = requests.count { it.statusStep == 11 }

    // Pending Urgent Actions (Jobs in step 1 needing tech, or step 3 needing dispatch)
    val urgentJobs = requests.filter { it.statusStep == 1 || (it.statusStep == 3 && !it.isPickupOtpVerified) }

    // Filter requests
    val filteredRequests = requests.filter { req ->
        val matchesSearch = req.code.contains(searchVal, ignoreCase = true) ||
                "${req.deviceBrand} ${req.deviceModel}".contains(searchVal, ignoreCase = true) ||
                req.customerName.contains(searchVal, ignoreCase = true)

        val matchesFilter = when (activeFilter) {
            "Unassigned" -> req.statusStep == 1
            "Active" -> req.statusStep in 2..10
            "Completed" -> req.statusStep == 11
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and Role Tag
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "COORDINATION PLATFORM", color = TealPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "Central Dispatcher", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            RoleBadge(role = "COORDINATOR")
        }

        // Sub-tab Selector
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = NavySurface,
            contentColor = TealPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = { Text("Jobs & Dispatch", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (selectedSubTab == 0) TealPrimary else Color.White) }
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = { Text("Manage Offers", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (selectedSubTab == 1) TealPrimary else Color.White) }
            )
            Tab(
                selected = selectedSubTab == 2,
                onClick = { selectedSubTab = 2 },
                text = { Text("Part Inventory", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (selectedSubTab == 2) TealPrimary else Color.White) }
            )
        }

        if (selectedSubTab == 2) {
            AdminPartsPanel(
                parts = parts,
                onAddPart = { name, price, cat -> viewModel.addPart(name, price, cat) },
                onDeletePart = { viewModel.deletePart(it) },
                onUpdatePart = { id, name, price, cat -> viewModel.updatePart(id, name, price, cat) }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (selectedSubTab == 0) {
            // Stats Block Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard(
                        title = "Total Orders",
                        value = "$totalCount",
                        icon = Icons.Filled.List,
                        iconColor = TealPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Unassigned",
                        value = "$unassignedCount",
                        icon = Icons.Filled.Warning,
                        iconColor = AccentRed,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard(
                        title = "Active Repairs",
                        value = "$activeCount",
                        icon = Icons.Filled.Build,
                        iconColor = AmberAccent,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Handed Over",
                        value = "$completedCount",
                        icon = Icons.Filled.CheckCircle,
                        iconColor = AccentGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Urgent Pending Alert Panel
        if (urgentJobs.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AmberAccent.copy(alpha = 0.08f)),
                    border = BorderStroke(1.dp, AmberAccent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Warning, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PENDING DISPATCH ALERTS (${urgentJobs.size})",
                                color = AmberAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You have newly submitted smartphone repair requests awaiting certified technician matching and doorstep scheduling.",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // 2.5 My Self-Assigned Desk Tasks Section
        val deskTasks = requests.filter { (it.technicianId == 999L || it.repairMasterId == 999L) && it.statusStep < 11 }
        if (deskTasks.isNotEmpty()) {
            item {
                Text(
                    text = "My Self-Assigned Desk Tasks (${deskTasks.size})",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }
            items(deskTasks) { req ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val isTech = req.technicianId == 999L
                                    val isRM = req.repairMasterId == 999L
                                    val roleLabel = if (isTech && isRM) "Tech & Master" else if (isTech) "Technician" else "Repair Master"
                                    Surface(
                                        color = TealPrimary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = roleLabel.uppercase(),
                                            color = TealPrimary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${req.deviceBrand} ${req.deviceModel}",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = "Client: ${req.customerName} | Phone: ${req.customerPhone}",
                                    color = GrayText,
                                    fontSize = 11.sp
                                )
                            }
                            RepairStatusBadge(step = req.statusStep)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = GrayBorder)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Actions depending on statusStep
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            when (req.statusStep) {
                                2 -> {
                                    Text("Status: Assigned to me. Dispatching doorstep pickup is pending.", color = GrayText, fontSize = 12.sp)
                                    Button(
                                        onClick = { viewModel.advanceRepairStatus(req.id, 3) },
                                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Dispatch Doorstep Pickup (Step 3)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                                3 -> {
                                    Text("Status: Pickup initiated. Secure customer OTP is ${req.pickupOtpCode ?: ""}.", color = GrayText, fontSize = 12.sp)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { selectedJobForPickupOtp = req },
                                            colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
                                            modifier = Modifier.fillMaxWidth().testTag("coord_enter_pickup_otp_${req.id}"),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Verify Doorstep Pickup OTP", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                                4 -> {
                                    Text("Status: Device received at workshop lab.", color = GrayText, fontSize = 12.sp)
                                    Button(
                                        onClick = { viewModel.advanceRepairStatus(req.id, 5) },
                                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Start Lab Diagnosis (Step 5)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                                5 -> {
                                    Text("Status: Lab diagnosis is underway.", color = GrayText, fontSize = 12.sp)
                                    Button(
                                        onClick = { selectedDeskJobForQuote = req },
                                        colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Build & Transmit Quotation", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                                6 -> {
                                    Text("Status: Quotation pending client signoff (Total: ₹${String.format("%.2f", req.totalAmount)}).", color = GrayText, fontSize = 12.sp)
                                    Button(
                                        onClick = { viewModel.advanceRepairStatus(req.id, 7) },
                                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Force Approve Quote (Coord Override)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                                7 -> {
                                    Text("Status: Quote approved. Laboratory repair and testing is active.", color = GrayText, fontSize = 12.sp)
                                    Button(
                                        onClick = { viewModel.advanceRepairStatus(req.id, 8) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Finish Repair & QC Pass (Step 8)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                                8 -> {
                                    Text("Status: Quality control passed. Pending delivery dispatch.", color = GrayText, fontSize = 12.sp)
                                    Button(
                                        onClick = { viewModel.advanceRepairStatus(req.id, 9) },
                                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Dispatch Delivery Courier (Step 9)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                                9 -> {
                                    Text("Status: Courier out for delivery. Handover is pending.", color = GrayText, fontSize = 12.sp)
                                    Button(
                                        onClick = { viewModel.advanceRepairStatus(req.id, 10) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Arrived & Prepare Handover (Step 10)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                                10 -> {
                                    Text("Status: Handover verification active. Secure handover OTP is ${req.deliveryOtpCode ?: ""}.", color = GrayText, fontSize = 12.sp)
                                    Button(
                                        onClick = { selectedJobForDeliveryOtp = req },
                                        colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
                                        modifier = Modifier.fillMaxWidth().testTag("coord_enter_delivery_otp_${req.id}"),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Verify Handover OTP & Complete", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                                else -> {
                                    Text("Status: Active Lab Ticket (${req.statusStep})", color = GrayText, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Search & Status filters
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = searchVal,
                    onValueChange = { viewModel.coordinatorSearch.value = it },
                    placeholder = { Text("Search by code, model, client name...", color = Color.Gray, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = GrayText) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = TealPrimary,
                        unfocusedBorderColor = GrayBorder
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Tabs for filter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val filterOptions = listOf("All", "Unassigned", "Active", "Completed")
                    filterOptions.forEach { opt ->
                        val isSelected = activeFilter == opt
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.coordinatorFilter.value = opt },
                            color = if (isSelected) TealPrimary else NavySurface,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, if (isSelected) TealPrimary else GrayBorder)
                        ) {
                            Text(
                                text = opt,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Request list
        if (filteredRequests.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No matching repair tickets found.", color = GrayText, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(filteredRequests) { req ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, GrayBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectRequest(req.id) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = req.code, color = TealPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${req.deviceBrand} ${req.deviceModel}",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            RepairStatusBadge(step = req.statusStep)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Client: ${req.customerName} | Phone: ${req.customerPhone}", color = GrayText, fontSize = 12.sp)
                        Text(text = "City: ${req.city}", color = GrayText, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = GrayBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Assigned tech name
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Person, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = req.technicianName ?: "Unassigned Warning",
                                    color = if (req.technicianName != null) Color.White else AccentRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (req.statusStep == 1) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (req.repairMasterId == null) {
                                            Button(
                                                onClick = { selectedRequestForPreQuote = req },
                                                colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                modifier = Modifier
                                                    .height(32.dp)
                                                    .weight(1f)
                                                    .testTag("coord_prepare_prequote_button_${req.id}")
                                            ) {
                                                Text("Prepare Pre-Quote", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                            }
                                            Button(
                                                onClick = { viewModel.routePreQuote(req.id, "", 250.0, 999L) },
                                                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                modifier = Modifier
                                                    .height(32.dp)
                                                    .weight(1f)
                                                    .testTag("coord_self_assign_rm_button_${req.id}")
                                            ) {
                                                Text("Self-Assign RM", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                            }
                                        }
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (req.technicianId == null) {
                                            Button(
                                                onClick = { selectedRequestForAssign = req },
                                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                modifier = Modifier
                                                    .height(32.dp)
                                                    .weight(1f)
                                                    .testTag("coord_assign_button_${req.id}")
                                            ) {
                                                Text("Assign Tech", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                            }
                                            Button(
                                                onClick = { viewModel.assignTechnician(req.id, 999L, viewModel.userProfile.value.name) },
                                                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                                modifier = Modifier
                                                    .height(32.dp)
                                                    .weight(1f)
                                                    .testTag("coord_self_assign_tech_button_${req.id}")
                                            ) {
                                                Text("Self-Assign Tech", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                            }
                                        }
                                    }
                                    if (req.repairMasterId == null && req.technicianId == null) {
                                        Button(
                                            onClick = { viewModel.selfAssignBoth(req.id, viewModel.userProfile.value.name) },
                                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                            modifier = Modifier
                                                .height(32.dp)
                                                .fillMaxWidth()
                                                .testTag("coord_self_assign_both_button_${req.id}")
                                        ) {
                                            Icon(Icons.Filled.Person, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Self-Assign Both (Tech & RM)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                        }
                                    }
                                }
                            } else {
                                TextButton(
                                    onClick = { viewModel.selectRequest(req.id) },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("View Steps →", color = TealPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
            // Manage Offers subtab
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, GrayBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Submit New Promotional Offer",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        var promoTitle by remember { mutableStateOf("") }
                        var promoDesc by remember { mutableStateOf("") }
                        var promoPrice by remember { mutableStateOf("") }
                        var selectedImageOption by remember { mutableStateOf(0) } // 0: Dead Phone poster, 1: Lab diagnostic, 2: Doorstep Scooter

                        val imageOptions = listOf(
                            Pair("Check Dead Phone ₹250 Poster", "img_dead_phone_offer_1782763644093"),
                            Pair("Nagpur High-Tech Repair Lab", "img_indian_service_lab_1782739540576"),
                            Pair("Nagpur Doorstep Service Scooter", "img_indian_service_scooter_1782739563064")
                        )

                        OutlinedTextField(
                            value = promoTitle,
                            onValueChange = { promoTitle = it },
                            label = { Text("Offer Title (e.g., Check Dead Phone Offer)", color = Color.Gray, fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = TealPrimary,
                                unfocusedBorderColor = GrayBorder
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        )

                        OutlinedTextField(
                            value = promoDesc,
                            onValueChange = { promoDesc = it },
                            label = { Text("Short Description of service & terms", color = Color.Gray, fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = TealPrimary,
                                unfocusedBorderColor = GrayBorder
                            ),
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        )

                        OutlinedTextField(
                            value = promoPrice,
                            onValueChange = { promoPrice = it },
                            label = { Text("Offer Price in ₹ (e.g., 250)", color = Color.Gray, fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = TealPrimary,
                                unfocusedBorderColor = GrayBorder
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Choose Advertisement Banner Style:",
                            color = GrayText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        imageOptions.forEachIndexed { index, option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedImageOption = index }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedImageOption == index,
                                    onClick = { selectedImageOption = index },
                                    colors = RadioButtonDefaults.colors(selectedColor = TealPrimary, unselectedColor = Color.Gray)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = option.first, color = Color.White, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (promoTitle.isNotBlank() && promoDesc.isNotBlank() && promoPrice.isNotBlank()) {
                                    val priceVal = promoPrice.toDoubleOrNull() ?: 250.0
                                    viewModel.insertPromotion(
                                        title = promoTitle,
                                        description = promoDesc,
                                        offerPrice = priceVal,
                                        drawableResName = imageOptions[selectedImageOption].second
                                    )
                                    // Reset fields
                                    promoTitle = ""
                                    promoDesc = ""
                                    promoPrice = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Submit & Broadcast Promotional Offer", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // List of promotions
            if (promos.isEmpty()) {
                item {
                    Text(text = "No active promotions found. Add one above to display it on the platform.", color = GrayText, fontSize = 12.sp, modifier = Modifier.padding(16.dp))
                }
            } else {
                item {
                    Text(
                        text = "Active Broadcasted Promotions (${promos.size})",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(promos) { promo ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                        border = BorderStroke(1.dp, GrayBorder),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Image
                            val imgId = if (promo.drawableResName == "img_dead_phone_offer_1782763644093") {
                                R.drawable.img_dead_phone_offer_1782763644093
                            } else if (promo.drawableResName == "img_indian_service_lab_1782739540576") {
                                R.drawable.img_indian_service_lab_1782739540576
                            } else {
                                R.drawable.img_indian_service_scooter_1782739563064
                            }

                            Image(
                                painter = painterResource(id = imgId),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = promo.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = promo.description, color = GrayText, fontSize = 11.sp, maxLines = 2)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = TealPrimary,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "₹${promo.offerPrice.toInt()}",
                                            color = Color.Black,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            IconButton(
                                onClick = { viewModel.deletePromotion(promo.id) }
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete Offer", tint = AccentRed)
                            }
                        }
                    }
                }
            }
        }
    }
}
}

    // Assign Technician Dialog
    if (selectedRequestForAssign != null) {
        val targetReq = selectedRequestForAssign!!
        AlertDialog(
            onDismissRequest = { selectedRequestForAssign = null },
            title = { Text("Assign Certified Technician", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Match and allocate a skilled doorstep technician to repair ${targetReq.customerName}'s ${targetReq.deviceBrand} ${targetReq.deviceModel}.",
                        color = GrayText,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    techniciansRoster.forEach { (techId, techName) ->
                        val techReviews = reviews.filter { it.technicianId == techId }
                        val avgRating = if (techReviews.isEmpty()) 5.0 else techReviews.map { it.techRating }.average()
                        val ratingText = if (techReviews.isEmpty()) "No ratings yet" else "⭐ ${String.format("%.1f", avgRating)} (${techReviews.size} reviews)"

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.assignTechnician(targetReq.id, techId, techName)
                                    selectedRequestForAssign = null
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(TealPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Person, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = techName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = "Certified Repair Specialist", color = GrayText, fontSize = 11.sp)
                            }
                            Text(
                                text = ratingText,
                                color = AmberAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Divider(color = GrayBorder.copy(alpha = 0.4f))
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedRequestForAssign = null }) {
                    Text("Close", color = GrayText)
                }
            },
            containerColor = NavySurface
        )
    }

    // Pre-Quote Preparation Dialog
    if (selectedRequestForPreQuote != null) {
        val targetReq = selectedRequestForPreQuote!!
        
        // Define default major repair spare requirements depending on Device Type / Reported Issue
        val isSolar = targetReq.issueDescription.contains("Solar", ignoreCase = true) || targetReq.issueDescription.contains("panel", ignoreCase = true)
        val isTV = targetReq.issueDescription.contains("TV", ignoreCase = true) || targetReq.issueDescription.contains("LED", ignoreCase = true)

        val defaultSpares = remember(targetReq.id) {
            when {
                isSolar -> listOf(
                    Pair("Solar Inverter Module", 4999.0),
                    Pair("Solar Junction Box & Wiring", 1599.0),
                    Pair("Solar Cell Panel Polish / Repair", 2199.0),
                    Pair("Efficiency Optimizer / Diode Swap", 899.0)
                )
                isTV -> listOf(
                    Pair("LED Backlight Strip Array", 2499.0),
                    Pair("Motherboard Board Swapping", 3999.0),
                    Pair("HDMI/USB Port Hub Panel", 1199.0),
                    Pair("Power Supply Inverter Board", 1899.0)
                )
                else -> listOf(
                    Pair("Screen Display Assembly", 2999.0),
                    Pair("Battery Cell Swap", 1499.0),
                    Pair("Charging Connector Port", 799.0),
                    Pair("Main Logic Board Micro-soldering", 3499.0),
                    Pair("Camera Lens Module", 1899.0)
                )
            }
        }

        val selectedSpares = remember { mutableStateMapOf<String, Boolean>().apply {
            defaultSpares.forEach { this[it.first] = false }
        } }

        var customServiceCharge by remember { mutableStateOf("250") }
        var selectedRmId by remember { mutableStateOf(101L) } // Default: Pranay Pathak

        val mockRepairMasters = listOf(
            Triple(101L, "Pranay Pathak", "Nagpur Central Workshop (Sitabuldi)"),
            Triple(102L, "Rahul Deshmukh", "Sitabuldi Doorstep Hub"),
            Triple(103L, "Baburao Dharaskar", "Dharampeth Repairs Hub")
        )

        AlertDialog(
            onDismissRequest = { selectedRequestForPreQuote = null },
            title = {
                Text(
                    text = "Analyze & Prepare Pre-Quote",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "ANALYZE REPORTED ISSUE",
                        color = TealPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NavyLightSurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "Device: ${targetReq.deviceBrand} ${targetReq.deviceModel}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "Issue: ${targetReq.issueDescription}", color = GrayText, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                            Text(text = "Region: ${targetReq.city}", color = GrayText, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "SELECT MAJOR REPAIR SPARES",
                        color = TealPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    defaultSpares.forEach { (spareName, price) ->
                        val isChecked = selectedSpares[spareName] ?: false
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedSpares[spareName] = !isChecked }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { selectedSpares[spareName] = it },
                                colors = CheckboxDefaults.colors(checkedColor = TealPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = spareName, color = Color.White, fontSize = 13.sp)
                                Text(text = "Est: ₹${price.toInt()}", color = GrayText, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "ESTIMATED SERVICE CHARGE (₹)",
                        color = TealPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = customServiceCharge,
                        onValueChange = { customServiceCharge = it },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = GrayBorder
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "ROUTE TO LAB WORKSHOP (REPAIRMASTER)",
                        color = TealPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    mockRepairMasters.forEach { (rmId, rmName, rmLoc) ->
                        val isSelected = selectedRmId == rmId
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedRmId = rmId }
                                .border(
                                    1.dp,
                                    if (isSelected) TealPrimary else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .background(
                                    if (isSelected) TealPrimary.copy(alpha = 0.08f) else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedRmId = rmId },
                                colors = RadioButtonDefaults.colors(selectedColor = TealPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = rmName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(text = rmLoc, color = GrayText, fontSize = 11.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = AccentGreen.copy(alpha = 0.08f)),
                        border = BorderStroke(1.dp, AccentGreen.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Lock, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🔒 PRIVACY GUARD ACTIVE: Client details (Name, Phone, Doorstep) are 100% masked. Only device specifications and general locality are shared with RepairMaster.",
                                color = Color.White,
                                fontSize = 10.sp,
                                lineHeight = 13.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val activeSparesList = defaultSpares.filter { selectedSpares[it.first] == true }
                        val partsString = activeSparesList.joinToString(";") { "${it.first},${it.second},1" }
                        val sc = customServiceCharge.toDoubleOrNull() ?: 250.0
                        viewModel.routePreQuote(targetReq.id, partsString, sc, selectedRmId)
                        selectedRequestForPreQuote = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Route Pre-Quote", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRequestForPreQuote = null }) {
                    Text("Cancel", color = GrayText)
                }
            },
            containerColor = NavySurface
        )
    }

    if (selectedDeskJobForQuote != null) {
        val targetJob = selectedDeskJobForQuote!!
        val dbParts = parts.filter { it.category.contains("Spare", ignoreCase = true) || it.category.contains("Part", ignoreCase = true) || it.category.contains("Mobile", ignoreCase = true) || it.category.contains("Screen", ignoreCase = true) }
        val displayedParts = if (dbParts.isNotEmpty()) dbParts else listOf(
            Part(id = 1, name = "Screen Display Assembly", price = 2999.0, category = "Display"),
            Part(id = 2, name = "Premium Lithium Battery", price = 1499.0, category = "Power"),
            Part(id = 3, name = "USB-C Charging Connector Board", price = 799.0, category = "Charging"),
            Part(id = 4, name = "Camera Lens Glass & Module", price = 1199.0, category = "Camera")
        )

        val selectedPartsCart = remember { mutableStateMapOf<Long, Int>() }
        var customServiceCharge by remember { mutableStateOf("300") }

        AlertDialog(
            onDismissRequest = { selectedDeskJobForQuote = null },
            title = {
                Text("Build & Submit Quotation", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Select required spare parts & set the service charge for this repair job.", color = GrayText, fontSize = 12.sp)

                    Text("SELECT SPARES", color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    displayedParts.forEach { part ->
                        val count = selectedPartsCart[part.id] ?: 0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(NavyDark, RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(part.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text("Price: ₹${part.price}", color = TealPrimary, fontSize = 11.sp)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (count > 0) {
                                    IconButton(
                                        onClick = { selectedPartsCart[part.id] = count - 1 },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Decrease", tint = Color.White)
                                    }
                                    Text("$count", color = Color.White, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 8.dp))
                                }
                                IconButton(
                                    onClick = { selectedPartsCart[part.id] = count + 1 },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = "Increase", tint = TealPrimary)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("LABOR SERVICE FEE", color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    OutlinedTextField(
                        value = customServiceCharge,
                        onValueChange = { customServiceCharge = it },
                        prefix = { Text("₹", color = TealPrimary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = GrayBorder
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = GrayBorder)
                    Spacer(modifier = Modifier.height(6.dp))

                    val subtotal = displayedParts.sumOf { (selectedPartsCart[it.id] ?: 0) * it.price }
                    val laborFee = customServiceCharge.toDoubleOrNull() ?: 0.0
                    val adminFee = (subtotal + laborFee) * (targetJob.serviceChargePercent / 100.0)
                    val finalTotal = subtotal + laborFee + adminFee

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Parts Subtotal:", color = GrayText, fontSize = 12.sp)
                        Text("₹${String.format("%.2f", subtotal)}", color = Color.White, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Platform Fee (${targetJob.serviceChargePercent}%):", color = GrayText, fontSize = 12.sp)
                        Text("₹${String.format("%.2f", adminFee)}", color = GrayText, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Final Quote Total:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("₹${String.format("%.2f", finalTotal)}", color = TealPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val chosenParts = displayedParts.filter { (selectedPartsCart[it.id] ?: 0) > 0 }.map { Pair(it, selectedPartsCart[it.id]!!) }
                        val laborFee = customServiceCharge.toDoubleOrNull() ?: 300.0
                        viewModel.submitQuotation(targetJob.id, chosenParts, laborFee)
                        selectedDeskJobForQuote = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Text("Transmit Quote to Client", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedDeskJobForQuote = null }) {
                    Text("Cancel", color = GrayText)
                }
            },
            containerColor = NavySurface
        )
    }

    // 1. Forced Coordinator Self-Assign Pickup OTP Dialog
    if (selectedJobForPickupOtp != null) {
        var code by remember { mutableStateOf("") }
        var error by remember { mutableStateOf<String?>(null) }
        val targetJob = selectedJobForPickupOtp!!

        AlertDialog(
            onDismissRequest = { selectedJobForPickupOtp = null },
            title = { Text("Secure Pickup OTP (Self-Assign)", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Verify customer ${targetJob.customerName}'s 4-digit code to securely confirm device pickup handover.",
                        color = GrayText,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Display Maintenance Mode Privacy Status & Dead/Blank Warning
                    if (targetJob.isDeviceDeadOrBlank) {
                        Surface(
                            color = AmberAccent.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, AmberAccent.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Info, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "BYPASS EXCEPTION ACTIVE: Customer declared device is dead/blank. Physical maintenance mode verification is waived.",
                                    color = AmberAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else if (targetJob.isMaintenanceModeEnabled) {
                        Surface(
                            color = AccentGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, AccentGreen.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Maintenance Mode is ACTIVE & SECURED. Customer data is fully isolated.",
                                    color = AccentGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Surface(
                            color = AccentRed.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, AccentRed.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Warning, contentDescription = null, tint = AccentRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "FORCE WARNING: Maintenance Mode is NOT active! Ask customer to enable it before handover, unless device is dead/blank.",
                                    color = AccentRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it; error = null },
                        placeholder = { Text("Enter 4-digit code (e.g. 4321)", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = GrayBorder
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("coord_pickup_otp_input")
                    )

                    if (error != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = error!!, color = AccentRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.verifyPickupOtp(
                            requestId = targetJob.id,
                            enteredOtp = code,
                            onSuccess = { selectedJobForPickupOtp = null },
                            onError = { error = it }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    modifier = Modifier.testTag("coord_pickup_otp_confirm_btn")
                ) {
                    Text("Verify & Collect", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedJobForPickupOtp = null }) {
                    Text("Cancel", color = GrayText)
                }
            },
            containerColor = NavySurface
        )
    }

    // 2. Forced Coordinator Self-Assign Delivery OTP Dialog
    if (selectedJobForDeliveryOtp != null) {
        var code by remember { mutableStateOf("") }
        var error by remember { mutableStateOf<String?>(null) }
        val targetJob = selectedJobForDeliveryOtp!!

        AlertDialog(
            onDismissRequest = { selectedJobForDeliveryOtp = null },
            title = { Text("Secure Delivery Handover (Self-Assign)", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Verify customer ${targetJob.customerName}'s 4-digit delivery handover OTP to securely complete and close the ticket.",
                        color = GrayText,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it; error = null },
                        placeholder = { Text("Enter 4-digit delivery code (e.g. 8765)", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = GrayBorder
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("coord_delivery_otp_input")
                    )

                    if (error != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = error!!, color = AccentRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.verifyDeliveryOtp(
                            requestId = targetJob.id,
                            enteredOtp = code,
                            onSuccess = { selectedJobForDeliveryOtp = null },
                            onError = { error = it }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                    modifier = Modifier.testTag("coord_delivery_otp_confirm_btn")
                ) {
                    Text("Verify & Complete Ticket", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedJobForDeliveryOtp = null }) {
                    Text("Cancel", color = GrayText)
                }
            },
            containerColor = NavySurface
        )
    }
}
