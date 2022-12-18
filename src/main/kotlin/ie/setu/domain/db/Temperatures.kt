package ie.setu.domain.db

import ie.setu.domain.db.BloodPressures.clientDefault
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime

object Temperatures: Table("temperatures") {
    val id = integer("id").autoIncrement().primaryKey().uniqueIndex()
    val temperature = float("temperature")
    val userId = integer("userId").references(Users.id,ReferenceOption.CASCADE,ReferenceOption.CASCADE)
    val createdAt = datetime("createdAt").clientDefault { DateTime.now() }

}