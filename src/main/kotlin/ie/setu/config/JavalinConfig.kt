package ie.setu.config

import ie.setu.controllers.*
import ie.setu.domain.Running
import ie.setu.utils.sqlSessionHandler
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.javalin.plugin.rendering.vue.JavalinVue
import io.javalin.plugin.rendering.vue.VueComponent
import io.javalin.plugin.openapi.ui.SwaggerOptions
import io.javalin.plugin.openapi.ui.ReDocOptions
import io.swagger.v3.oas.models.info.Info

class JavalinConfig {
    fun startJavalinService(): Javalin {

        val app = Javalin.create{
            it.registerPlugin(getConfiguredOpenApiPlugin())
            it.defaultContentType = "application/json"
            it.sessionHandler { sqlSessionHandler() }
            it.enableWebjars()
        }.apply {
            exception(Exception::class.java) { e, _ -> e.printStackTrace() }
            error(404) { ctx -> ctx.json("404 - Not Found") }
            with(JavalinVue){
                vueVersion{
                    it.vue3("app")
                }
            }
        }.start(getRemoteAssignedPort())

        registerRoutes(app)
        return app
    }


    private fun getConfiguredOpenApiPlugin() = OpenApiPlugin(
        OpenApiOptions(
            Info().apply {
                title("Health Tracker App")
                version("1.0")
                description("Health Tracker API")
            }
        ).apply {
            path("/swagger-docs") // endpoint for OpenAPI json
            swagger(SwaggerOptions("/swagger-ui")) // endpoint for swagger-ui
            reDoc(ReDocOptions("/redoc")) // endpoint for redoc
        }
    )

    private fun getRemoteAssignedPort(): Int {
        val herokuPort = System.getenv("PORT")
        return if (herokuPort != null) {
            Integer.parseInt(herokuPort)
        } else 7000
    }

    private fun registerRoutes(app: Javalin) {
        app.routes {
            path("/"){
                get(VueComponent("Login"))
            }
            path("/signup"){
                get(VueComponent("signup"))
            }
            path("/home") {
                get(VueComponent("hello-world"))
            }
            path("/profile"){
                get(VueComponent("profile"))
            }
            path("/api/users") {
                get(UserController::getAllUsers)
                post(UserController::addUser)
                path("/login"){
                    post(UserController::authenticateUser)
                }
                path("/logout"){
                    get(UserController::logoutUser)
                }
                path("/email/{email}"){
                    get(UserController::getUserByEmail)
                }
                path("{user-id}"){
                    get(UserController::getUserByUserId)
                    delete(UserController::deleteUser)
                    patch(UserController::updateUser)
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
            path("/api/profile"){
                get(ProfileController::getUserProfile)
                patch(ProfileController::updateUserProfile)
            }
            path("api/blood-pressure"){
                get(BloodPressureController::getAllActivities)
                post(BloodPressureController::addActivity)
                path("{id}"){
                    patch(BloodPressureController::updateActivity)
                    delete(BloodPressureController::deleteActivity)
                    get(BloodPressureController::getActivityById)
                }

            }
            path("api/running"){
                get(RunningsController::getAllActivities)
                post(RunningsController::addActivity)
                path("{id}"){
                    patch(RunningsController::updateActivity)
                    delete(RunningsController::deleteActivity)
                    get(RunningsController::getActivityById)
                }

            }
            path("api/temperature"){
                get(TemperatureController::getAllActivities)
                post(TemperatureController::addActivity)
                path("{id}"){
                    patch(TemperatureController::updateActivity)
                    delete(TemperatureController::deleteActivity)
                    get(TemperatureController::getActivityById)
                }

            }
        }
    }
}