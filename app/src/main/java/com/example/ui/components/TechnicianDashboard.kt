package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.text.BasicTextField
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
import com.example.data.Part
import com.example.data.RepairRequest
import com.example.ui.RepairViewModel
import com.example.ui.theme.*

@Composable
fun TechnicianDashboardView(
    viewModel: RepairViewModel,
    modifier: Modifier = Modifier
) {
    val requests by viewModel.allRepairRequests.collectAsState()
    val reviews by viewModel.allReviews.collectAsState()
    val parts by viewModel.allParts.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val myRequests = remember(requests, userProfile) {
        requests.filter { it.technicianName.equals(userProfile.name, ignoreCase = true) }
    }
    val activeJobs = myRequests.filter { it.statusStep < 11 }
    val completedJobs = myRequests.filter { it.statusStep == 11 }

    var selectedJobForQuote by remember { mutableStateOf<RepairRequest?>(null) }
    var selectedJobForPickupOtp by remember { mutableStateOf<RepairRequest?>(null) }
    var selectedJobForDeliveryOtp by remember { mutableStateOf<RepairRequest?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "TECHNICIAN TERMINAL", color = TealPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(text = userProfile.name, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
                RoleBadge(role = "TECHNICIAN")
            }
        }

        // Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Active Jobs",
                    value = "${activeJobs.size}",
                    icon = Icons.Filled.Build,
                    iconColor = AmberAccent,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Completed",
                    value = "${completedJobs.size}",
                    icon = Icons.Filled.CheckCircle,
                    iconColor = AccentGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // My Service Profile Rating & Feedback Section
        item {
            val techReviews = reviews.filter { it.technicianName.equals(userProfile.name, ignoreCase = true) || it.technicianId == 101L }
            val avgRating = if (techReviews.isEmpty()) 5.0 else techReviews.map { it.techRating }.average()
            
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, AmberAccent.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = AmberAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "MY SERVICE PROFILE RATING",
                                color = AmberAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "⭐ ${String.format("%.1f", avgRating)} / 5.0",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Based on ${techReviews.size} secure doorstep feedback reviews.",
                        color = GrayText,
                        fontSize = 12.sp
                    )

                    if (techReviews.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Customer Feedback Highlights:",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            techReviews.take(3).forEach { rev ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(NavyDark, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = rev.customerName, color = TealPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            repeat(rev.techRating) {
                                                Icon(Icons.Filled.Star, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(12.dp))
                                            }
                                        }
                                    }
                                    if (rev.comment.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = rev.comment, color = Color.White, fontSize = 12.sp, lineHeight = 16.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Repair Requests Section
        item {
            Text(
                text = "Active Handbags & Jobs",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (activeJobs.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(text = "No active repair jobs assigned.", color = GrayText, fontSize = 13.sp)
                    }
                }
            }
        } else {
            items(activeJobs) { job ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GrayBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = job.code, color = TealPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(text = "${job.deviceBrand} ${job.deviceModel}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            RepairStatusBadge(step = job.statusStep)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Client: ${job.customerName} (${job.customerPhone})", color = Color.White, fontSize = 13.sp)
                        Text(text = "Reported issue: ${job.issueDescription}", color = GrayText, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Actions depending on current step
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Next Step: ${job.statusStep + 1}/11",
                                color = GrayText,
                                fontSize = 12.sp
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                when (job.statusStep) {
                                    2 -> { // Assigned -> Dispatch Pickup
                                        Button(
                                            onClick = { viewModel.advanceRepairStatus(job.id, 3) },
                                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("tech_dispatch_pickup_${job.id}")
                                        ) {
                                            Text("Dispatch Pickup", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                    3 -> { // Pickup -> Verify OTP
                                        Button(
                                            onClick = { selectedJobForPickupOtp = job },
                                            colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("tech_enter_pickup_otp_${job.id}")
                                        ) {
                                            Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Enter Pickup OTP", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                    4 -> { // Collected -> Start Diagnosis
                                        Button(
                                            onClick = { viewModel.advanceRepairStatus(job.id, 5) },
                                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Start Diagnosis", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                    5 -> { // Diagnosing -> Build Quotation
                                        Button(
                                            onClick = { selectedJobForQuote = job },
                                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("tech_build_quote_${job.id}")
                                        ) {
                                            Text("Build Quotation", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                    6 -> { // Awaiting approval
                                        Text(text = "Awaiting Customer Signoff", color = AmberAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    7 -> { // Approved -> Complete Repair
                                        Button(
                                            onClick = { viewModel.advanceRepairStatus(job.id, 8) },
                                            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Finish Repair", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                    8 -> { // Completed repair -> Dispatch Delivery
                                        Button(
                                            onClick = { viewModel.advanceRepairStatus(job.id, 9) },
                                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Dispatch Delivery", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                    9 -> { // Handing over -> Arrived OTP check
                                        Button(
                                            onClick = { viewModel.advanceRepairStatus(job.id, 10) },
                                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Arrived & Complete", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                    10 -> { // Delivery OTP
                                        Button(
                                            onClick = { selectedJobForDeliveryOtp = job },
                                            colors = ButtonDefaults.buttonColors(containerColor = AmberAccent),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("tech_enter_delivery_otp_${job.id}")
                                        ) {
                                            Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Enter Handover OTP", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Completed History Section
        item {
            Text(
                text = "My Completed Repairs History",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (completedJobs.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(text = "No historical completed repairs.", color = GrayText, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(completedJobs) { job ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    shape = RoundedCornerShape(10.dp),
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
                            Text(text = job.code, color = GrayText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${job.deviceBrand} ${job.deviceModel}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Customer: ${job.customerName}", color = GrayText, fontSize = 12.sp)
                        }
                        Text(text = "₹${String.format("%.2f", job.totalAmount)}", color = AccentGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // 1. Pickup OTP dialog
    if (selectedJobForPickupOtp != null) {
        var code by remember { mutableStateOf("") }
        var error by remember { mutableStateOf<String?>(null) }
        val targetJob = selectedJobForPickupOtp!!

        AlertDialog(
            onDismissRequest = { selectedJobForPickupOtp = null },
            title = { Text("Secure Pickup OTP Verification", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Verify customer ${targetJob.customerName}'s 4-digit code to securely confirm device pickup handover.",
                        color = GrayText,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Display Maintenance Mode Privacy Status
                    if (targetJob.isMaintenanceModeEnabled) {
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
                                    text = "WARNING: Maintenance Mode is NOT active yet. Please ask the customer to enable it on their device for their data privacy.",
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tech_pickup_otp_input")
                    )
                    if (error != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = error!!, color = AccentRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.verifyPickupOtp(
                            targetJob.id,
                            code,
                            onSuccess = { selectedJobForPickupOtp = null },
                            onError = { error = it }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    modifier = Modifier.testTag("tech_confirm_pickup_otp")
                ) {
                    Text("Verify Handover", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedJobForPickupOtp = null }) { Text("Cancel", color = GrayText) }
            },
            containerColor = NavySurface
        )
    }

    // 2. Delivery OTP dialog
    if (selectedJobForDeliveryOtp != null) {
        var code by remember { mutableStateOf("") }
        var error by remember { mutableStateOf<String?>(null) }
        val targetJob = selectedJobForDeliveryOtp!!

        AlertDialog(
            onDismissRequest = { selectedJobForDeliveryOtp = null },
            title = { Text("Secure Handover OTP Verification", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Verify customer ${targetJob.customerName}'s 4-digit handover code to close the ticket and receive your payment.",
                        color = GrayText,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it; error = null },
                        placeholder = { Text("Enter 4-digit code (e.g. 8765)", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = GrayBorder
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tech_delivery_otp_input")
                    )
                    if (error != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = error!!, color = AccentRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.verifyDeliveryOtp(
                            targetJob.id,
                            code,
                            onSuccess = { selectedJobForDeliveryOtp = null },
                            onError = { error = it }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    modifier = Modifier.testTag("tech_confirm_delivery_otp")
                ) {
                    Text("Verify Handover", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedJobForDeliveryOtp = null }) { Text("Cancel", color = GrayText) }
            },
            containerColor = NavySurface
        )
    }

    // 3. Complete Quotation Builder Workspace
    if (selectedJobForQuote != null) {
        val targetJob = selectedJobForQuote!!
        var searchPartQuery by remember { mutableStateOf("") }
        var customServiceCharge by remember { mutableStateOf("25") }
        val selectedPartsCart = remember { mutableStateMapOf<Long, Int>() } // PartId -> Qty

        val filteredParts = parts.filter { it.name.contains(searchPartQuery, ignoreCase = true) }

        AlertDialog(
            onDismissRequest = { selectedJobForQuote = null },
            title = {
                Text(
                    text = "Build Diagnosis Quotation [${targetJob.code}]",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 450.dp)
                ) {
                    Text(
                        text = "Select required replacement parts, adjust quantities, and specify your diagnostic service fee.",
                        fontSize = 12.sp,
                        color = GrayText,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Parts Selector Panel
                    OutlinedTextField(
                        value = searchPartQuery,
                        onValueChange = { searchPartQuery = it },
                        placeholder = { Text("Search parts catalogue...", color = Color.Gray, fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = GrayText) },
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
                    )

                    // Parts list scrollbox
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .background(NavyDark, RoundedCornerShape(8.dp))
                            .padding(6.dp)
                    ) {
                        filteredParts.forEach { part ->
                            val currentQty = selectedPartsCart[part.id] ?: 0
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = part.name, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "${part.category} • ₹${String.format("%.2f", part.price)}", color = TealPrimary, fontSize = 11.sp)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (currentQty > 0) {
                                        IconButton(
                                            onClick = {
                                                if (currentQty == 1) selectedPartsCart.remove(part.id)
                                                else selectedPartsCart[part.id] = currentQty - 1
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Filled.RemoveCircle, contentDescription = null, tint = AccentRed, modifier = Modifier.size(18.dp))
                                        }
                                        Text(text = "$currentQty", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp))
                                    }
                                    IconButton(
                                        onClick = { selectedPartsCart[part.id] = currentQty + 1 },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Filled.AddCircle, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                            Divider(color = GrayBorder.copy(alpha = 0.5f))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Calculations Review Box
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NavyLightSurface),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            // Service labor input
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Diagnosis Labor Fee ($)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                BasicTextField(
                                    value = customServiceCharge,
                                    onValueChange = { customServiceCharge = it },
                                    textStyle = androidx.compose.ui.text.TextStyle(color = TealPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp),
                                    modifier = Modifier
                                        .width(60.dp)
                                        .background(NavyDark, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                        .testTag("quote_labor_input")
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Calculate live tallies
                            val chosenParts = parts.filter { selectedPartsCart.containsKey(it.id) }.map { Pair(it, selectedPartsCart[it.id]!!) }
                            val subtotal = chosenParts.sumOf { it.first.price * it.second }
                            val laborFee = customServiceCharge.toDoubleOrNull() ?: 0.0
                            val adminFee = (subtotal + laborFee) * (targetJob.serviceChargePercent / 100.0)
                            val finalTotal = subtotal + laborFee + adminFee

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Parts Subtotal:", color = GrayText, fontSize = 11.sp)
                                Text(text = "₹${String.format("%.2f", subtotal)}", color = Color.White, fontSize = 11.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Platform Fee (${targetJob.serviceChargePercent}%):", color = GrayText, fontSize = 11.sp)
                                Text(text = "₹${String.format("%.2f", adminFee)}", color = GrayText, fontSize = 11.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Final Quote Total:", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(text = "₹${String.format("%.2f", finalTotal)}", color = TealPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val chosenParts = parts.filter { selectedPartsCart.containsKey(it.id) }.map { Pair(it, selectedPartsCart[it.id]!!) }
                        val laborFee = customServiceCharge.toDoubleOrNull() ?: 25.0
                        viewModel.submitQuotation(targetJob.id, chosenParts, laborFee)
                        selectedJobForQuote = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    modifier = Modifier.testTag("submit_quote_to_client_button")
                ) {
                    Text("Transmit Quote to Client", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedJobForQuote = null }) { Text("Cancel", color = GrayText) }
            },
            containerColor = NavySurface
        )
    }
}
