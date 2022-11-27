package ie.setu.controllers

import ie.setu.domain.User
import ie.setu.domain.repository.UserDAO
import io.javalin.http.Context
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.javalin.plugin.openapi.annotations.*

object UserController {

    private val userDao = UserDAO()


    @OpenApi(
        summary = "Get all users",
        operationId = "getAllUsers",
        tags = ["User"],
        path = "/api/users",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<User>::class)])]
    )

    fun getAllUsers(ctx: Context) {
        ctx.json(userDao.getAll())
    }

    @OpenApi(
        summary = "Get user by ID",
        operationId = "getUserById",
        tags = ["User"],
        path = "/api/users/{user-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(User::class)])]
    )

    fun getUserByUserId(ctx: Context) {
        val user = userDao.findById(ctx.pathParam("user-id").toInt())
        if (user != null) {
            ctx.json(user)
        }
    }

    @OpenApi(
        summary = "Add User",
        operationId = "addUser",
        tags = ["User"],
        path = "/api/users",
        method = HttpMethod.POST,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("200")]
    )

    fun addUser(ctx: Context) {
        val user = userDao.save(
            User(
                name = ctx.formParam("name") as String,
                password = ctx.formParam("password") as String,
                email = ctx.formParam("email") as String,
                id = 1 // patching for making it compatible this would not be used as actual id
            ))
        if (user != null) {
            ctx.sessionAttribute("id",user)
            return ctx.redirect("/home",302)
        }else {
            ctx.status(500).json("{success:false,message:'Failed to signup user'}")
        }
    }

    @OpenApi(
        summary = "Get user by Email",
        operationId = "getUserByEmail",
        tags = ["User"],
        path = "/api/users/email/{email}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("email", Int::class, "The user email")],
        responses  = [OpenApiResponse("200", [OpenApiContent(User::class)])]
    )

    fun getUserByEmail(ctx: Context) {
        val user = userDao.findByEmail(ctx.pathParam("email"))
        if (user != null) {
            ctx.json(user)
        }
    }

    @OpenApi(
        summary = "Delete user by ID",
        operationId = "deleteUserById",
        tags = ["User"],
        path = "/api/users/{user-id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("204")]
    )

    fun deleteUser(ctx: Context){
        userDao.delete(ctx.pathParam("user-id").toInt())
    }

    @OpenApi(
        summary = "Update user by ID",
        operationId = "updateUserById",
        tags = ["User"],
        path = "/api/users/{user-id}",
        method = HttpMethod.PATCH,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("204")]
    )

    fun updateUser(ctx: Context){
        val mapper = jacksonObjectMapper()
        val userUpdates = mapper.readValue<User>(ctx.body())
        userDao.update(
            id = ctx.pathParam("user-id").toInt(),
            user=userUpdates)
    }

    @OpenApi(
        summary = "Authenticate User Credentials",
        operationId = "loginUser",
        tags = ["Users"],
        path = "/api/users/login",
        method = HttpMethod.POST,
        responses = [OpenApiResponse("302"),OpenApiResponse("401")]
    )

    fun authenticateUser(ctx: Context){
        val user = userDao.authenticate(ctx.formParam("email") as String,ctx.formParam("password") as String)
        if(user!=null){
            ctx.sessionAttribute("id",user.id)
            return ctx.redirect("/home",302)
        }else{
            ctx.status(401).result("Invalid credentials")
        }
    }
}
