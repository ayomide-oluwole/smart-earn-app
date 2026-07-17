package com.smartearn.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartearn.app.data.local.AppDatabase
import com.smartearn.app.data.local.EarningsEntity
import com.smartearn.app.util.EarningsCalculator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onWithdraw: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentEarnings by remember { mutableFloatStateOf(0f) }
    var totalEarned by remember { mutableFloatStateOf(0f) }
    var timeUntilClaim by remember { mutableLongStateOf(EarningsCalculator.CYCLE_MILLIS) }
    var canClaimNow by remember { mutableStateOf(false) }
    var lastClaimTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var claimedCount by remember { mutableIntStateOf(0) }
    var showClaimAnimation by remember { mutableStateOf(false) }

    // Timer update every second
    LaunchedEffect(lastClaimTime) {
        while (true) {
            val pending = EarningsCalculator.calculatePendingEarnings(lastClaimTime)
            currentEarnings = pending.toFloat()
            timeUntilClaim = EarningsCalculator.getTimeUntilNextClaim(lastClaimTime)
            canClaimNow = EarningsCalculator.canClaim(lastClaimTime)
            delay(1000L)
        }
    }

    // Load data from DB on launch
    LaunchedEffect(Unit) {
        try {
            val db = AppDatabase.getInstance(context)
            val total = db.earningsDao().getTotalEarnings() ?: 0.0
            totalEarned = total.toFloat()
            val allEarnings = db.earningsDao().getAllEarnings()
            claimedCount = allEarnings.size
            if (allEarnings.isNotEmpty()) {
                lastClaimTime = allEarnings.first().claimedAt
            }
        } catch (_: Exception) { }
    }

    fun claimEarnings() {
        if (!canClaimNow) return
        scope.launch {
            try {
                val db = AppDatabase.getInstance(context)
                val amount = EarningsCalculator.CLAIM_AMOUNT
                db.earningsDao().insertEarning(EarningsEntity(amount = amount))
                totalEarned += amount.toFloat()
                claimedCount++
                lastClaimTime = System.currentTimeMillis()
                canClaimNow = false
                showClaimAnimation = true
                currentEarnings = 0f

                // Start background service
                com.smartearn.app.service.BackgroundService.start(context)

                kotlinx.coroutines.delay(2000)
                showClaimAnimation = false
            } catch (_: Exception) { }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Smart Earn",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(onClick = { /* settings */ }) {
                Text("⚙️", fontSize = 24.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Balance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Current Balance",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "₦${"%.2f".format(currentEarnings)}",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "≈ ${(currentEarnings / 500 * 100).toInt()}% of daily goal",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Timer Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Next claim in",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Countdown
                val hours = timeUntilClaim / 3_600_000
                val minutes = (timeUntilClaim % 3_600_000) / 60_000
                val seconds = (timeUntilClaim % 60_000) / 1_000

                Text(
                    text = "%02d : %02d : %02d".format(hours, minutes, seconds),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (canClaimNow) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Progress bar
                val progress = if (canClaimNow) 1f
                else 1f - (timeUntilClaim.toFloat() / EarningsCalculator.CYCLE_MILLIS)

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mine Button
        Button(
            onClick = { claimEarnings() },
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            enabled = canClaimNow && !showClaimAnimation,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (canClaimNow) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "💎", fontSize = 32.sp)
                if (canClaimNow) {
                    Text(
                        text = "MINE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (showClaimAnimation) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "₦${"%.2f".format(EarningsCalculator.CLAIM_AMOUNT)} claimed! 🎉",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                icon = "💰",
                label = "Total Earned",
                value = "₦${"%.2f".format(totalEarned)}"
            )
            StatItem(
                icon = "🎯",
                label = "Claims",
                value = "$claimedCount"
            )
            StatItem(
                icon = "📊",
                label = "Today",
                value = "₦${"%.2f".format(totalEarned)}"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Withdraw Button
        OutlinedButton(
            onClick = onWithdraw,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalance,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "WITHDRAW (Min ₦500)",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun StatItem(icon: String, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}