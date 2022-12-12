package ie.setu.domain.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Runings: Table("running") {
    val id = integer("id").uniqueIndex().primaryKey()
    val distance = integer("distance")
    val calories = integer("calories")
    val userId = integer("userId").references(Users.id,ReferenceOption.CASCADE,ReferenceOption.CASCADE)
}