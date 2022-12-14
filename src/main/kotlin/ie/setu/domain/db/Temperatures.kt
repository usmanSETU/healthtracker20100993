package ie.setu.domain.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Temperatures: Table("temperatures") {
    val id = integer("id").primaryKey().uniqueIndex()
    val temperature = float("temperature")
    val userId = integer("userId").references(Users.id,ReferenceOption.CASCADE,ReferenceOption.CASCADE)
    val createdAt = datetime("createdAt")

}