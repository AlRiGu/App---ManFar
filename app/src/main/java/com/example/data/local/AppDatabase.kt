package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.BarbershopDao
import com.example.data.local.entity.AppointmentEntity
import com.example.data.local.entity.AvailabilityBlockEntity
import com.example.data.local.entity.ClientUserEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.ServiceEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ServiceEntity::class,
        AppointmentEntity::class,
        AvailabilityBlockEntity::class,
        ClientUserEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun barbershopDao(): BarbershopDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return getDatabase(context, CoroutineScope(Dispatchers.IO))
        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "manfar_barbershop.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.barbershopDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: BarbershopDao) {
            // Seed Services
            val initialServices = listOf(
                ServiceEntity(
                    id = 1,
                    name = "Corte Clásico & Peinado",
                    category = "Cortes",
                    price = 18.00,
                    durationMinutes = 35,
                    description = "Corte tradicional a tijera o máquina, lavado con champú tonificante y peinado con cera mate.",
                    iconName = "content_cut",
                    isActive = true
                ),
                ServiceEntity(
                    id = 2,
                    name = "Degradado Skin Fade Pro",
                    category = "Cortes",
                    price = 22.00,
                    durationMinutes = 45,
                    description = "Degradado afeitado a navaja al cero absoluto, pulido con shaver y texturizado superior.",
                    iconName = "brush",
                    isActive = true
                ),
                ServiceEntity(
                    id = 3,
                    name = "Perfilado de Barba & Toalla Caliente",
                    category = "Barba",
                    price = 15.00,
                    durationMinutes = 30,
                    description = "Ritual tradicional con toalla caliente infusionada en eucalipto, recorte, perfilado a navaja y aceite hidratante.",
                    iconName = "face",
                    isActive = true
                ),
                ServiceEntity(
                    id = 4,
                    name = "Afeitado Clásico a Navaja",
                    category = "Barba",
                    price = 16.00,
                    durationMinutes = 30,
                    description = "Espuma caliente con brocha de tejón, doble pasada a navaja clásica y loción aftershave artesanal.",
                    iconName = "spa",
                    isActive = true
                ),
                ServiceEntity(
                    id = 5,
                    name = "Combo Ejecutivo VIP (Corte + Barba)",
                    category = "Combos",
                    price = 32.00,
                    durationMinutes = 65,
                    description = "Corte de cabello completo personalizado + ritual completo de barba con toalla caliente y bebida cortesía.",
                    iconName = "star",
                    isActive = true
                ),
                ServiceEntity(
                    id = 6,
                    name = "Corte Infantil (Hasta 12 años)",
                    category = "Cortes",
                    price = 14.00,
                    durationMinutes = 30,
                    description = "Corte moderno adaptado para los más jóvenes con acabado suave y diseño opcional.",
                    iconName = "child_care",
                    isActive = true
                ),
                ServiceEntity(
                    id = 7,
                    name = "Tratamiento Capilar & Masaje Craneal",
                    category = "Tratamientos",
                    price = 20.00,
                    durationMinutes = 35,
                    description = "Exfoliación suave de cuero cabelludo, ampolla fortalecedora anticaída y masaje relajante.",
                    iconName = "health_and_safety",
                    isActive = true
                ),
                ServiceEntity(
                    id = 8,
                    name = "Color & Camuflaje de Canas",
                    category = "Tratamientos",
                    price = 28.00,
                    durationMinutes = 45,
                    description = "Tinte matizador sutil y natural para barba o cabello, sin efecto raíz artificial.",
                    iconName = "palette",
                    isActive = true
                )
            )
            dao.insertServices(initialServices)

            // Seed Users
            val initialUsers = listOf(
                ClientUserEntity(
                    id = 1,
                    name = "Carlos Mendoza",
                    email = "carlos.mendoza@gmail.com",
                    phone = "+34 612 345 678",
                    role = "CLIENT",
                    preferredBarber = "Mateo Silva",
                    avatarInitials = "CM",
                    notes = "Prefiere fade medio sin navaja en la coronilla.",
                    createdAt = System.currentTimeMillis() - 86400000L * 30
                ),
                ClientUserEntity(
                    id = 2,
                    name = "Alejandro Vega",
                    email = "alejandro.vega@hotmail.com",
                    phone = "+34 622 987 654",
                    role = "CLIENT",
                    preferredBarber = "Diego Rossi",
                    avatarInitials = "AV",
                    notes = "Cliente habitual semanal para barba.",
                    createdAt = System.currentTimeMillis() - 86400000L * 45
                ),
                ClientUserEntity(
                    id = 3,
                    name = "Javier Navarro",
                    email = "j.navarro@outlook.com",
                    phone = "+34 633 456 789",
                    role = "CLIENT",
                    preferredBarber = "Carlos 'Fade' Gómez",
                    avatarInitials = "JN",
                    notes = "Gusta de toalla caliente extra.",
                    createdAt = System.currentTimeMillis() - 86400000L * 15
                ),
                ClientUserEntity(
                    id = 4,
                    name = "Lucas Ferreira",
                    email = "lucas.ferreira@gmail.com",
                    phone = "+34 644 112 233",
                    role = "CLIENT",
                    preferredBarber = "Mateo Silva",
                    avatarInitials = "LF",
                    notes = "Corte clásico tijera.",
                    createdAt = System.currentTimeMillis() - 86400000L * 60
                ),
                ClientUserEntity(
                    id = 5,
                    name = "Admin ManFar",
                    email = "admin@manfarbarbershop.com",
                    phone = "+34 910 000 111",
                    role = "ADMIN",
                    preferredBarber = "Todos",
                    avatarInitials = "MF",
                    notes = "Gerente del negocio.",
                    createdAt = System.currentTimeMillis() - 86400000L * 100
                )
            )
            dao.insertUsers(initialUsers)

            // Seed Appointments (Upcoming & Past)
            val today = java.time.LocalDate.now().toString()
            val tomorrow = java.time.LocalDate.now().plusDays(1).toString()
            val yesterday = java.time.LocalDate.now().minusDays(1).toString()
            val twoDaysAgo = java.time.LocalDate.now().minusDays(2).toString()
            val threeDaysAgo = java.time.LocalDate.now().minusDays(3).toString()

            val initialAppointments = listOf(
                // Today's appointments
                AppointmentEntity(
                    id = 1,
                    clientName = "Carlos Mendoza",
                    clientEmail = "carlos.mendoza@gmail.com",
                    clientPhone = "+34 612 345 678",
                    barberName = "Mateo Silva (Master Barber)",
                    serviceId = 5,
                    serviceName = "Combo Ejecutivo VIP (Corte + Barba)",
                    servicePrice = 32.00,
                    date = today,
                    timeSlot = "10:30",
                    status = "CONFIRMED",
                    notes = "Corte degradado bajo y barba cuadrada.",
                    createdAt = System.currentTimeMillis() - 86400000L * 2
                ),
                AppointmentEntity(
                    id = 2,
                    clientName = "Alejandro Vega",
                    clientEmail = "alejandro.vega@hotmail.com",
                    clientPhone = "+34 622 987 654",
                    barberName = "Diego Rossi (Especialista en Barba)",
                    serviceId = 3,
                    serviceName = "Perfilado de Barba & Toalla Caliente",
                    servicePrice = 15.00,
                    date = today,
                    timeSlot = "12:00",
                    status = "CONFIRMED",
                    notes = "Aceite de cedro.",
                    createdAt = System.currentTimeMillis() - 86400000L
                ),
                AppointmentEntity(
                    id = 3,
                    clientName = "Javier Navarro",
                    clientEmail = "j.navarro@outlook.com",
                    clientPhone = "+34 633 456 789",
                    barberName = "Carlos 'Fade' Gómez",
                    serviceId = 2,
                    serviceName = "Degradado Skin Fade Pro",
                    servicePrice = 22.00,
                    date = today,
                    timeSlot = "16:00",
                    status = "PENDING",
                    notes = "Primera vez en ManFar.",
                    createdAt = System.currentTimeMillis() - 3600000L * 5
                ),
                AppointmentEntity(
                    id = 4,
                    clientName = "Lucas Ferreira",
                    clientEmail = "lucas.ferreira@gmail.com",
                    clientPhone = "+34 644 112 233",
                    barberName = "Mateo Silva (Master Barber)",
                    serviceId = 1,
                    serviceName = "Corte Clásico & Peinado",
                    servicePrice = 18.00,
                    date = today,
                    timeSlot = "17:30",
                    status = "CONFIRMED",
                    notes = "Peinado hacia atrás.",
                    createdAt = System.currentTimeMillis() - 3600000L * 8
                ),
                // Tomorrow
                AppointmentEntity(
                    id = 5,
                    clientName = "Carlos Mendoza",
                    clientEmail = "carlos.mendoza@gmail.com",
                    clientPhone = "+34 612 345 678",
                    barberName = "Mateo Silva (Master Barber)",
                    serviceId = 7,
                    serviceName = "Tratamiento Capilar & Masaje Craneal",
                    servicePrice = 20.00,
                    date = tomorrow,
                    timeSlot = "11:00",
                    status = "CONFIRMED",
                    notes = "Sesión de spa capilar.",
                    createdAt = System.currentTimeMillis()
                ),
                // Past appointments for stats
                AppointmentEntity(
                    id = 6,
                    clientName = "Alejandro Vega",
                    clientEmail = "alejandro.vega@hotmail.com",
                    clientPhone = "+34 622 987 654",
                    barberName = "Diego Rossi (Especialista en Barba)",
                    serviceId = 5,
                    serviceName = "Combo Ejecutivo VIP (Corte + Barba)",
                    servicePrice = 32.00,
                    date = yesterday,
                    timeSlot = "15:00",
                    status = "COMPLETED",
                    notes = "Servicio completado con éxito.",
                    createdAt = System.currentTimeMillis() - 86400000L * 3
                ),
                AppointmentEntity(
                    id = 7,
                    clientName = "Lucas Ferreira",
                    clientEmail = "lucas.ferreira@gmail.com",
                    clientPhone = "+34 644 112 233",
                    barberName = "Carlos 'Fade' Gómez",
                    serviceId = 2,
                    serviceName = "Degradado Skin Fade Pro",
                    servicePrice = 22.00,
                    date = yesterday,
                    timeSlot = "11:30",
                    status = "COMPLETED",
                    notes = "Completado.",
                    createdAt = System.currentTimeMillis() - 86400000L * 4
                ),
                AppointmentEntity(
                    id = 8,
                    clientName = "Javier Navarro",
                    clientEmail = "j.navarro@outlook.com",
                    clientPhone = "+34 633 456 789",
                    barberName = "Mateo Silva (Master Barber)",
                    serviceId = 1,
                    serviceName = "Corte Clásico & Peinado",
                    servicePrice = 18.00,
                    date = twoDaysAgo,
                    timeSlot = "10:00",
                    status = "COMPLETED",
                    notes = "",
                    createdAt = System.currentTimeMillis() - 86400000L * 5
                ),
                AppointmentEntity(
                    id = 9,
                    clientName = "Carlos Mendoza",
                    clientEmail = "carlos.mendoza@gmail.com",
                    clientPhone = "+34 612 345 678",
                    barberName = "Diego Rossi (Especialista en Barba)",
                    serviceId = 4,
                    serviceName = "Afeitado Clásico a Navaja",
                    servicePrice = 16.00,
                    date = threeDaysAgo,
                    timeSlot = "18:00",
                    status = "COMPLETED",
                    notes = "",
                    createdAt = System.currentTimeMillis() - 86400000L * 6
                )
            )
            dao.insertAppointments(initialAppointments)

            // Seed Availability Blocks
            val initialBlocks = listOf(
                AvailabilityBlockEntity(
                    id = 1,
                    blockType = "TIME_RANGE",
                    date = today,
                    startTime = "14:00",
                    endTime = "15:00",
                    reason = "Descanso & Almuerzo del Equipo"
                ),
                AvailabilityBlockEntity(
                    id = 2,
                    blockType = "TIME_RANGE",
                    date = tomorrow,
                    startTime = "14:00",
                    endTime = "15:00",
                    reason = "Almuerzo del Equipo"
                )
            )
            dao.insertBlocks(initialBlocks)

            // Seed Notifications
            val initialNotifications = listOf(
                NotificationEntity(
                    id = 1,
                    title = "¡Cita Confirmada! ✂️",
                    message = "Tu cita para 'Combo Ejecutivo VIP' hoy a las 10:30 está confirmada con Mateo Silva.",
                    timestamp = System.currentTimeMillis() - 3600000L * 2,
                    isRead = false,
                    type = "CONFIRMATION"
                ),
                NotificationEntity(
                    id = 2,
                    title = "Recordatorio de Asistencia 🔔",
                    message = "Recuerda llegar 5 minutos antes a ManFar Barbershop. ¡Te esperamos!",
                    timestamp = System.currentTimeMillis() - 3600000L * 1,
                    isRead = false,
                    type = "REMINDER"
                ),
                NotificationEntity(
                    id = 3,
                    title = "Nueva Reserva Recibida 📅",
                    message = "Javier Navarro ha agendado 'Degradado Skin Fade Pro' para hoy a las 16:00.",
                    timestamp = System.currentTimeMillis() - 3600000L * 4,
                    isRead = true,
                    type = "BOOKING"
                )
            )
            initialNotifications.forEach { dao.insertNotification(it) }
        }
    }
}
