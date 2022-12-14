package ie.setu.utils

import ie.setu.domain.*
import ie.setu.domain.db.*
import org.jetbrains.exposed.sql.ResultRow

fun mapToUser(it: ResultRow) = User(
    id = it[Users.id],
    name = it[Users.name],
    email = it[Users.email],
    password = it[Users.password]
)

fun mapToActivities(it: ResultRow) = Activity(
    id = it[Activities.id],
    activityName = it[Activities.activityName],
    calories = it[Activities.calories],
    userId = it[Activities.userId],
    createdAt = it[Activities.createdAt]
)

fun mapToBloodPressure(it:ResultRow) = BloodPressure(
    id = it[BloodPressures.id],
    systolic = it[BloodPressures.systolic],
    diastolic = it[BloodPressures.diastolic],
    pulse = it[BloodPressures.pulse],
    userId = it[BloodPressures.userId],
    createdAt = it[BloodPressures.createdAt]
)

fun mapToRuning(it: ResultRow)= Running(
    id = it[Runings.id],
    userId = it[Runings.userId],
    calories = it[Runings.calories],
    distance = it[Runings.distance],
    createdAt = it[Runings.createdAt]
)

fun mapToTemperature(it:ResultRow) = Temperature(
    id = it[Temperatures.id],
    temperature = it[Temperatures.temperature],
    userId = it[Temperatures.userId],
    createdAt = it[Temperatures.createdAt]
)