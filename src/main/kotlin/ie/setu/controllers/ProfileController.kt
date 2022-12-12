package ie.setu.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.User
import ie.setu.domain.repository.UserDAO
import io.javalin.http.Context
object ProfileController {
    private val userDAO = UserDAO()

    fun getUserProfile(ctx:Context): Any? {
        val userID = ctx.sessionAttribute<Int>("id")
        return if(userID != null)
            userDAO.findById(userID)?.let { ctx.json(it) }
        else
            ctx.status(401).result("Unauthorized")
    }

    fun updateUserProfile(ctx:Context) {
        val userID = ctx.sessionAttribute<Int>("id")
        if(userID === null){
            ctx.status(500).result("Unauthorized")
        }else{
            val mapper = jacksonObjectMapper()
            val userUpdates = mapper.readValue<User>(ctx.body())
            ctx.json(userDAO.update(userID,userUpdates)!!)
        }
    }
}