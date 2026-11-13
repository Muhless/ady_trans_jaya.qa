package com.adytransjaya.app.ui.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adytransjaya.app.ui.theme.BrandPrimaryDark
import com.adytransjaya.app.ui.theme.BrandTeal

/**
 * Layar pembuka (splash) dengan branding Ady Trans Jaya.
 *
 * Ditampilkan sebentar sebelum halaman login atau dashboard, supaya
 * aplikasi tidak terasa "kosong" saat pertama kali dibuka.
 */
@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "splash")

    val ringScale by transition.animateFloat(
        initialValue = 0.82f,
        targetValue = 1.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ring-scale",
    )
    val ringAlpha by transition.animateFloat(
        initialValue = 0.30f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ring-alpha",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("splash_screen")
            .background(
                Brush.linearGradient(
                    colors = listOf(BrandPrimaryDark, BrandTeal),
                ),
            ),
    ) {
        // Ornamen lingkaran transparan agar latar tidak terasa datar.
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 72.dp, y = (-64).dp)
                .size(250.dp)
                .background(Color.White.copy(alpha = 0.06f), CircleShape),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-84).dp, y = 72.dp)
                .size(210.dp)
                .background(Color.White.copy(alpha = 0.05f), CircleShape),
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .graphicsLayer {
                            scaleX = ringScale
                            scaleY = ringScale
                            alpha = ringAlpha
                        }
                        .background(Color.White.copy(alpha = 0.22f), CircleShape),
                )
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .background(Color.White.copy(alpha = 0.14f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DirectionsCar,
                        contentDescription = "Logo Ady Trans Jaya",
                        tint = Color.White,
                        modifier = Modifier.size(58.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "Ady Trans Jaya",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Rental mobil nyaman untuk setiap perjalanan",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.82f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 48.dp),
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = Color.White.copy(alpha = 0.9f),
                strokeWidth = 2.dp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Menyiapkan perjalanan Anda...",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.72f),
            )
        }
    }
}
