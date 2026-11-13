package com.adytransjaya.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adytransjaya.app.data.model.Role
import com.adytransjaya.app.data.model.User
import com.adytransjaya.app.ui.common.Avatar
import com.adytransjaya.app.ui.home.tabs.BookingsTab
import com.adytransjaya.app.ui.home.tabs.CarsTab
import com.adytransjaya.app.ui.home.tabs.CustomersTab
import com.adytransjaya.app.ui.home.tabs.OverviewTab
import com.adytransjaya.app.ui.home.tabs.ProfileTab
import com.adytransjaya.app.ui.home.tabs.ReportTab
import com.adytransjaya.app.ui.theme.BrandPrimary

enum class HomeTab(
    val label: String,
    val icon: ImageVector,
) {
    OVERVIEW("Beranda", Icons.Rounded.Home),
    CARS("Mobil", Icons.Rounded.DirectionsCar),
    BOOKINGS("Pesanan", Icons.Rounded.ReceiptLong),
    REPORT("Laporan", Icons.Rounded.BarChart),
    CUSTOMERS("Pelanggan", Icons.Rounded.Groups),
    PROFILE("Profil", Icons.Rounded.Person),
}

fun tabsFor(role: Role): List<HomeTab> = when (role) {
    Role.OWNER -> listOf(
        HomeTab.OVERVIEW,
        HomeTab.CARS,
        HomeTab.BOOKINGS,
        HomeTab.REPORT,
        HomeTab.PROFILE,
    )

    Role.EMPLOYEE -> listOf(
        HomeTab.OVERVIEW,
        HomeTab.CARS,
        HomeTab.BOOKINGS,
        HomeTab.CUSTOMERS,
        HomeTab.PROFILE,
    )

    Role.CUSTOMER -> listOf(
        HomeTab.OVERVIEW,
        HomeTab.CARS,
        HomeTab.BOOKINGS,
        HomeTab.PROFILE,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    user: User,
    onLogout: () -> Unit,
    onUserUpdated: (User) -> Unit,
) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.Factory(context),
    )
    val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val tabs = remember(user.role) { tabsFor(user.role) }
    var selectedTabName by rememberSaveable(user.id) {
        mutableStateOf(tabs.first().name)
    }
    val selectedTab = remember(selectedTabName, tabs) {
        tabs.firstOrNull { it.name == selectedTabName } ?: tabs.first()
    }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(homeState.message) {
        val message = homeState.message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        homeViewModel.clearMessage()
    }

    LaunchedEffect(homeState.updatedUser) {
        val updated = homeState.updatedUser ?: return@LaunchedEffect
        onUserUpdated(updated)
        homeViewModel.clearUpdatedUser()
    }

    Scaffold(
        modifier = Modifier.testTag("home_root"),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = selectedTab.label,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                        )
                        Text(
                            text = "Ady Trans Jaya · ${user.role.label}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.72f),
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { selectedTabName = HomeTab.PROFILE.name },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("profile_avatar"),
                    ) {
                        Avatar(user = user, size = 36.dp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrandPrimary,
                    titleContentColor = Color.White,
                ),
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
            ) {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTabName = tab.name },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                modifier = Modifier.size(22.dp),
                            )
                        },
                        label = {
                            Text(
                                text = tab.label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        },
                        modifier = Modifier.testTag("tab_${tab.name.lowercase()}"),
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
        ) {
            if (homeState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                when (selectedTab) {
                    HomeTab.OVERVIEW -> OverviewTab(
                        user = user,
                        cars = homeState.cars,
                        bookings = homeState.bookings,
                        customers = homeState.customers,
                        onNavigate = { selectedTabName = it.name },
                    )

                    HomeTab.CARS -> CarsTab(
                        user = user,
                        cars = homeState.cars,
                        onSaveCar = homeViewModel::saveCar,
                        onDeleteCar = homeViewModel::deleteCar,
                    )

                    HomeTab.BOOKINGS -> BookingsTab(
                        user = user,
                        bookings = homeState.bookings,
                        cars = homeState.cars,
                        customers = homeState.customers,
                        onCreateBooking = { carId, startDay, endDay ->
                            homeViewModel.createBooking(
                                carId = carId,
                                customerId = user.id,
                                startEpochDay = startDay,
                                endEpochDay = endDay,
                            )
                        },
                        onUpdateStatus = { code, status ->
                            homeViewModel.updateBookingStatus(
                                code = code,
                                newStatus = status,
                                actorRole = user.role,
                                actorId = user.id,
                            )
                        },
                        onUpdatePayment = { code, status ->
                            homeViewModel.updatePaymentStatus(
                                code = code,
                                newStatus = status,
                                actorRole = user.role,
                            )
                        },
                    )

                    HomeTab.REPORT -> ReportTab(
                        cars = homeState.cars,
                        bookings = homeState.bookings,
                        customers = homeState.customers,
                    )

                    HomeTab.CUSTOMERS -> CustomersTab(
                        customers = homeState.customers,
                        bookings = homeState.bookings,
                    )

                    HomeTab.PROFILE -> ProfileTab(
                        user = user,
                        employees = homeState.employees,
                        onSaveProfile = { name, phone ->
                            homeViewModel.updateProfile(user.id, name, phone)
                        },
                        onChangePassword = { current, new ->
                            homeViewModel.changePassword(user.id, current, new)
                        },
                        onCreateEmployee = { name, email, phone, password ->
                            homeViewModel.createEmployee(name, email, phone, password)
                        },
                        onToggleEmployeeActive = { employeeId, active ->
                            homeViewModel.setEmployeeActive(employeeId, active)
                        },
                        onLogoutRequest = { showLogoutDialog = true },
                    )
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Keluar dari akun?") },
            text = {
                Text(
                    "Anda akan kembali ke halaman login. " +
                        "Sesi ${user.role.label.lowercase()} akan diakhiri.",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    modifier = Modifier.testTag("confirm_logout"),
                ) {
                    Text(
                        text = "Keluar",
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal")
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
        )
    }
}
