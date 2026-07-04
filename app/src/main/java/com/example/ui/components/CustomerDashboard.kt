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
import com.example.ui.RepairViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

@Composable
fun CustomerDashboardView(
    viewModel: RepairViewModel,
    modifier: Modifier = Modifier
) {
    val requestsAll by viewModel.allRepairRequests.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    val requests = remember(requestsAll, userProfile) {
        requestsAll.filter { it.customerPhone == userProfile.phone }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Welcome Back,",
                                color = GrayText,
                                fontSize = 14.sp
                            )
                            Text(
                                text = userProfile.name,
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        RepairMasterLogo(sizeDp = 64)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Professional smartphone repairs delivered straight to your doorstep. Schedule a service, track in real-time, and approve quotes securely.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.navigateTo(Screen.NewRepairRequest) },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("book_repair_entry_button")
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Book New Doorstep Repair",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Active Repair Trackers Section
        item {
            Text(
                text = "My Repair Tickets",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        if (requests.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Build,
                            contentDescription = null,
                            tint = GrayText,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No repair tickets booked yet.",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Click the button above to request doorstep pickup for your damaged device.",
                            color = GrayText,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(requests) { ticket ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectRequest(ticket.id) }
                        .testTag("repair_ticket_card_${ticket.id}"),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GrayBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = ticket.code,
                                    color = TealPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${ticket.deviceBrand} ${ticket.deviceModel}",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            RepairStatusBadge(step = ticket.statusStep)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Issue: ${ticket.issueDescription}",
                            color = GrayText,
                            fontSize = 13.sp,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Divider(color = GrayBorder)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = GrayText,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = ticket.city, color = GrayText, fontSize = 12.sp)
                            }

                            Text(
                                text = "Track Progress →",
                                color = TealPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewRepairRequestView(
    viewModel: RepairViewModel,
    modifier: Modifier = Modifier
) {
    var deviceType by remember { mutableStateOf("Smartphone") }
    var deviceTypeExpanded by remember { mutableStateOf(false) }

    var brand by remember { mutableStateOf("Apple") }
    var brandExpanded by remember { mutableStateOf(false) }

    var model by remember { mutableStateOf("") }

    var selectedIssue by remember { mutableStateOf("Dead Phone") }
    var issueExpanded by remember { mutableStateOf(false) }

    var additionalDesc by remember { mutableStateOf("") }

    var selectedRegion by remember { mutableStateOf("Nagpur") }
    var selectedLocality by remember { mutableStateOf("Sitabuldi") }
    var localityExpanded by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Synchronize defaults on deviceType changes
    LaunchedEffect(deviceType) {
        brand = when (deviceType) {
            "Smartphone", "Smartwatch" -> "Apple"
            "TV", "LED" -> "Samsung"
            "Solar panel" -> "Tata Power"
            else -> "Other"
        }
        selectedIssue = when (deviceType) {
            "Smartphone", "Smartwatch" -> "Dead Phone"
            "TV", "LED" -> "Screen Backlight Repair"
            "Solar panel" -> "Inverter Faulty"
            else -> "Other"
        }
    }

    val deviceTypes = listOf("Smartphone", "TV", "LED", "Smartwatch", "Solar panel")

    val brandsForType = when (deviceType) {
        "Smartphone", "Smartwatch" -> listOf("Apple", "Samsung", "Google", "OnePlus", "Xiaomi", "Realme", "Other")
        "TV", "LED" -> listOf("Samsung", "Sony", "LG", "Mi", "OnePlus", "Other")
        "Solar panel" -> listOf("Tata Power", "Luminous", "Adani Solar", "Waaree", "Havells", "Other")
        else -> listOf("Other")
    }

    val issuesForType = when (deviceType) {
        "Smartphone", "Smartwatch" -> listOf("Dead Phone", "Screen Replacement", "Battery Saturated / Swap", "Water Damage", "Charging Port Fault", "Camera / Speaker Bug", "Other")
        "TV", "LED" -> listOf("Screen Backlight Repair", "No Power / Motherboard", "Display Line / Color Distortion", "HDMI / Ports Repair", "Other")
        "Solar panel" -> listOf("Inverter Faulty", "Physical Crack / Cleaning", "Wiring & Connection Failure", "Efficiency Optimization", "Other")
        else -> listOf("Other")
    }

    val regions = listOf(
        Pair("Nagpur", "Active"),
        Pair("Pune", "Under Development - Inactive"),
        Pair("Mumbai", "Under Development - Inactive"),
        Pair("Thane", "Under Development - Inactive"),
        Pair("Nashik", "Under Development - Inactive")
    )

    val nagpurLocalities = listOf(
        "Sitabuldi",
        "Manewada",
        "Wadi",
        "Gorewada",
        "Dharampeth",
        "Sadar",
        "Nandanvan",
        "Wardha",
        "Amravati",
        "Chandrapur",
        "Bhandara"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Back Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            IconButton(
                onClick = { viewModel.navigateTo(Screen.CustomerDashboard) },
                modifier = Modifier.background(NavySurface, CircleShape)
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Book Doorstep Repair",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Device Information",
                    color = TealPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 1. Device Type Dropdown
                Text(
                    text = "Device Type",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = deviceType,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "Expand Device Type dropdown",
                                tint = TealPrimary,
                                modifier = Modifier.clickable { deviceTypeExpanded = !deviceTypeExpanded }
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = GrayBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { deviceTypeExpanded = !deviceTypeExpanded }
                            .testTag("repair_device_type_input")
                    )
                    DropdownMenu(
                        expanded = deviceTypeExpanded,
                        onDismissRequest = { deviceTypeExpanded = false },
                        modifier = Modifier
                            .background(NavySurface)
                            .border(1.dp, GrayBorder)
                            .fillMaxWidth(0.85f)
                    ) {
                        deviceTypes.forEach { dType ->
                            DropdownMenuItem(
                                text = { Text(text = dType, color = Color.White) },
                                onClick = {
                                    deviceType = dType
                                    deviceTypeExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Device Brand Dropdown
                Text(
                    text = "Device Brand",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = brand,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "Expand Brand dropdown",
                                tint = TealPrimary,
                                modifier = Modifier.clickable { brandExpanded = !brandExpanded }
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = GrayBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { brandExpanded = !brandExpanded }
                            .testTag("repair_brand_input")
                    )
                    DropdownMenu(
                        expanded = brandExpanded,
                        onDismissRequest = { brandExpanded = false },
                        modifier = Modifier
                            .background(NavySurface)
                            .border(1.dp, GrayBorder)
                            .fillMaxWidth(0.85f)
                    ) {
                        brandsForType.forEach { bName ->
                            DropdownMenuItem(
                                text = { Text(text = bName, color = Color.White) },
                                onClick = {
                                    brand = bName
                                    brandExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Model Number (Text Input)
                Text(
                    text = "Model Number",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    placeholder = { Text("e.g. A2633, SM-S908B, Waaree-335", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealPrimary,
                        unfocusedBorderColor = GrayBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("repair_model_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Repair Issue Dropdown
                Text(
                    text = "Select Repairing Issue",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedIssue,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "Expand Issue dropdown",
                                tint = TealPrimary,
                                modifier = Modifier.clickable { issueExpanded = !issueExpanded }
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = GrayBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { issueExpanded = !issueExpanded }
                            .testTag("repair_issue_dropdown")
                    )
                    DropdownMenu(
                        expanded = issueExpanded,
                        onDismissRequest = { issueExpanded = false },
                        modifier = Modifier
                            .background(NavySurface)
                            .border(1.dp, GrayBorder)
                            .fillMaxWidth(0.85f)
                    ) {
                        issuesForType.forEach { iName ->
                            DropdownMenuItem(
                                text = { Text(text = iName, color = Color.White) },
                                onClick = {
                                    selectedIssue = iName
                                    issueExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5. Additional Description (Text Area)
                Text(
                    text = "Additional Description",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = additionalDesc,
                    onValueChange = { additionalDesc = it },
                    placeholder = { Text("Include additional context, patterns, or timing of the defect here...", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealPrimary,
                        unfocusedBorderColor = GrayBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .testTag("repair_issue_input"),
                    maxLines = 3
                )

                if (deviceType == "Solar panel") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AccentGreen.copy(alpha = 0.12f)),
                        border = BorderStroke(1.dp, AccentGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Info, contentDescription = "On-site notice", tint = AccentGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "☀️ On-Site Doorstep Service: Solar Panel repairs are executed entirely on-site. No pickup, delivery, or logistics are required.",
                                color = Color.White,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Service Region Selection",
                    color = TealPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Region selector
                Text(
                    text = "Region",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    regions.forEach { (regName, status) ->
                        val isSelected = selectedRegion == regName
                        val isActive = status == "Active"
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedRegion = regName },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) TealPrimary.copy(alpha = 0.12f) else NavyLightSurface
                            ),
                            border = BorderStroke(1.dp, if (isSelected) TealPrimary else Color.Transparent),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedRegion = regName },
                                        colors = RadioButtonDefaults.colors(selectedColor = TealPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = regName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                Surface(
                                    color = if (isActive) AccentGreen.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = status,
                                        color = if (isActive) AccentGreen else Color.Gray,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Nagpur Locality Dropdown
                if (selectedRegion == "Nagpur") {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your Locality in Nagpur Region",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedLocality,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = "Expand Locality dropdown",
                                    tint = TealPrimary,
                                    modifier = Modifier.clickable { localityExpanded = !localityExpanded }
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TealPrimary,
                                unfocusedBorderColor = GrayBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { localityExpanded = !localityExpanded }
                                .testTag("repair_locality_dropdown")
                        )
                        DropdownMenu(
                            expanded = localityExpanded,
                            onDismissRequest = { localityExpanded = false },
                            modifier = Modifier
                                .background(NavySurface)
                                .border(1.dp, GrayBorder)
                                .fillMaxWidth(0.85f)
                        ) {
                            nagpurLocalities.forEach { locName ->
                                DropdownMenuItem(
                                    text = { Text(text = locName, color = Color.White) },
                                    onClick = {
                                        selectedLocality = locName
                                        localityExpanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "⚠️ Service in $selectedRegion is currently unavailable. You cannot place orders outside active regions.",
                        color = AccentRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                val brands by viewModel.brandsList.collectAsState()
                val devices by viewModel.devicesList.collectAsState()
                val repairTypes by viewModel.repairTypesList.collectAsState()
                val parts by viewModel.partsList.collectAsState()

                val matchedBrand = brands.find { it.name.lowercase().contains(brand.lowercase()) }
                val matchedDevice = if (matchedBrand != null) {
                    devices.find { it.brand_id == matchedBrand.id && (it.name.lowercase().contains(model.lowercase()) || model.lowercase().contains(it.name.lowercase())) }
                } else null

                val matchedRepairType = when {
                    selectedIssue.lowercase().contains("screen") -> repairTypes.find { it.name == "screen" }
                    selectedIssue.lowercase().contains("battery") -> repairTypes.find { it.name == "battery" }
                    selectedIssue.lowercase().contains("charging") -> repairTypes.find { it.name == "chargingport" }
                    selectedIssue.lowercase().contains("camera") || selectedIssue.lowercase().contains("speaker") -> repairTypes.find { it.name == "camera" || it.name == "speaker" }
                    selectedIssue.lowercase().contains("motherboard") || selectedIssue.lowercase().contains("power") -> repairTypes.find { it.name == "motherboard" }
                    selectedIssue.lowercase().contains("water") -> repairTypes.find { it.name == "waterdamage" }
                    else -> null
                }

                val partsTotal = if (matchedDevice != null && matchedRepairType != null) {
                    val matchedParts = parts.filter { it.device_id == matchedDevice.id && it.repair_type_id == matchedRepairType.id }
                    matchedParts.sumOf { it.price * 0.7 }
                } else {
                    0.0
                }

                val discountedParts = partsTotal * 0.9
                val serviceFee = discountedParts * 0.15
                val diagnosisCharge = 250.0
                val totalEstimate = discountedParts + serviceFee + diagnosisCharge

                if (partsTotal > 0.0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NavyDark),
                        border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📊 Live Repair Estimate (Synced with Calculator)",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Spares Cost (Discounted 10%):", color = GrayText, fontSize = 11.sp)
                                Text("₹${String.format("%.2f", discountedParts)}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Service Fee (15%):", color = GrayText, fontSize = 11.sp)
                                Text("₹${String.format("%.2f", serviceFee)}", color = AmberAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Diagnosis & Doorstep Pickup:", color = GrayText, fontSize = 11.sp)
                                Text("₹250.00", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Divider(color = GrayBorder, modifier = Modifier.padding(vertical = 6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total Estimated Cost:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("₹${String.format("%.2f", totalEstimate)}", color = TealPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = errorMessage!!, color = AccentRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                val isFormValid = selectedRegion == "Nagpur" && model.isNotBlank()

                Button(
                    onClick = {
                        if (model.isBlank()) {
                            errorMessage = "Model number is required!"
                        } else if (selectedRegion != "Nagpur") {
                            errorMessage = "Booking is only allowed for the active Nagpur region!"
                        } else {
                            val combinedIssue = "Type: $deviceType | Issue: $selectedIssue" +
                                if (additionalDesc.isNotBlank()) " | Details: $additionalDesc" else ""
                            viewModel.createRepairRequest(
                                brand = brand,
                                model = model,
                                issue = combinedIssue,
                                city = "$selectedLocality, Nagpur",
                                partsTotal = discountedParts,
                                serviceCharge = serviceFee,
                                total = totalEstimate
                            )
                        }
                    },
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_repair_request_button")
                ) {
                    Text(
                        text = if (selectedRegion == "Nagpur") "Submit Doorstep Repair Request" else "Selected Region Unavailable",
                        color = if (isFormValid) Color.Black else Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RepairRequestDetailView(
    viewModel: RepairViewModel,
    requestId: Long,
    modifier: Modifier = Modifier
) {
    val requests by viewModel.allRepairRequests.collectAsState()
    val ticket = requests.find { it.id == requestId }

    if (ticket == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(NavyBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("Request Not Found", color = Color.White)
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Back Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.CustomerDashboard) },
                    modifier = Modifier.background(NavySurface, CircleShape)
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = ticket.code,
                        color = TealPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Repair Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Cancel button
            if (ticket.statusStep == 1) {
                IconButton(
                    onClick = { viewModel.deleteRequest(ticket.id) },
                    modifier = Modifier.background(AccentRed.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Cancel", tint = AccentRed)
                }
            }
        }

        // Summary Card
        Card(
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, GrayBorder),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${ticket.deviceBrand} ${ticket.deviceModel}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    RepairStatusBadge(step = ticket.statusStep)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Reported Issue: ${ticket.issueDescription}",
                    color = GrayText,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Location: ${ticket.city}",
                        color = GrayText,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Technician: ${ticket.technicianName ?: "Awaiting Assignment"}",
                        color = if (ticket.technicianName != null) TealPrimary else AmberAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // 1. Pickup / Delivery OTP Panel (CRITICAL REQUIREMENT)
        if (ticket.statusStep == 3 && !ticket.isPickupOtpVerified) {
            LiveMapTrackingView(
                technicianName = ticket.technicianName ?: "Rahul Kumar",
                etaMins = 12,
                statusText = "Technician is approaching your doorstep for collection.",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, if (ticket.isMaintenanceModeEnabled) AccentGreen else AmberAccent),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (ticket.isMaintenanceModeEnabled) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                                contentDescription = null,
                                tint = if (ticket.isMaintenanceModeEnabled) AccentGreen else AmberAccent
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SECURE DOORSTEP PICKUP",
                                color = if (ticket.isMaintenanceModeEnabled) AccentGreen else AmberAccent,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        // Small tag for status
                        Surface(
                            color = (if (ticket.isMaintenanceModeEnabled) AccentGreen else AccentRed).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, if (ticket.isMaintenanceModeEnabled) AccentGreen else AccentRed)
                        ) {
                            Text(
                                text = if (ticket.isMaintenanceModeEnabled) "SECURED" else "ACTION REQUIRED",
                                color = if (ticket.isMaintenanceModeEnabled) AccentGreen else AccentRed,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (ticket.isDeviceDeadOrBlank) {
                        Surface(
                            color = AmberAccent.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, AmberAccent.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "⚠️ MAINTENANCE MODE BYPASSED (DEVICE DEAD/BLANK)",
                                    color = AmberAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "You have declared that the device is completely dead or screen is blank. Under this exception, the Nagpur partner is authorized to collect the device without visual verification of Maintenance Mode. Please hand over the physical device safely.",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Provide this 4-digit security code to the technician to confirm secure handover.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .background(NavyDark, RoundedCornerShape(8.dp))
                                .padding(horizontal = 24.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = ticket.pickupOtpCode,
                                color = AmberAccent,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 4.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        TextButton(
                            onClick = { viewModel.setDeviceDeadOrBlank(ticket.id, false) },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Cancel Dead/Blank Bypass", color = GrayText, fontSize = 11.sp)
                        }
                    } else if (!ticket.isMaintenanceModeEnabled) {
                        // Data Privacy Alert Box
                        Surface(
                            color = AccentRed.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, AccentRed.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "🔒 MANDATORY PRIVACY LOCK REQUIRED",
                                    color = AccentRed,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "To guarantee the absolute confidentiality of your private photos, passwords, chats, and UPI apps, you MUST enable Maintenance Mode on your device before handback.",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // How-To Details
                        Text(
                            text = "💡 How to activate Maintenance Mode:",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Android: Settings -> Security and Privacy -> Maintenance Mode -> Turn On\n• iOS: Use Assistive/Guided Access or our simulated device sandbox below.",
                            color = GrayText,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.setMaintenanceMode(ticket.id, true) },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("activate_maintenance_mode_btn")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("🛡️ Activate Maintenance Mode & Reveal OTP", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setDeviceDeadOrBlank(ticket.id, true) }
                                .background(NavyDark, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Icon(Icons.Filled.Info, contentDescription = null, tint = AmberAccent, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Is the device dead or screen blank?", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Bypass maintenance mode if the device cannot power on.", color = GrayText, fontSize = 10.sp)
                            }
                            Text("Bypass →", color = AmberAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(NavyDark, RoundedCornerShape(8.dp))
                                .border(1.dp, GrayBorder, RoundedCornerShape(8.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🔑 [OTP LOCKED - ENABLE PRIVACY LOCK]",
                                color = GrayText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    } else {
                        // Maintenance mode is ACTIVE
                        Surface(
                            color = AccentGreen.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, AccentGreen.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "🛡️ MAINTENANCE MODE ACTIVE & VERIFIED",
                                    color = AccentGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Your sensitive data is safely isolated in an encrypted sandbox. It is now completely safe to hand over the physical device to the Nagpur dispatch partner.",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Provide this 4-digit security code to the technician arriving at your door to confirm secure handover.",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .background(NavyDark, RoundedCornerShape(8.dp))
                                .padding(horizontal = 24.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = ticket.pickupOtpCode,
                                color = AccentGreen,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 4.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        TextButton(
                            onClick = { viewModel.setMaintenanceMode(ticket.id, false) },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Change / Deactivate Maintenance Mode", color = GrayText, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        if (ticket.statusStep == 10 && !ticket.isDeliveryOtpVerified) {
            LiveMapTrackingView(
                technicianName = ticket.technicianName ?: "Rahul Kumar",
                etaMins = 8,
                statusText = "Technician is carrying your repaired device to your doorstep.",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, TealPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Lock, contentDescription = null, tint = TealPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SECURE DELIVERY HANDOVER",
                            color = TealPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your device has been repaired and checked. Share this delivery code with the dispatcher once you receive your working phone.",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .background(NavyDark, RoundedCornerShape(8.dp))
                            .padding(horizontal = 24.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = ticket.deliveryOtpCode,
                            color = TealPrimary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 4.sp
                        )
                    }
                }
            }
        }

        // 2. Quotation Invoice Review
        if (ticket.statusStep == 6) {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, TealPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "REPAIR QUOTATION PROPOSAL",
                        color = TealPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Parse parts "Name,Price,Qty;Name,Price,Qty"
                    val partsList = ticket.quotePartsString.split(";").filter { it.isNotBlank() }.map {
                        val tokens = it.split(",")
                        Triple(tokens.getOrNull(0) ?: "Part", tokens.getOrNull(1)?.toDoubleOrNull() ?: 0.0, tokens.getOrNull(2)?.toIntOrNull() ?: 1)
                    }

                    partsList.forEach { (name, price, qty) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "$name (x$qty)", color = Color.White, fontSize = 13.sp)
                            Text(text = "₹${String.format("%.2f", price * qty)}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Service Diagnosis Charge", color = Color.White, fontSize = 13.sp)
                        Text(text = "₹${String.format("%.2f", ticket.quoteServiceCharge)}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Platform Admin Fee (${ticket.serviceChargePercent}%)", color = GrayText, fontSize = 12.sp)
                        val subtotal = partsList.sumOf { it.second * it.third } + ticket.quoteServiceCharge
                        val fee = subtotal * (ticket.serviceChargePercent / 100.0)
                        Text(text = "₹${String.format("%.2f", fee)}", color = GrayText, fontSize = 12.sp)
                    }

                    Divider(color = GrayBorder, modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Total Guaranteed Quote", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = "₹${String.format("%.2f", ticket.totalAmount)}", color = TealPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.approveQuotation(ticket.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("approve_quote_button")
                        ) {
                            Text("Approve & Repair", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.rejectQuotation(ticket.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("reject_quote_button")
                        ) {
                            Text("Reject & Cancel", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 2.5 Review & Rating System for Completed Jobs
        if (ticket.statusStep == 11) {
            val reviews by viewModel.allReviews.collectAsState()
            val existingReview = reviews.find { it.repairRequestId == ticket.id }
            val userProfile by viewModel.userProfile.collectAsState()

            if (existingReview != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, AccentGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = AccentGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "THANK YOU FOR YOUR FEEDBACK",
                                color = AccentGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "Doorstep Specialist: ${ticket.technicianName ?: "Technician"}",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = if (index < existingReview.techRating) AmberAccent else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "(${existingReview.techRating}/5 Stars)", color = GrayText, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "RepairMaster Shop Partner",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = if (index < existingReview.masterRating) AmberAccent else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "(${existingReview.masterRating}/5 Stars)", color = GrayText, fontSize = 12.sp)
                        }

                        if (existingReview.comment.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Written Feedback:", color = GrayText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                                    .background(NavyDark, RoundedCornerShape(6.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = existingReview.comment,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            } else {
                // Interactive Review Form
                var techRatingInput by remember { mutableStateOf(5) }
                var masterRatingInput by remember { mutableStateOf(5) }
                var writtenCommentInput by remember { mutableStateOf("") }

                Card(
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, TealPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(bottom = 16.dp).fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = TealPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "RATE YOUR SERVICE EXPERIENCE",
                                color = TealPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "1. Rate Doorstep Technician (${ticket.technicianName ?: "Devendra Chaudhari"})",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            repeat(5) { index ->
                                val starNum = index + 1
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Rate ${starNum} Stars",
                                    tint = if (starNum <= techRatingInput) AmberAccent else Color.Gray,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable { techRatingInput = starNum }
                                        .padding(horizontal = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "2. Rate Local RepairMaster Workshop Partner",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            repeat(5) { index ->
                                val starNum = index + 1
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Rate ${starNum} Stars",
                                    tint = if (starNum <= masterRatingInput) AmberAccent else Color.Gray,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clickable { masterRatingInput = starNum }
                                        .padding(horizontal = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "3. Written Review & Experience Details",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        OutlinedTextField(
                            value = writtenCommentInput,
                            onValueChange = { writtenCommentInput = it },
                            placeholder = { Text("What did you like or think we can improve about our doorstep collection and workshop repair service?", color = Color.Gray, fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = TealPrimary,
                                unfocusedBorderColor = GrayBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .testTag("review_written_comment_input")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.submitReview(
                                    repairRequestId = ticket.id,
                                    customerName = userProfile.name,
                                    technicianId = ticket.technicianId ?: 101L,
                                    technicianName = ticket.technicianName ?: "Devendra Chaudhari",
                                    techRating = techRatingInput,
                                    masterRating = masterRatingInput,
                                    comment = writtenCommentInput
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("submit_review_button")
                        ) {
                            Text("Submit Secure Feedback", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3. 11-Step Timeline
        Card(
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, GrayBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "REPAIR PROGRESS TIMELINE",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val steps = listOf(
                    Pair(1, "New Request Received"),
                    Pair(2, "Certified Technician Assigned"),
                    Pair(3, "Secure Doorstep Pickup Dispatched"),
                    Pair(4, "Device Collected & Handed Over"),
                    Pair(5, "Advanced Device Diagnosis Underway"),
                    Pair(6, "Quotation Prepared & Awaiting Approval"),
                    Pair(7, "Quotation Approved & Repairing"),
                    Pair(8, "Hardware Repair Finished & Quality Checked"),
                    Pair(9, "Secure Out-for-Delivery Handback"),
                    Pair(10, "Secure Code Verification On-Site"),
                    Pair(11, "Repair Completed Successfully")
                )

                steps.forEach { (stepNum, stepDesc) ->
                    val isCompleted = ticket.statusStep > stepNum
                    val isActive = ticket.statusStep == stepNum
                    val color = if (isCompleted) TealPrimary else if (isActive) AmberAccent else Color.Gray

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Vertical line visual
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(24.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(color),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCompleted) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                            if (stepNum < 11) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(28.dp)
                                        .background(if (isCompleted) TealPrimary else GrayBorder)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Step $stepNum: $stepDesc",
                                color = if (isActive) Color.White else if (isCompleted) TealPrimary else GrayText,
                                fontSize = 13.sp,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
                            )
                            if (isActive) {
                                val helpMsg = when (stepNum) {
                                    1 -> "We have received your booking and are validating details for coordinator delegation."
                                    2 -> "Technician has accepted your booking and is preparing equipment."
                                    3 -> "Technician is on the way to your doorstep to pick up the device. Get your 4-digit code ready!"
                                    4 -> "Device received. Safely shipping under lockbox directly to our repair laboratory."
                                    5 -> "Device under ultrasonic cleaners and multimeter probes. Identifying malfunctioning microchips."
                                    6 -> "A precise quotation is ready. Scroll up to review the breakdown and accept to initiate assembly."
                                    7 -> "Diagnostics passed. Technicians are soldering replacement parts on the logic board."
                                    8 -> "Micro-soldering complete. Device undergoing multi-point camera, display, and charging test suites."
                                    9 -> "Technician is driving repaired device back to your house. Get your delivery code ready!"
                                    10 -> "Deliverer is at your door. Verify the device is fully working, then share your delivery code."
                                    11 -> "Device handed back. Digital warranty issued and active!"
                                    else -> ""
                                }
                                Text(
                                    text = helpMsg,
                                    color = AmberAccent,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
