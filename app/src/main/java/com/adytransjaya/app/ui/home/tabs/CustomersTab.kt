package com.adytransjaya.app.ui.home.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adytransjaya.app.data.model.Booking
import com.adytransjaya.app.data.model.BookingStatus
import com.adytransjaya.app.data.model.User
import com.adytransjaya.app.ui.common.Avatar
import com.adytransjaya.app.ui.common.EmptyState
import com.adytransjaya.app.ui.common.formatCompactRupiah

@Composable
fun CustomersTab(
    customers: List<User>,
    bookings: List<Booking>,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val filteredCustomers = remember(query, customers) {
        if (query.isBlank()) {
            customers
        } else {
            customers.filter {
                it.name.contains(query, ignoreCase = true) ||
                    it.email.contains(query, ignoreCase = true) ||
                    it.phone.contains(query, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column {
                Text(
                    text = "Data pelanggan",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${customers.size} pelanggan terdaftar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari nama, email, atau telepon") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
            )
        }

        if (filteredCustomers.isEmpty()) {
            item {
                EmptyState(
                    title = "Pelanggan tidak ditemukan",
                    message = "Coba kata kunci lain.",
                    icon = Icons.Rounded.Groups,
                )
            }
        } else {
            items(filteredCustomers, key = { it.id }) { customer ->
                CustomerCard(customer = customer, bookings = bookings)
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun CustomerCard(
    customer: User,
    bookings: List<Booking>,
) {
    val customerBookings = bookings.filter { it.customerId == customer.id }
    val completed = customerBookings.count { it.status == BookingStatus.COMPLETED }
    val totalSpend = customerBookings
        .filter { it.status == BookingStatus.COMPLETED }
        .sumOf { it.total }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Avatar(user = customer, size = 48.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = customer.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = customer.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = customer.phone,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${customerBookings.size} pesanan · $completed selesai · " +
                        formatCompactRupiah(totalSpend),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
