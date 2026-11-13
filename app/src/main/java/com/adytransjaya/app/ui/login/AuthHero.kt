package com.adytransjaya.app.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adytransjaya.app.ui.theme.BrandPrimaryDark
import com.adytransjaya.app.ui.theme.BrandTeal

@Composable
fun AuthHero(
    title: String,
    subtitle: String,
    badge: String = "PEMILIK  ·  KARYAWAN  ·  PELANGGAN",
    onBack: (() -> Unit)? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(258.dp)
            .background(
                Brush.linearGradient(
                    colors = listOf(BrandPrimaryDark, BrandTeal),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-40).dp)
                .size(180.dp)
                .background(Color.White.copy(alpha = 0.06f), CircleShape),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-60).dp, y = 50.dp)
                .size(150.dp)
                .background(Color.White.copy(alpha = 0.05f), CircleShape),
        )

        if (onBack != null) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(8.dp)
                    .testTag("back_to_login"),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Kembali",
                    tint = Color.White,
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .background(Color.White.copy(alpha = 0.14f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.DirectionsCar,
                    contentDescription = "Logo Ady Trans Jaya",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp),
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.82f),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(14.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.14f),
            ) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                )
            }
        }
    }
}
