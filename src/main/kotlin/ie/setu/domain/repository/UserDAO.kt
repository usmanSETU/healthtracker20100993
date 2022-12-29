package ie.setu.domain.repository

import ie.setu.domain.User
import ie.setu.domain.db.Users
import ie.setu.utils.mapToUser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserDAO {

    fun getAll(): ArrayList<User> {
        val userList: ArrayList<User> = arrayListOf()
        transaction {
            Users.selectAll().map {
                userList.add(mapToUser(it)) }
        }
        return userList
    }

    fun findById(id: Int): User?{
        return transaction {
            Users.select() {
                Users.id eq id}
                .map{mapToUser(it)}
                .firstOrNull()
        }
    }

    fun save(user: User):User?{
        val insertedId = transaction {
            Users.insert{
                it[name] = user.name
                it[email] = user.email
                it[password] = user.password
            }
        } get Users.id

        return this.findById(insertedId)
    }

    fun findByEmail(email: String) :User?{
        return transaction {
            Users.select() {
                Users.email eq email}
                .map{mapToUser(it)}
                .firstOrNull()
        }
    }

    fun delete(id: Int) {
        return transaction {
            Users.deleteWhere {
                Users.id eq id
            }
        }
    }

    fun update(id: Int, user: User): User? {
        transaction {
            Users.update({Users.id eq id}){
                it[email] = user.email
                it[name] = user.name
                it[password] = user.password
            }
        }
        return this.findById(id)
    }

    fun authenticate(email:String,password:String):User?{
        return transaction {
            Users.select() {
                (Users.email eq email) and (Users.password eq password)
            }.map {
                mapToUser(it)
            }.firstOrNull()
        }
    }
}