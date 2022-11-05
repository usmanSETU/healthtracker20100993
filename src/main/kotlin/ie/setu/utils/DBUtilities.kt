package ie.setu.utils

import ie.setu.domain.Activity
import ie.setu.domain.User
import ie.setu.domain.db.Activities
import ie.setu.domain.db.Users
import org.jetbrains.exposed.sql.ResultRow

fun mapToUser(it: ResultRow) = User(
    id = it[Users.id],
    name = it[Users.name],
    email = it[Users.email]
)

fun mapToActivities(it: ResultRow) = Activity(
    id = it[Activities.id],
    userId = it[Activities.userId],
    activityName = it[Activities.activityName],
    calories = it[Activities.calories]
)