package ie.setu.config

import ie.setu.controllers.ActivityController
import ie.setu.controllers.UserController
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
            it.defaultContentType = "application/json"}.apply {
            _conf.enableWebjars()

            exception(Exception::class.java) { e, _ -> e.printStackTrace() }
            error(404) { ctx -> ctx.json("404 - Not Found") }
            with(JavalinVue){
                vueVersion{
                    it.vue3("app")
                }
            }
        }.start(getHerokuAssignedPort())

        registerRoutes(app)
        return app
    }


    fun getConfiguredOpenApiPlugin() = OpenApiPlugin(
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

    private fun getHerokuAssignedPort(): Int {
        val herokuPort = System.getenv("PORT")
        return if (herokuPort != null) {
            Integer.parseInt(herokuPort)
        } else 7000
    }

    private fun registerRoutes(app: Javalin) {
        app.routes {
            path("/home") {
                get(VueComponent("hello-world"))
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