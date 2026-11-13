package com.adytransjaya.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [UserEntity::class, CarEntity::class, BookingEntity::class],
    version = 4,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun carDao(): CarDao
    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        /**
         * Migrasi v1 → v2: menambah tabel `cars` dan `bookings` tanpa
         * menghapus akun pengguna yang sudah terdaftar.
         */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `cars` (" +
                        "`id` TEXT NOT NULL, " +
                        "`name` TEXT NOT NULL, " +
                        "`brand` TEXT NOT NULL, " +
                        "`plate` TEXT NOT NULL, " +
                        "`seats` INTEGER NOT NULL, " +
                        "`transmission` TEXT NOT NULL, " +
                        "`pricePerDay` INTEGER NOT NULL, " +
                        "`status` TEXT NOT NULL, " +
                        "`year` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`))",
                )
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `bookings` (" +
                        "`code` TEXT NOT NULL, " +
                        "`carId` TEXT NOT NULL, " +
                        "`customerId` TEXT NOT NULL, " +
                        "`startDate` TEXT NOT NULL, " +
                        "`endDate` TEXT NOT NULL, " +
                        "`days` INTEGER NOT NULL, " +
                        "`total` INTEGER NOT NULL, " +
                        "`status` TEXT NOT NULL, " +
                        "PRIMARY KEY(`code`))",
                )
            }
        }

        /**
         * Migrasi v2 → v3: menambah status aktif akun agar Pemilik bisa
         * menonaktifkan karyawan tanpa menghapus riwayatnya.
         */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE `users` ADD COLUMN `isActive` INTEGER NOT NULL DEFAULT 1",
                )
            }
        }

        /**
         * Migrasi v3 → v4: menambah status pembayaran pesanan. Pesanan yang
         * sudah selesai dianggap lunas agar laporan lama tetap konsisten.
         */
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE `bookings` ADD COLUMN " +
                        "`paymentStatus` TEXT NOT NULL DEFAULT 'UNPAID'",
                )
                database.execSQL(
                    "ALTER TABLE `bookings` ADD COLUMN " +
                        "`paidAmount` INTEGER NOT NULL DEFAULT 0",
                )
                database.execSQL(
                    "UPDATE `bookings` SET `paymentStatus` = 'PAID', " +
                        "`paidAmount` = `total` WHERE `status` = 'COMPLETED'",
                )
            }
        }

        fun get(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ady_trans_jaya.db",
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                    .also { instance = it }
            }
    }
}
