package ie.setu.domain.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Activities:Table("activities") {
    val id = integer("id").autoIncrement().primaryKey()
    val userId = integer("userid").references(Users.id,ReferenceOption.CASCADE,ReferenceOption.CASCADE)
}