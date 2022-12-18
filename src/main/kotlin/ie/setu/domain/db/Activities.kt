package ie.setu.domain.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime

object Activities:Table("activities") {
    val id = integer("id").autoIncrement().primaryKey()
    val calories = varchar("calories",255)
    val activityName = varchar("activityName",255)
    val userId = integer("userId").references(Users.id, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE)
    val createdAt = datetime("createdAt").default(DateTime.now())
}