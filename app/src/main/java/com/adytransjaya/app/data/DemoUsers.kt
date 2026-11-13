package com.adytransjaya.app.data

import com.adytransjaya.app.data.model.Role
import com.adytransjaya.app.data.model.User

/**
 * Akun contoh untuk mencoba alur login tanpa backend.
 * Nanti bisa diganti dengan data dari API tanpa mengubah layer UI.
 */
object DemoUsers {

    val owner = User(
        id = "owner-001",
        name = "Budi Santoso",
        email = "owner@adytransjaya.id",
        phone = "0812-3456-7890",
        role = Role.OWNER,
        joinedAt = "Januari 2023",
    )

    val employee = User(
        id = "employee-001",
        name = "Siti Rahma",
        email = "karyawan@adytransjaya.id",
        phone = "0813-2233-4455",
        role = Role.EMPLOYEE,
        joinedAt = "Maret 2024",
    )

    val andi = User(
        id = "customer-001",
        name = "Andi Pratama",
        email = "customer@adytransjaya.id",
        phone = "0857-8899-1122",
        role = Role.CUSTOMER,
        joinedAt = "Agustus 2025",
    )

    val rina = User(
        id = "customer-002",
        name = "Rina Wulandari",
        email = "rina.wulandari@mail.com",
        phone = "0819-3344-7788",
        role = Role.CUSTOMER,
        joinedAt = "Februari 2025",
    )

    val dimas = User(
        id = "customer-003",
        name = "Dimas Saputra",
        email = "dimas.saputra@mail.com",
        phone = "0878-5566-9900",
        role = Role.CUSTOMER,
        joinedAt = "Juni 2025",
    )

    val customers: List<User> = listOf(andi, rina, dimas)
}
