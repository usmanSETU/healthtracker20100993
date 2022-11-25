package ie.setu.controllers

import ie.setu.domain.db.Users
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class AuthController {
    fun loginUser(userEmail: String,userPassword:String){
            val user = Users.select(){
                (Users.email eq userEmail) and (Users.password eq userPassword)
            }
    }
}