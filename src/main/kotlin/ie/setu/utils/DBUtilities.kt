package ie.setu.utils

import ie.setu.domain.Activity
import ie.setu.domain.User
import ie.setu.domain.BloodPressure
import ie.setu.domain.db.Activities
import ie.setu.domain.db.BloodPressures
import ie.setu.domain.db.Users
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
    userId = it[Activities.userId]
)

fun mapToBloodPressure(it:ResultRow): BloodPressure = BloodPressure(
    id = it[BloodPressures.id],
    systolic = it[BloodPressures.systolic],
    diastolic = it[BloodPressures.diastolic],
    pulse = it[BloodPressures.pulse],
    userId = it[BloodPressures.userId]
)