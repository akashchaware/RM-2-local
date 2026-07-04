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
import com.example.data.Listing
import com.example.data.Order
import com.example.data.RepairRequest
import com.example.data.Review
import com.example.ui.RepairViewModel
import com.example.ui.theme.*

@Composable
fun RepairMasterDashboardView(
    viewModel: RepairViewModel,
    modifier: Modifier = Modifier
) {
    val listings by viewModel.allListings.collectAsState()
    val orders by viewModel.allOrders.collectAsState()
    val requests by viewModel.allRepairRequests.collectAsState()
    val reviews by viewModel.allReviews.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var activeTab by viewModel.repairMasterSelectedTab
    var showNewListingDialog by remember { mutableStateOf(false) }

    // Derived states
    val activeListings = listings.filter { !it.isSold }
    val soldListings = listings.filter { it.isSold }
    val totalRevenue = orders.sumOf { it.amountPaid }
    val myRequests = remember(requests, userProfile) {
        requests.filter { it.repairMasterId != null }
    }
    val activeJobs = myRequests.filter { it.statusStep < 11 }
    val completedJobs = myRequests.filter { it.statusStep == 11 }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {
        // Welcome and Role header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "PARTNER SERVICE NETWORK", color = TealPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "RepairMaster Shop", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            RoleBadge(role = "REPAIRMASTER")
        }

        // Custom Tabs Row
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = NavySurface,
            contentColor = TealPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                    color = TealPrimary
                )
            }
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("Overview", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (activeTab == 0) TealPrimary else GrayText) }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("Listings", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (activeTab == 1) TealPrimary else GrayText) }
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = { Text("Orders", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (activeTab == 2) TealPrimary else GrayText) }
            )
            Tab(
                selected = activeTab == 3,
                onClick = { activeTab = 3 },
                text = { Text("Repairs", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (activeTab == 3) TealPrimary else GrayText) }
            )
        }

        // Body Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeTab) {
                0 -> OverviewTab(
                    activeCount = activeListings.size,
                    soldCount = soldListings.size,
                    revenue = totalRevenue,
                    activeRepairsCount = activeJobs.size,
                    completedCount = completedJobs.size,
                    reviews = reviews,
                    onNavigateToListings = { activeTab = 1 },
                    onNavigateToOrders = { activeTab = 2 }
                )
                1 -> ListingsTab(
                    listings = listings,
                    onAddNewListing = { showNewListingDialog = true },
                    onDeleteListing = { viewModel.deleteListing(it) }
                )
                2 -> OrdersTab(orders = orders)
                3 -> RepairsTab(requests = myRequests, onSelect = { viewModel.selectRequest(it) })
            }
        }
    }

    // New Listing Dialog with live preview
    if (showNewListingDialog) {
        var title by remember { mutableStateOf("") }
        var grade by remember { mutableStateOf("Excellent") }
        var storage by remember { mutableStateOf("128GB") }
        var price by remember { mutableStateOf("") }
        var city by remember { mutableStateOf("Nagpur") }
        var description by remember { mutableStateOf("") }
        var imageUrl by remember { mutableStateOf("") }
        var errorMsg by remember { mutableStateOf<String?>(null) }

        val grades = listOf("Mint", "Excellent", "Good")
        val storages = listOf("64GB", "128GB", "256GB", "512GB")

        AlertDialog(
            onDismissRequest = { showNewListingDialog = false },
            title = { Text("Sell Refurbished Smartphone", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "List checked, fully certified refurbished phones in the marketplace.",
                        color = GrayText,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Title
                    Text("Listing Title", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("e.g. iPhone 13 Pro Max", color = Color.Gray) },
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
                            .testTag("listing_title_input")
                    )

                    // Grade & Storage Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Grade", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row {
                                grades.forEach { g ->
                                    val isSelected = grade == g
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isSelected) TealPrimary else NavyLightSurface)
                                            .clickable { grade = g }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = g, color = if (isSelected) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(2.dp))
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Storage", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row {
                                storages.take(3).forEach { s ->
                                    val isSelected = storage == s
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isSelected) TealPrimary else NavyLightSurface)
                                            .clickable { storage = s }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = s, color = if (isSelected) Color.Black else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(2.dp))
                                }
                            }
                        }
                    }

                    // Price & City Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Price ($)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = price,
                                onValueChange = { price = it },
                                placeholder = { Text("499.00", color = Color.Gray) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = TealPrimary,
                                    unfocusedBorderColor = GrayBorder
                                ),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("listing_price_input")
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("City", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                placeholder = { Text("Nagpur", color = Color.Gray) },
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
                    }

                    // Image URL
                    Text("Image URL (Preview Available)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        placeholder = { Text("https://image.url...", color = Color.Gray) },
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
                            .testTag("listing_image_input")
                    )

                    // Live Image Preview Placeholder
                    Text(text = "LIVE IMAGE PREVIEW", color = TealPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(NavyDark),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUrl.isNotBlank()) {
                            // Display URL preview
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = AccentGreen)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("URL Registered", color = Color.White, fontSize = 12.sp)
                                Text(imageUrl, color = GrayText, fontSize = 9.sp, maxLines = 1)
                            }
                        } else {
                            Text("No image URL provided. Will use standard default.", color = GrayText, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Description
                    Text("Description", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Write about cosmetic flaws, battery health, accessories...", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = GrayBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .testTag("listing_desc_input")
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
                        val numPrice = price.toDoubleOrNull()
                        if (title.isBlank() || numPrice == null || numPrice <= 0 || description.isBlank()) {
                            errorMsg = "Please check input. Title, valid Price, and Description are required!"
                        } else {
                            viewModel.createListing(title, grade, storage, numPrice, city, description, imageUrl)
                            showNewListingDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    modifier = Modifier.testTag("confirm_create_listing")
                ) {
                    Text("Publish Listing", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewListingDialog = false }) { Text("Cancel", color = GrayText) }
            },
            containerColor = NavySurface
        )
    }
}

@Composable
fun OverviewTab(
    activeCount: Int,
    soldCount: Int,
    revenue: Double,
    activeRepairsCount: Int,
    completedCount: Int,
    reviews: List<Review>,
    onNavigateToListings: () -> Unit,
    onNavigateToOrders: () -> Unit
) {
    val avgMasterRating = if (reviews.isEmpty()) 5.0 else reviews.map { it.masterRating }.average()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Shop Overview Analytics", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(title = "Listed Units", value = "$activeCount", icon = Icons.Filled.ShoppingCart, iconColor = TealPrimary, modifier = Modifier.weight(1f))
            StatCard(title = "Sold Devices", value = "$soldCount", icon = Icons.Filled.CheckCircle, iconColor = AccentGreen, modifier = Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatCard(title = "Total Earnings", value = "₹${String.format("%.2f", revenue)}", icon = Icons.Filled.Star, iconColor = AmberAccent, modifier = Modifier.weight(1.5f))
            StatCard(title = "Repair Volume", value = "${activeRepairsCount + completedCount}", icon = Icons.Filled.Build, iconColor = Color.White, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(4.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = BorderStroke(1.dp, GrayBorder),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Quick Management Actions", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onNavigateToListings,
                        colors = ButtonDefaults.buttonColors(containerColor = NavyLightSurface),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.List, contentDescription = null, tint = TealPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Listings Screen", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onNavigateToOrders,
                        colors = ButtonDefaults.buttonColors(containerColor = NavyLightSurface),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = null, tint = AmberAccent)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Buyer Orders Feed", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Shop Reputation & Customer Feedback Feed
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
                            text = "SHOP REPUTATION INDEX",
                            color = AmberAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "⭐ ${String.format("%.1f", avgMasterRating)} / 5.0",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Consolidated rating score from Nagpur repair workshop handbacks.",
                    color = GrayText,
                    fontSize = 12.sp
                )

                if (reviews.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Latest Customer Reviews:",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        reviews.take(3).forEach { rev ->
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
                                    Column {
                                        Text(text = rev.customerName, color = TealPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Verified Repair Handover", color = GrayText, fontSize = 9.sp)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        repeat(rev.masterRating) {
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
}

@Composable
fun ListingsTab(
    listings: List<Listing>,
    onAddNewListing: () -> Unit,
    onDeleteListing: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Listed Smartphone Inventory", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = onAddNewListing,
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("add_listing_entry_button")
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Create Listing", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        if (listings.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Inventory is currently empty. Use the button to publish refurbished devices.", color = GrayText, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(listings) { listing ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                        border = BorderStroke(1.dp, if (listing.isSold) AccentGreen.copy(alpha = 0.5f) else GrayBorder),
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
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = listing.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = if (listing.isSold) AccentGreen.copy(alpha = 0.15f) else TealPrimary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = if (listing.isSold) "SOLD" else listing.grade,
                                            color = if (listing.isSold) AccentGreen else TealPrimary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = "${listing.storage} • Listed in ${listing.city}", color = GrayText, fontSize = 12.sp)
                                Text(text = "₹${String.format("%.2f", listing.price)}", color = TealPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }

                            IconButton(
                                onClick = { onDeleteListing(listing.id) },
                                modifier = Modifier
                                    .background(AccentRed.copy(alpha = 0.12f), CircleShape)
                                    .testTag("delete_listing_${listing.id}")
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = AccentRed)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrdersTab(orders: List<Order>) {
    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No sales orders received yet in the marketplace.", color = GrayText, fontSize = 13.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text("Refurbished Purchase Feed", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
            }
            items(orders) { order ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, GrayBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = order.listingTitle, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Surface(color = AccentGreen.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                Text("PAID", color = AccentGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Buyer: ${order.buyerName} (${order.buyerPhone})", color = Color.White, fontSize = 12.sp)
                        Text(text = "Ship Address: ${order.buyerAddress}", color = GrayText, fontSize = 11.sp)

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = GrayBorder.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(4.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Checkout Amount", color = GrayText, fontSize = 11.sp)
                            Text("₹${String.format("%.2f", order.amountPaid)}", color = AccentGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RepairsTab(
    requests: List<RepairRequest>,
    onSelect: (Long) -> Unit
) {
    if (requests.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No repair orders currently assigned to this shop node.", color = GrayText, fontSize = 13.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text("Assigned Lab Tickets", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
            }
            items(requests) { req ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.dp, GrayBorder),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(req.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = req.code, color = TealPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${req.deviceBrand} ${req.deviceModel}", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Client: [Masked for Privacy]", color = GrayText, fontSize = 12.sp)
                        }
                        RepairStatusBadge(step = req.statusStep)
                    }
                }
            }
        }
    }
}
