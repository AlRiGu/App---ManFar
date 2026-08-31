package com.example.data.mapper

import com.example.data.local.entity.AppointmentEntity
import com.example.data.local.entity.AvailabilityBlockEntity
import com.example.data.local.entity.ClientUserEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.ServiceEntity
import com.example.shared.model.AppointmentModel
import com.example.shared.model.AvailabilityBlockModel
import com.example.shared.model.ClientUserModel
import com.example.shared.model.NotificationModel
import com.example.shared.model.ServiceModel

fun ServiceEntity.toModel(): ServiceModel = ServiceModel(
    id = id,
    name = name,
    category = category,
    price = price,
    durationMinutes = durationMinutes,
    description = description,
    iconName = iconName,
    isActive = isActive
)

fun ServiceModel.toEntity(): ServiceEntity = ServiceEntity(
    id = id,
    name = name,
    category = category,
    price = price,
    durationMinutes = durationMinutes,
    description = description,
    iconName = iconName,
    isActive = isActive
)

fun AppointmentEntity.toModel(): AppointmentModel = AppointmentModel(
    id = id,
    clientName = clientName,
    clientEmail = clientEmail,
    clientPhone = clientPhone,
    barberName = barberName,
    serviceId = serviceId,
    serviceName = serviceName,
    servicePrice = servicePrice,
    date = date,
    timeSlot = timeSlot,
    status = status,
    notes = notes,
    createdAt = createdAt
)

fun AppointmentModel.toEntity(): AppointmentEntity = AppointmentEntity(
    id = id,
    clientName = clientName,
    clientEmail = clientEmail,
    clientPhone = clientPhone,
    barberName = barberName,
    serviceId = serviceId,
    serviceName = serviceName,
    servicePrice = servicePrice,
    date = date,
    timeSlot = timeSlot,
    status = status,
    notes = notes,
    createdAt = createdAt
)

fun AvailabilityBlockEntity.toModel(): AvailabilityBlockModel = AvailabilityBlockModel(
    id = id,
    blockType = blockType,
    date = date,
    startTime = startTime,
    endTime = endTime,
    reason = reason
)

fun AvailabilityBlockModel.toEntity(): AvailabilityBlockEntity = AvailabilityBlockEntity(
    id = id,
    blockType = blockType,
    date = date,
    startTime = startTime,
    endTime = endTime,
    reason = reason
)

fun ClientUserEntity.toModel(): ClientUserModel = ClientUserModel(
    id = id,
    name = name,
    email = email,
    phone = phone,
    role = role,
    preferredBarber = preferredBarber,
    avatarInitials = avatarInitials,
    notes = notes,
    createdAt = createdAt
)

fun ClientUserModel.toEntity(): ClientUserEntity = ClientUserEntity(
    id = id,
    name = name,
    email = email,
    phone = phone,
    role = role,
    preferredBarber = preferredBarber,
    avatarInitials = avatarInitials,
    notes = notes,
    createdAt = createdAt
)

fun NotificationEntity.toModel(): NotificationModel = NotificationModel(
    id = id,
    title = title,
    message = message,
    timestamp = timestamp,
    isRead = isRead,
    appointmentId = appointmentId,
    type = type
)

fun NotificationModel.toEntity(): NotificationEntity = NotificationEntity(
    id = id,
    title = title,
    message = message,
    timestamp = timestamp,
    isRead = isRead,
    appointmentId = appointmentId,
    type = type
)

fun com.example.ui.model.Barber.toShared(): com.example.shared.model.Barber =
    com.example.shared.model.Barber(id, name, title, specialty, rating, reviewsCount, 0xFFD4AF37)

fun com.example.shared.model.Barber.toApp(): com.example.ui.model.Barber =
    com.example.ui.model.Barber(id, name, title, specialty, rating, reviewsCount)

fun com.example.ui.model.TimeSlotItem.toShared(): com.example.shared.model.TimeSlotItem =
    com.example.shared.model.TimeSlotItem(time, isAvailable, unavailableReason)

