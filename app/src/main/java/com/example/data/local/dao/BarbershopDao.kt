package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AppointmentEntity
import com.example.data.local.entity.AvailabilityBlockEntity
import com.example.data.local.entity.ClientUserEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.ServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BarbershopDao {
    // --- Services ---
    @Query("SELECT * FROM services ORDER BY category ASC, price ASC")
    fun getAllServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE isActive = 1 ORDER BY category ASC, price ASC")
    fun getActiveServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE id = :id LIMIT 1")
    suspend fun getServiceById(id: Long): ServiceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<ServiceEntity>)

    @Update
    suspend fun updateService(service: ServiceEntity)

    @Delete
    suspend fun deleteService(service: ServiceEntity)

    @Query("DELETE FROM services WHERE id = :id")
    suspend fun deleteServiceById(id: Long)

    // --- Appointments ---
    @Query("SELECT * FROM appointments ORDER BY date ASC, timeSlot ASC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE clientEmail = :email ORDER BY date DESC, timeSlot DESC")
    fun getAppointmentsForClient(email: String): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE date = :date ORDER BY timeSlot ASC")
    fun getAppointmentsForDate(date: String): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE id = :id LIMIT 1")
    suspend fun getAppointmentById(id: Long): AppointmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointments(appointments: List<AppointmentEntity>)

    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)

    @Query("UPDATE appointments SET status = :status WHERE id = :id")
    suspend fun updateAppointmentStatus(id: Long, status: String)

    @Delete
    suspend fun deleteAppointment(appointment: AppointmentEntity)

    // --- Availability Blocks ---
    @Query("SELECT * FROM availability_blocks ORDER BY date ASC, startTime ASC")
    fun getAllBlocks(): Flow<List<AvailabilityBlockEntity>>

    @Query("SELECT * FROM availability_blocks WHERE date = :date")
    fun getBlocksForDate(date: String): Flow<List<AvailabilityBlockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlock(block: AvailabilityBlockEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlocks(blocks: List<AvailabilityBlockEntity>)

    @Delete
    suspend fun deleteBlock(block: AvailabilityBlockEntity)

    @Query("DELETE FROM availability_blocks WHERE id = :id")
    suspend fun deleteBlockById(id: Long)

    // --- Users / Clients ---
    @Query("SELECT * FROM users WHERE role = 'CLIENT' ORDER BY name ASC")
    fun getAllClients(): Flow<List<ClientUserEntity>>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): ClientUserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: ClientUserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<ClientUserEntity>)

    @Update
    suspend fun updateUser(user: ClientUserEntity)

    // --- Notifications ---
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotificationById(id: Long)
}
