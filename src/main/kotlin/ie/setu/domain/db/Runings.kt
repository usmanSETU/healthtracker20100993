package ie.setu.domain.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.joda.time.DateTime

object Runings: Table("running") {
    val id = integer("id").autoIncrement().uniqueIndex().primaryKey()
    val distance = integer("distance")
    val calories = integer("calories")
    val userId = integer("userId").references(Users.id,ReferenceOption.CASCADE,ReferenceOption.CASCADE)
    val createdAt = datetime("createdAt").clientDefault { DateTime.now() }
}