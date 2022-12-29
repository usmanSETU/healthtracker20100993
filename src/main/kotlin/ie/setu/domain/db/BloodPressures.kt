package ie.setu.domain.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime


object BloodPressures: Table("blood_pressure") {
    val id = integer("id").autoIncrement().primaryKey()
    val pulse = integer("pulse")
    val systolic = integer("systolic")
    val diastolic = integer("diastolic")
    val userId = integer("userId").references(Users.id,ReferenceOption.CASCADE,ReferenceOption.CASCADE)
    val createdAt = datetime("createdAt").clientDefault { DateTime.now() }
}