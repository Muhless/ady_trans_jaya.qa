package com.adytransjaya.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Badge
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Inbox
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.adytransjaya.app.data.model.BookingStatus
import com.adytransjaya.app.data.model.CarStatus
import com.adytransjaya.app.data.model.Role
import com.adytransjaya.app.data.model.User
import com.adytransjaya.app.ui.theme.Success
import com.adytransjaya.app.ui.theme.SuccessLight
import com.adytransjaya.app.ui.theme.Danger
import com.adytransjaya.app.ui.theme.DangerLight
import com.adytransjaya.app.ui.theme.BrandAmber
import com.adytransjaya.app.ui.theme.BrandAmberLight

fun roleIcon(role: Role): ImageVector = when (role) {
    Role.OWNER -> Icons.Rounded.Shield
    Role.EMPLOYEE -> Icons.Rounded.Badge
    Role.CUSTOMER -> Icons.Rounded.Person
}

@Composable
fun roleContainerColor(role: Role): Color = when (role) {
    Role.OWNER -> MaterialTheme.colorScheme.primaryContainer
    Role.EMPLOYEE -> MaterialTheme.colorScheme.secondaryContainer
    Role.CUSTOMER -> MaterialTheme.colorScheme.tertiaryContainer
}

@Composable
fun roleContentColor(role: Role): Color = when (role) {
    Role.OWNER -> MaterialTheme.colorScheme.onPrimaryContainer
    Role.EMPLOYEE -> MaterialTheme.colorScheme.onSecondaryContainer
    Role.CUSTOMER -> MaterialTheme.colorScheme.onTertiaryContainer
}

@Composable
fun RoleBadge(role: Role, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = roleContainerColor(role),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
        ) {
            Icon(
                imageVector = roleIcon(role),
                contentDescription = null,
                tint = roleContentColor(role),
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = role.label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = roleContentColor(role),
            )
        }
    }
}

@Composable
fun Avatar(
    user: User,
    size: Dp = 42.dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(size)
            .background(
                color = roleContainerColor(user.role),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = user.initials,
            color = roleContentColor(user.role),
            style = if (size >= 64.dp) {
                MaterialTheme.typography.headlineSmall
            } else {
                MaterialTheme.typography.titleSmall
            },
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun BookingStatusPill(status: BookingStatus) {
    val (container, content) = when (status) {
        BookingStatus.PENDING -> BrandAmberLight to Color(0xFF6A4A00)
        BookingStatus.ACTIVE -> MaterialTheme.colorScheme.primaryContainer to
            MaterialTheme.colorScheme.onPrimaryContainer
        BookingStatus.COMPLETED -> SuccessLight to Success
        BookingStatus.CANCELLED -> DangerLight to Danger
    }

    Surface(shape = RoundedCornerShape(50), color = container) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = content,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
        )
    }
}

@Composable
fun CarStatusPill(status: CarStatus) {
    val (container, content) = when (status) {
        CarStatus.AVAILABLE -> SuccessLight to Success
        CarStatus.RENTED -> MaterialTheme.colorScheme.primaryContainer to
            MaterialTheme.colorScheme.onPrimaryContainer
        CarStatus.MAINTENANCE -> BrandAmberLight to Color(0xFF6A4A00)
    }

    Surface(shape = RoundedCornerShape(50), color = container) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = content,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f),
        )
        if (actionLabel != null && onAction != null) {
            Surface(
                onClick = onAction,
                shape = RoundedCornerShape(50),
                color = Color.Transparent,
            ) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Rounded.Inbox,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(20.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(30.dp),
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun CarIconBox(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Box(
        modifier = modifier
            .size(size)
            .background(containerColor, RoundedCornerShape(18.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.DirectionsCar,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(size * 0.48f),
        )
    }
}

@Composable
fun SpecChip(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
        )
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(110.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
        )
    }
}

val ScreenPadding = PaddingValues(16.dp)
