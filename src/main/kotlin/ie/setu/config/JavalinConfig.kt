package ie.setu.config

import ie.setu.controllers.ActivityController
import ie.setu.controllers.UserController
import ie.setu.controllers.Main
import ie.setu.domain.Activity
import ie.setu.domain.db.Activities
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
class JavalinConfig {

    fun startJavalinService(): Javalin {

        val app = Javalin.create().apply {
            exception(Exception::class.java) { e, _ -> e.printStackTrace() }
            error(404) { ctx -> ctx.json("404 - Not Found") }
        }.start(getHerokuAssignedPort())

        registerRoutes(app)
        return app
    }

    private fun getHerokuAssignedPort(): Int {
        val herokuPort = System.getenv("PORT")
        return if (herokuPort != null) {
            Integer.parseInt(herokuPort)
        } else 7000
    }

    private fun registerRoutes(app: Javalin) {
        app.routes {
            path("/") {
                get(Main::ping)
            }
            path("/api/users") {
                get(UserController::getAllUsers)
                post(UserController::addUser)
                path("{user-id}"){
                    get(UserController::getUserByUserId)
                    delete(UserController::deleteUser)
                    patch(UserController::updateUser)
                }
                path("/email/{email}"){
                    get(UserController::getUserByEmail)
                }
            }
            path("api/activities"){
                get(ActivityController::getAllActivities)
                post(ActivityController::addActivity)
                path("{id}"){
                    patch(ActivityController::updateActivity)
                    delete(ActivityController::deleteActivity)
                    get(ActivityController::getActivityById)
                }

            }
        }
    }
}