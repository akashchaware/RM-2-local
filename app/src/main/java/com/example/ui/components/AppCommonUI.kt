package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.Screen
import com.example.ui.theme.*

// Branded circular RM Badge Icon in pure Canvas / Compose
@Composable
fun RepairMasterLogo(modifier: Modifier = Modifier, sizeDp: Int = 56) {
    Image(
        painter = painterResource(id = R.drawable.img_brand_logo_circular_1782765220741),
        contentDescription = "RepairMaster Brand Logo",
        modifier = modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .border(2.dp, TealPrimary, CircleShape),
        contentScale = ContentScale.Crop
    )
}

// Custom badges based on roles
@Composable
fun RoleBadge(role: String) {
    val (bgColor, textColor, label) = when (role) {
        "CUSTOMER" -> Triple(TealPrimary.copy(alpha = 0.15f), TealPrimary, "📱 Customer")
        "TECHNICIAN" -> Triple(NavyLightSurface, Color.White, "🔧 Technician")
        "REPAIRMASTER" -> Triple(AccentGreen.copy(alpha = 0.15f), AccentGreen, "🏪 Repair Master")
        "COORDINATOR" -> Triple(AmberAccent.copy(alpha = 0.15f), AmberAccent, "📋 Coordinator")
        "ADMIN" -> Triple(AccentRed.copy(alpha = 0.15f), AccentRed, "⚙️ Admin")
        "MARKETPLACE_BUYER" -> Triple(Color(0xFF818CF8).copy(alpha = 0.15f), Color(0xFF818CF8), "🛒 Marketplace Buyer")
        else -> Triple(Color.Gray.copy(alpha = 0.15f), Color.Gray, role)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// 11-step repair process progress indicator
@Composable
fun RepairStatusBadge(step: Int) {
    val label = when (step) {
        1 -> "New Request"
        2 -> "Tech Assigned"
        3 -> "Pickup Dispatched"
        4 -> "Device Collected"
        5 -> "Diagnosing"
        6 -> "Awaiting Approval"
        7 -> "Repairing"
        8 -> "Quality Checked"
        9 -> "Out for Delivery"
        10 -> "Delivery OTP"
        11 -> "Completed"
        else -> "Processing"
    }

    val color = when (step) {
        1 -> Color(0xFF10B981) // Emerald Green (Highlight)
        2, 3 -> Color(0xFF38BDF8) // Sky Blue
        4, 5 -> Color(0xFFF59E0B) // Amber
        6 -> Color(0xFFF43F5E) // Red Rose
        7, 8 -> Color(0xFF10B981) // Emerald Green
        9, 10 -> Color(0xFF8B5CF6) // Violet
        11 -> Color(0xFF10B981)
        else -> TealPrimary
    }

    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Static fallback image placeholder if URL doesn't load
@Composable
fun ImagePlaceholder(text: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(NavySurface),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TealPrimary.copy(alpha = 0.6f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                color = GrayText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

// Custom Stat Card
@Composable
fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    color = GrayText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// Quick testing Role Switcher dialog
@Composable
fun RoleSwitcherDialog(
    currentRole: String,
    onRoleSelected: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedRole by remember { mutableStateOf(currentRole) }
    var passcode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val roles = listOf(
        Pair("CUSTOMER", "📱 Customer (No Code)"),
        Pair("MARKETPLACE_BUYER", "🛒 Marketplace Buyer (No Code)"),
        Pair("TECHNICIAN", "🔧 Technician (tech@rm2024)"),
        Pair("REPAIRMASTER", "🏪 Repair Master (master@rm2024)"),
        Pair("COORDINATOR", "📋 Coordinator (coord@rm2024)"),
        Pair("ADMIN", "⚙️ Admin (admin@rm2024)")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Switch Testing Role",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Column {
                Text(
                    text = "Switch to any role for testing purposes. Privileged roles require a passcode.",
                    fontSize = 13.sp,
                    color = GrayText,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Dropdown or list of options
                Text(
                    text = "Select Role",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = TealPrimary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NavyDark, RoundedCornerShape(8.dp))
                        .padding(4.dp)
                ) {
                    roles.forEach { (roleKey, roleLabel) ->
                        val isSelected = selectedRole == roleKey
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) TealPrimary.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable {
                                    selectedRole = roleKey
                                    passcode = ""
                                    errorMessage = null
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    selectedRole = roleKey
                                    passcode = ""
                                    errorMessage = null
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = TealPrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = roleLabel,
                                color = if (isSelected) TealPrimary else Color.White,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Show passcode input for restricted roles
                if (selectedRole != "CUSTOMER" && selectedRole != "MARKETPLACE_BUYER") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Enter Passcode",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = TealPrimary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = passcode,
                        onValueChange = {
                            passcode = it
                            errorMessage = null
                        },
                        placeholder = { Text("e.g. admin@rm2024", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealPrimary,
                            unfocusedBorderColor = GrayBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("role_passcode_input"),
                        singleLine = true
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = AccentRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onRoleSelected(selectedRole, passcode)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                modifier = Modifier.testTag("apply_role_button")
            ) {
                Text("Apply Role", color = Color.Black, fontWeight = FontWeight.Bold)
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
fun LiveMapTrackingView(
    technicianName: String,
    etaMins: Int,
    statusText: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 24f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    val movingProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = BorderStroke(1.dp, GrayBorder),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyLightSurface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(AccentGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LIVE DOORSTEP DISPATCH TRACKER",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                Surface(
                    color = TealPrimary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "ETA: $etaMins MINS",
                        color = TealPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(NavyDark)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    val gridSpacing = 30f
                    for (x in 0..(w / gridSpacing).toInt()) {
                        drawLine(
                            color = Color(0xFF1E293B).copy(alpha = 0.4f),
                            start = Offset(x * gridSpacing, 0f),
                            end = Offset(x * gridSpacing, h),
                            strokeWidth = 1f
                        )
                    }
                    for (y in 0..(h / gridSpacing).toInt()) {
                        drawLine(
                            color = Color(0xFF1E293B).copy(alpha = 0.4f),
                            start = Offset(0f, y * gridSpacing),
                            end = Offset(w, y * gridSpacing),
                            strokeWidth = 1f
                        )
                    }

                    drawLine(
                        color = Color(0xFF334155),
                        start = Offset(w * 0.25f, 0f),
                        end = Offset(w * 0.25f, h),
                        strokeWidth = 16f
                    )
                    drawLine(
                        color = Color(0xFF334155),
                        start = Offset(0f, h * 0.65f),
                        end = Offset(w, h * 0.65f),
                        strokeWidth = 16f
                    )
                    drawLine(
                        color = Color(0xFF334155),
                        start = Offset(0f, h * 0.15f),
                        end = Offset(w, h * 0.9f),
                        strokeWidth = 12f
                    )

                    val pStart = Offset(w * 0.1f, h * 0.2f)
                    val pIntermediate = Offset(w * 0.5f, h * 0.5f)
                    val pDestination = Offset(w * 0.8f, h * 0.65f)

                    drawLine(
                        color = GrayText.copy(alpha = 0.3f),
                        start = pStart,
                        end = pIntermediate,
                        strokeWidth = 4f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                    drawLine(
                        color = GrayText.copy(alpha = 0.3f),
                        start = pIntermediate,
                        end = pDestination,
                        strokeWidth = 4f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )

                    val currentPos = if (movingProgress < 0.5f) {
                        val t = movingProgress / 0.5f
                        Offset(
                            pStart.x + (pIntermediate.x - pStart.x) * t,
                            pStart.y + (pIntermediate.y - pStart.y) * t
                        )
                    } else {
                        val t = (movingProgress - 0.5f) / 0.5f
                        Offset(
                            pIntermediate.x + (pDestination.x - pIntermediate.x) * t,
                            pIntermediate.y + (pDestination.y - pIntermediate.y) * t
                        )
                    }

                    if (movingProgress < 0.5f) {
                        drawLine(
                            color = TealPrimary,
                            start = pStart,
                            end = currentPos,
                            strokeWidth = 4f
                        )
                    } else {
                        drawLine(
                            color = TealPrimary,
                            start = pStart,
                            end = pIntermediate,
                            strokeWidth = 4f
                        )
                        drawLine(
                            color = TealPrimary,
                            start = pIntermediate,
                            end = currentPos,
                            strokeWidth = 4f
                        )
                    }

                    drawCircle(
                        color = AccentRed.copy(alpha = 0.2f),
                        center = pDestination,
                        radius = 20f
                    )
                    drawCircle(
                        color = AccentRed,
                        center = pDestination,
                        radius = 6f
                    )

                    drawCircle(
                        color = TealPrimary.copy(alpha = pulseAlpha),
                        center = currentPos,
                        radius = pulseRadius
                    )
                    drawCircle(
                        color = TealPrimary,
                        center = currentPos,
                        radius = 6f
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = 10.dp, y = 10.dp)
                        .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("You (Home Location)", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-10).dp, y = (-10).dp)
                        .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.AccountBox,
                            contentDescription = null,
                            tint = AmberAccent,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Technician: $technicianName", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(TealPrimary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Build, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = statusText,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Verified dispatch carrying secure tamper-proof casing",
                        color = GrayText,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
