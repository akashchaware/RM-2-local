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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.RepairViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

data class SlideData(
    val imageRes: Int,
    val badge: String,
    val badgeColor: Color,
    val title: String,
    val description: String
)

@Composable
fun LandingView(
    viewModel: RepairViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val listings by viewModel.allListings.collectAsState()
    var isWorkflowExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Branded Navigation Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(NavySurface)
                        .border(1.dp, TealPrimary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_brand_logo_circular_1782765220741),
                        contentDescription = "RepairMaster Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Repair",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            lineHeight = 17.sp
                        )
                        Text(
                            text = "Master",
                            color = TealPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            lineHeight = 17.sp
                        )
                    }
                    Text(
                        text = "Direct To Consumer",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 11.sp
                    )
                    Text(
                        text = "powered by cm2us.com",
                        color = GrayText,
                        fontSize = 7.5.sp,
                        lineHeight = 9.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (viewModel.isLoggedIn.value) {
                    Surface(
                        color = NavySurface,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable { viewModel.navigateTo(Screen.UserProfile) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = userProfile.name.split(" ").firstOrNull() ?: "Profile", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            viewModel.isLoggedIn.value = false
                            viewModel.navigateTo(Screen.Landing)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(NavySurface, CircleShape)
                            .testTag("home_logout_button")
                    ) {
                        Icon(
                            Icons.Filled.ExitToApp,
                            contentDescription = "Log Out",
                            tint = AccentRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = { viewModel.navigateTo(Screen.Login) },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("home_signin_button")
                    ) {
                        Text("Sign In", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Hero Banner Graphic & Taglines
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(NavyDark, NavyBackground)
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    color = TealPrimary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = "⚡ Certified Doorstep Repairs & Refurbished Devices",
                        color = TealPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Professional Phone Repair,\nDelivered To Your Door",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 30.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Book certified repairs online, approve granular spare-parts quotes, and buy/sell verified refurbished hardware inside a secure escrow environment.",
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Premium Dynamic Service Slideshow (Indian Context)
                var currentSlide by remember { mutableStateOf(0) }
                val slides = remember {
                    listOf(
                        SlideData(
                            imageRes = R.drawable.img_indian_service_scooter_1782739563064,
                            badge = "REPAIR DISPATCH",
                            badgeColor = Color(0xFF818CF8),
                            title = "Swift Doorstep Return & 6-Month Warranty",
                            description = "Repaired smartphones are dispatched back to your doorstep. Test camera, sound, and screen before sharing."
                        ),
                        SlideData(
                            imageRes = R.drawable.img_maintenance_mode_1782766826537,
                            badge = "SECURE PRIVACY",
                            badgeColor = Color(0xFFFF2E93),
                            title = "Secure Maintenance Mode & Privacy",
                            description = "Enable Maintenance Mode with 1-tap before handover. Lock all your photos, chats, messages, and banking data in an impenetrable vault during repair."
                        ),
                        SlideData(
                            imageRes = R.drawable.img_indian_doorstep_service_1782738015281,
                            badge = "SECURE COLLECTION",
                            badgeColor = TealPrimary,
                            title = "Mandatory Maintenance Mode & OTP Verification",
                            description = "Enable Maintenance Mode to encrypt & isolate private data. Handover is authorized only after verifying Maintenance Mode is ON and scanning your secure 4-digit OTP."
                        ),
                        SlideData(
                            imageRes = R.drawable.img_indian_service_lab_1782739540576,
                            badge = "LAB DIAGNOSIS",
                            badgeColor = AccentGreen,
                            title = "Precision Laboratory Diagnostics",
                            description = "Devices are inspected in high-tech Nagpur laboratories with precise replacement parts breakdown & live digital approvals."
                        ),
                        SlideData(
                            imageRes = R.drawable.img_indian_service_lab_1782739540576,
                            badge = "LIVE TRACKING",
                            badgeColor = AmberAccent,
                            title = "Real-Time Tracking & Live Map Status",
                            description = "Monitor your collection, laboratory diagnosis, and handback stage with Nagpur's dynamic dispatch radar."
                        )
                    )
                }

                // Autoplay effect
                LaunchedEffect(currentSlide) {
                    kotlinx.coroutines.delay(5000)
                    currentSlide = (currentSlide + 1) % slides.size
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(185.dp)
                        .padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, GrayBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Display current slide image with smooth Crossfade
                        Crossfade(targetState = currentSlide, label = "slide_transition") { slideIdx ->
                            val slide = slides[slideIdx]
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(
                                    painter = painterResource(id = slide.imageRes),
                                    contentDescription = slide.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                // Gradient Overlay for text readability
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.95f)
                                                )
                                            )
                                        )
                                )
                                // Content on Top of Banner
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(16.dp)
                                        .padding(end = if (slide.badge == "SECURE COLLECTION") 185.dp else 40.dp) // Leave extra space on right for checklist card
                                ) {
                                    Surface(
                                        color = slide.badgeColor,
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    ) {
                                        Text(
                                            text = slide.badge,
                                            color = Color.Black,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = slide.title,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = slide.description,
                                        color = GrayText,
                                        fontSize = 10.5.sp,
                                        lineHeight = 14.sp,
                                        maxLines = 2
                                    )
                                }

                                if (slide.badge == "SECURE COLLECTION") {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = NavyDark.copy(alpha = 0.88f)),
                                        border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.8f)),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .padding(end = 12.dp, top = 8.dp, bottom = 8.dp)
                                            .width(165.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            verticalArrangement = Arrangement.spacedBy(5.dp)
                                        ) {
                                            Text(
                                                text = "⚡ HANDOVER CHECKLIST",
                                                color = TealPrimary,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 0.5.sp
                                            )
                                            HorizontalDivider(color = GrayBorder, thickness = 0.5.dp)
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Check,
                                                    contentDescription = null,
                                                    tint = AccentGreen,
                                                    modifier = Modifier.size(11.dp)
                                                )
                                                Text(
                                                    text = "Maintenance Mode ON",
                                                    color = Color.White,
                                                    fontSize = 8.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Check,
                                                    contentDescription = null,
                                                    tint = AccentGreen,
                                                    modifier = Modifier.size(11.dp)
                                                )
                                                Text(
                                                    text = "Secure 4-Digit OTP",
                                                    color = Color.White,
                                                    fontSize = 8.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Check,
                                                    contentDescription = null,
                                                    tint = AccentGreen,
                                                    modifier = Modifier.size(11.dp)
                                                )
                                                Text(
                                                    text = "Photos & Chats Locked",
                                                    color = Color.White,
                                                    fontSize = 8.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Left Arrow Overlay
                        IconButton(
                            onClick = {
                                currentSlide = if (currentSlide == 0) slides.size - 1 else currentSlide - 1
                            },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 8.dp)
                                .size(32.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowLeft,
                                contentDescription = "Previous Slide",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Right Arrow Overlay
                        IconButton(
                            onClick = {
                                currentSlide = (currentSlide + 1) % slides.size
                            },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 8.dp)
                                .size(32.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowRight,
                                contentDescription = "Next Slide",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Dot Indicators at top-right
                        Row(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            slides.forEachIndexed { idx, _ ->
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (currentSlide == idx) TealPrimary else Color.White.copy(alpha = 0.4f)
                                        )
                                        .clickable { currentSlide = idx }
                                )
                            }
                        }
                    }
                }

                // Active Promotions & Offers
                val promoList by viewModel.promotions.collectAsState()
                if (promoList.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔥 Special Promotional Offers",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                color = AmberAccent.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "LIVE OFFERS",
                                    color = AmberAccent,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        promoList.forEach { promo ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = NavySurface),
                                border = BorderStroke(1.2.dp, TealPrimary.copy(alpha = 0.45f))
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    val painter = if (promo.drawableResName == "img_dead_phone_offer_1782763644093") {
                                        coil.compose.rememberAsyncImagePainter(
                                            model = "https://mpcnfrshpgcpmrgledwy.supabase.co/storage/v1/object/public/RequestBucket/Gemini_Generated_Image_bgj7ssbgj7ssbgj7.png",
                                            placeholder = painterResource(id = R.drawable.img_dead_phone_offer_1782763644093),
                                            error = painterResource(id = R.drawable.img_dead_phone_offer_1782763644093)
                                        )
                                    } else {
                                        val imgId = if (promo.drawableResName == "img_indian_service_lab_1782739540576") {
                                            R.drawable.img_indian_service_lab_1782739540576
                                        } else {
                                            R.drawable.img_indian_service_scooter_1782739563064
                                        }
                                        painterResource(id = imgId)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp)
                                    ) {
                                        Image(
                                            painter = painter,
                                            contentDescription = promo.title,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Brush.verticalGradient(
                                                        colors = listOf(
                                                            Color.Transparent,
                                                            Color.Black.copy(alpha = 0.7f)
                                                        )
                                                    )
                                                )
                                        )
                                        Surface(
                                            color = AmberAccent,
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(10.dp)
                                        ) {
                                            Text(
                                                text = "🔥 HOT DEAL",
                                                color = Color.Black,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = promo.title,
                                                color = Color.White,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = TealPrimary,
                                                shape = RoundedCornerShape(5.dp)
                                            ) {
                                                Text(
                                                    text = "₹${promo.offerPrice.toInt()}",
                                                    color = Color.Black,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = promo.description,
                                            color = Color.White.copy(alpha = 0.7f),
                                            fontSize = 11.5.sp,
                                            lineHeight = 15.sp
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Button(
                                            onClick = {
                                                if (viewModel.isLoggedIn.value) {
                                                    viewModel.navigateTo(Screen.NewRepairRequest)
                                                } else {
                                                    viewModel.navigateTo(Screen.Login)
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Build,
                                                    contentDescription = null,
                                                    tint = Color.Black,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Claim Offer & Book Diagnosis",
                                                    color = Color.Black,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.5.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Estimator Price Calculator Widget (Synced with Web logic & Supabase)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurface),
                    border = BorderStroke(1.5.dp, TealPrimary.copy(alpha = 0.6f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(TealPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = "Calculator",
                                    tint = TealPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Pricing Estimate Tool",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Calculate dynamic service rates instantly",
                                    color = GrayText,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Collect State from ViewModel
                        val brands by viewModel.brandsList.collectAsState()
                        val devices by viewModel.devicesList.collectAsState()
                        val repairTypes by viewModel.repairTypesList.collectAsState()
                        val parts by viewModel.partsList.collectAsState()
                        val isCatalogLoading by viewModel.isCatalogLoading.collectAsState()

                        // Selection States
                        var selectedBrand by remember { mutableStateOf<com.example.data.SupabaseBrand?>(null) }
                        var selectedDevice by remember { mutableStateOf<com.example.data.SupabaseDevice?>(null) }
                        var selectedRepairType by remember { mutableStateOf<com.example.data.SupabaseRepairType?>(null) }
                        var selectedQualityGrade by remember { mutableStateOf("Premium Original Grade") }
                        var isPromoApplied by remember { mutableStateOf(false) }

                        // Dropdowns Expanded States
                        var brandExpanded by remember { mutableStateOf(false) }
                        var deviceExpanded by remember { mutableStateOf(false) }
                        var repairExpanded by remember { mutableStateOf(false) }
                        var qualityExpanded by remember { mutableStateOf(false) }

                        // LaunchedEffects to trigger default selection on loaded
                        LaunchedEffect(brands) {
                            if (selectedBrand == null && brands.isNotEmpty()) {
                                selectedBrand = brands.find { it.name == "Vivo" } ?: brands.first()
                            }
                        }

                        LaunchedEffect(selectedBrand, devices) {
                            if (selectedBrand != null) {
                                val filtered = devices.filter { it.brand_id == selectedBrand?.id }
                                if (filtered.isNotEmpty()) {
                                    selectedDevice = filtered.find { it.name == "Vivo V30 Pro" } ?: filtered.first()
                                } else {
                                    selectedDevice = null
                                }
                            }
                        }

                        LaunchedEffect(selectedDevice, repairTypes) {
                            if (selectedDevice != null) {
                                val availableRTs = repairTypes
                                if (availableRTs.isNotEmpty()) {
                                    selectedRepairType = availableRTs.find { it.name == "screen" || it.name.lowercase().contains("screen") } ?: availableRTs.first()
                                } else {
                                    selectedRepairType = null
                                }
                            }
                        }

                        if (isCatalogLoading && brands.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = TealPrimary)
                            }
                        } else {
                            // 1. SELECT BRAND DROPDOWN
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "SELECT BRAND",
                                    color = TealPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    onClick = { brandExpanded = !brandExpanded },
                                    color = NavyDark,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, GrayBorder),
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = selectedBrand?.name ?: "— Select Brand —",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Icon(
                                            imageVector = if (brandExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                            contentDescription = null,
                                            tint = TealPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                if (brandExpanded) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                                        border = BorderStroke(1.dp, GrayBorder),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(max = 240.dp)
                                                .verticalScroll(rememberScrollState())
                                                .padding(vertical = 4.dp)
                                        ) {
                                            brands.forEach { brand ->
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            selectedBrand = brand
                                                            brandExpanded = false
                                                        }
                                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                                ) {
                                                    Text(
                                                        text = brand.name,
                                                        color = if (selectedBrand?.id == brand.id) TealPrimary else Color.White,
                                                        fontSize = 12.sp,
                                                        fontWeight = if (selectedBrand?.id == brand.id) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 2. SELECT MODEL DROPDOWN
                            val filteredDevices = remember(selectedBrand, devices) {
                                if (selectedBrand != null) devices.filter { it.brand_id == selectedBrand?.id } else emptyList()
                            }

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "SELECT MODEL",
                                    color = TealPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    onClick = { if (filteredDevices.isNotEmpty()) deviceExpanded = !deviceExpanded },
                                    color = if (filteredDevices.isNotEmpty()) NavyDark else NavyDark.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, GrayBorder),
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = selectedDevice?.name ?: if (filteredDevices.isEmpty()) "— No Models Available —" else "— Select Model —",
                                            color = if (filteredDevices.isNotEmpty()) Color.White else Color.White.copy(alpha = 0.5f),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Icon(
                                            imageVector = if (deviceExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                            contentDescription = null,
                                            tint = if (filteredDevices.isNotEmpty()) TealPrimary else Color.Gray,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                if (deviceExpanded && filteredDevices.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                                        border = BorderStroke(1.dp, GrayBorder),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(max = 240.dp)
                                                .verticalScroll(rememberScrollState())
                                                .padding(vertical = 4.dp)
                                        ) {
                                            filteredDevices.forEach { dev ->
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            selectedDevice = dev
                                                            deviceExpanded = false
                                                        }
                                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                                ) {
                                                    Text(
                                                        text = dev.name,
                                                        color = if (selectedDevice?.id == dev.id) TealPrimary else Color.White,
                                                        fontSize = 12.sp,
                                                        fontWeight = if (selectedDevice?.id == dev.id) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 3. SELECT REPAIR TYPE DROPDOWN
                            val deviceParts = remember(selectedDevice, parts) {
                                if (selectedDevice != null) parts.filter { it.device_id == selectedDevice?.id } else emptyList()
                            }
                            val availableRepairTypes = remember(repairTypes) {
                                repairTypes
                            }

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "SELECT REPAIR TYPE",
                                    color = TealPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    onClick = { if (availableRepairTypes.isNotEmpty()) repairExpanded = !repairExpanded },
                                    color = if (availableRepairTypes.isNotEmpty()) NavyDark else NavyDark.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, GrayBorder),
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = selectedRepairType?.let { it.label ?: it.name } ?: if (availableRepairTypes.isEmpty()) "— No Repairs for this Model —" else "— Select Repair —",
                                            color = if (availableRepairTypes.isNotEmpty()) Color.White else Color.White.copy(alpha = 0.5f),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Icon(
                                            imageVector = if (repairExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                            contentDescription = null,
                                            tint = if (availableRepairTypes.isNotEmpty()) TealPrimary else Color.Gray,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                if (repairExpanded && availableRepairTypes.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                                        border = BorderStroke(1.dp, GrayBorder),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(max = 240.dp)
                                                .verticalScroll(rememberScrollState())
                                                .padding(vertical = 4.dp)
                                        ) {
                                            availableRepairTypes.forEach { rt ->
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            selectedRepairType = rt
                                                            repairExpanded = false
                                                        }
                                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                                ) {
                                                    Text(
                                                        text = rt.label ?: rt.name,
                                                        color = if (selectedRepairType?.id == rt.id) TealPrimary else Color.White,
                                                        fontSize = 12.sp,
                                                        fontWeight = if (selectedRepairType?.id == rt.id) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 4. PARTS QUALITY GRADE DROPDOWN
                            val qualityOptions = listOf("Premium Original Grade", "Certified High Copy", "Eco Refurbished/Budget")

                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "PARTS QUALITY GRADE",
                                    color = TealPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Surface(
                                    onClick = { qualityExpanded = !qualityExpanded },
                                    color = NavyDark,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, GrayBorder),
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = selectedQualityGrade,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Icon(
                                            imageVector = if (qualityExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                            contentDescription = null,
                                            tint = TealPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                if (qualityExpanded) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                                        border = BorderStroke(1.dp, GrayBorder),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                        ) {
                                            qualityOptions.forEach { grade ->
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            selectedQualityGrade = grade
                                                            qualityExpanded = false
                                                        }
                                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                                ) {
                                                    Text(
                                                        text = grade,
                                                        color = if (selectedQualityGrade == grade) TealPrimary else Color.White,
                                                        fontSize = 12.sp,
                                                        fontWeight = if (selectedQualityGrade == grade) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 5. PROMO CODE SWITCH / CHECKBOX ROW
                            Surface(
                                color = NavyDark,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, GrayBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = AmberAccent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "Apply Launch Promo",
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Get 50% OFF dynamic service fees",
                                                color = TealPrimary,
                                                fontSize = 9.5.sp
                                            )
                                        }
                                    }
                                    Switch(
                                        checked = isPromoApplied,
                                        onCheckedChange = { isPromoApplied = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.Black,
                                            checkedTrackColor = TealPrimary,
                                            uncheckedThumbColor = Color.Gray,
                                            uncheckedTrackColor = NavyDark
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Calculation Engine Variables
                            val qualityMultiplier = when (selectedQualityGrade) {
                                "Premium Original Grade" -> 1.00
                                "Certified High Copy" -> 0.80
                                "Eco Refurbished/Budget" -> 0.60
                                else -> 1.00
                            }

                            val matchedParts = remember(selectedDevice, selectedRepairType, parts) {
                                if (selectedDevice != null && selectedRepairType != null) {
                                    parts.filter { it.device_id == selectedDevice?.id && it.repair_type_id == selectedRepairType?.id }
                                } else {
                                    emptyList()
                                }
                            }

                            val basePartsCost = remember(matchedParts, qualityMultiplier) {
                                matchedParts.sumOf { it.price } * qualityMultiplier
                            }
                            val discountedPartsCost = basePartsCost * 0.90 // 10% Club Discount
                            val baseServiceFee = discountedPartsCost * 0.15
                            val finalServiceFee = if (isPromoApplied) baseServiceFee * 0.50 else baseServiceFee
                            val deliveryCharge = 250.00
                            val finalTotalEstimate = discountedPartsCost + finalServiceFee + deliveryCharge

                            // 6. REQUIRED SPARE PARTS PANEL
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(NavyDark)
                                    .border(1.dp, GrayBorder, RoundedCornerShape(14.dp))
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = "📋 REQUIRED PARTS BREAKDOWN",
                                    color = GrayText,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                if (matchedParts.isEmpty()) {
                                    Text(
                                        text = "No custom parts required for this selection.",
                                        color = GrayText,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                } else {
                                    matchedParts.forEach { part ->
                                        val priceVal = part.price * qualityMultiplier
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = part.name,
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = "₹${String.format("%,.2f", priceVal)}",
                                                color = Color.White,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                HorizontalDivider(color = GrayBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Subtotal Parts:",
                                        color = GrayText,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "₹${String.format("%,.2f", basePartsCost)}",
                                        color = TealPrimary,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 7. PRICING ESTIMATION SUMMARY
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(NavyDark)
                                    .border(1.dp, GrayBorder, RoundedCornerShape(14.dp))
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Discounted Parts (10% Club):", color = GrayText, fontSize = 11.sp)
                                    Text(
                                        text = "₹${String.format("%,.2f", discountedPartsCost)}",
                                        color = Color.White,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Dynamic Service Fee (15%):", color = GrayText, fontSize = 11.sp)
                                    Text(
                                        text = "₹${String.format("%,.2f", finalServiceFee)}",
                                        color = Color.White,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Doorstep Delivery & Diagnosis:", color = GrayText, fontSize = 11.sp)
                                    Text(
                                        text = "₹${String.format("%,.2f", deliveryCharge)}",
                                        color = AmberAccent,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                HorizontalDivider(color = GrayBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Estimated Total:", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "₹${String.format("%,.2f", finalTotalEstimate)}",
                                        color = TealPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Book Direct Action
                            val isQuoteMode = finalTotalEstimate > 15000.0
                            Button(
                                onClick = {
                                    if (viewModel.isLoggedIn.value) {
                                        viewModel.navigateTo(Screen.NewRepairRequest)
                                    } else {
                                        viewModel.navigateTo(Screen.Login)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isQuoteMode) AmberAccent else TealPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Icon(
                                    imageVector = if (isQuoteMode) Icons.Filled.Email else Icons.Filled.Add,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isQuoteMode) "Request Custom Quotation" else "Book Doorstep Repair",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Primary CTA Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val isLoggedInState = viewModel.isLoggedIn.value
                    Button(
                        onClick = {
                            if (isLoggedInState) {
                                val screen = when (userProfile.role) {
                                    "CUSTOMER" -> Screen.CustomerDashboard
                                    "TECHNICIAN" -> Screen.TechnicianDashboard
                                    "REPAIRMASTER" -> Screen.RepairMasterDashboard
                                    "COORDINATOR" -> Screen.CoordinatorDashboard
                                    "ADMIN" -> Screen.AdminDashboard
                                    "MARKETPLACE_BUYER" -> Screen.Marketplace
                                    else -> Screen.CustomerDashboard
                                }
                                viewModel.navigateTo(screen)
                            } else {
                                viewModel.navigateTo(Screen.Login)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp)
                            .testTag("landing_portal_cta")
                    ) {
                        Text(
                            text = if (isLoggedInState) "Enter Active Dashboard" else "Register & Book Repair",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = { viewModel.navigateTo(Screen.Marketplace) },
                        colors = ButtonDefaults.buttonColors(containerColor = NavySurface),
                        border = BorderStroke(1.dp, GrayBorder),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Browse Store", color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }

        // Marketplace Highlight Section (Now at Top)
        val hotDeals = listings.filter { !it.isSold }
        if (hotDeals.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🔥 HOT DEALS",
                            color = AccentRed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = TealPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "NAGPUR STOCK",
                                color = TealPrimary,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "See All",
                        color = TealPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { viewModel.navigateTo(Screen.Marketplace) }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    hotDeals.forEach { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = NavySurface),
                            border = BorderStroke(1.dp, GrayBorder),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .width(220.dp)
                                .clickable { viewModel.selectListing(item.id) }
                                .testTag("hot_deal_card_${item.id}")
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .background(NavyLightSurface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Filled.ShoppingCart,
                                        contentDescription = null,
                                        tint = TealPrimary.copy(alpha = 0.3f),
                                        modifier = Modifier.size(32.dp)
                                    )
                                    // Grade label at top end
                                    Surface(
                                        color = if (item.grade == "Mint") AccentGreen else if (item.grade == "Excellent") TealPrimary else AmberAccent,
                                        shape = RoundedCornerShape(bottomStart = 8.dp),
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    ) {
                                        Text(
                                            text = item.grade,
                                            color = Color.Black,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    // "HOT DEAL" badge at top start
                                    Surface(
                                        color = AccentRed,
                                        shape = RoundedCornerShape(bottomEnd = 8.dp),
                                        modifier = Modifier.align(Alignment.TopStart)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Icon(Icons.Filled.Star, contentDescription = null, tint = Color.Black, modifier = Modifier.size(8.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "HOT DEAL",
                                                color = Color.White,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = item.title,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = item.storage,
                                        color = GrayText,
                                        fontSize = 10.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "₹${String.format("%.0f", item.price)}",
                                            color = TealPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = GrayText, modifier = Modifier.size(10.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(text = item.city, color = GrayText, fontSize = 9.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Collapsible Repair Master Workflow Stages Section (Now below Hot Deals)
        Card(
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = BorderStroke(1.dp, GrayBorder),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .clickable { isWorkflowExpanded = !isWorkflowExpanded }
                .testTag("workflow_collapsible_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(TealPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Settings,
                                contentDescription = null,
                                tint = TealPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "REPAIR WORKFLOW STAGES",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Tap to view our secure end-to-end doorstep process",
                                color = GrayText,
                                fontSize = 10.sp
                            )
                        }
                    }
                    IconButton(
                        onClick = { isWorkflowExpanded = !isWorkflowExpanded },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (isWorkflowExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (isWorkflowExpanded) "Collapse" else "Expand",
                            tint = TealPrimary
                        )
                    }
                }

                AnimatedVisibility(
                    visible = isWorkflowExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val steps = listOf(
                            Triple(Icons.Filled.LocationOn, "1. Mandatory Maintenance Mode & OTP Verification", "Prior to handover, you must enable Maintenance Mode on your device to lock all private data. A certified technician arrives, verifies that Maintenance Mode is ACTIVE, and scans your secure 4-digit handover OTP to safely collect the device in locked tamper-proof cases."),
                            Triple(Icons.Filled.Settings, "2. Diagnostic Report & Live Approval", "Devices are inspected in high-tech laboratories. Receive a precise replacement parts breakdown, check total amounts, and approve repairs with a single tap."),
                            Triple(Icons.Filled.CheckCircle, "3. Secure Handback & Warranty", "The repaired smartphone is returned directly to your door. Test displays, cameras, and batteries, then share your secure delivery handover code.")
                        )

                        steps.forEach { (icon, title, desc) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(NavyLightSurface, RoundedCornerShape(10.dp))
                                    .padding(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(TealPrimary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        icon,
                                        contentDescription = null,
                                        tint = TealPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = title,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = desc,
                                        color = GrayText,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Marketplace Highlight Section
        Card(
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Escrow Certified Marketplace", color = TealPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Looking for a Refurbished Phone?", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Explore our certified stores catalog. Every listing undergoes rigorous multi-point functional inspections by authorized Repair Masters before listing. Buy with 100% security with locked escrow payouts.",
                    color = GrayText,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.navigateTo(Screen.Marketplace) },
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Explore Marketplace Catalog", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Partnerships Onboarding Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Want to Join the Network?", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Earn high-density repair quotas or list shop refurbished devices.", color = GrayText, fontSize = 12.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { viewModel.navigateTo(Screen.Careers) },
                colors = ButtonDefaults.buttonColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, GrayBorder),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Join as Technician or Partner Shop", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Footer Brand Note
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyDark)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_banner_1782690134468),
                    contentDescription = "RepairMaster Brand Banner",
                    modifier = Modifier
                        .height(36.dp)
                        .fillMaxWidth(0.85f),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Nagpur's Doorstep Platform © 2026", color = GrayText, fontSize = 11.sp)
                Text(text = "Trust • Precision • Convenience", color = TealPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}
