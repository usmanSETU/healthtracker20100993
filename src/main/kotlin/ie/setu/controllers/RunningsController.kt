package ie.setu.controllers

import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.Running
import ie.setu.domain.repository.RunningDAO
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*
import mu.KotlinLogging

object RunningsController {
    private val runningDAO = RunningDAO()
    private val logger = KotlinLogging.logger(){}
    private val mapper = jacksonObjectMapper().registerModule(JodaModule())

    @OpenApi(
        summary = "Get all running Activities",
        operationId = "getAllRunningActivities",
        description = "This endpoint returns all the activities of running present in the database",
        tags = ["Running"],
        path = "/api/running",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Running>::class)])]
    )
    fun getAllActivities(ctx: Context) {
        this.logger.info {ctx.sessionAttribute("id")}
        if(ctx.sessionAttribute<Int>("id") != null) {
            ctx.json(this.mapper.writeValueAsString(this.runningDAO.findByUserId(ctx.sessionAttribute<Int>("id")!!.toInt())))
        }else{
            ctx.json(this.mapper.writeValueAsString(this.runningDAO.getAll()))
        }
    }

    @OpenApi(
        summary = "Get running activity by ID",
        operationId = "getRunningActivityByID",
        description = "This endpoint returns single running activity record based on the ID provided",
        tags = ["Running"],
        path = "/api/running/{id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("id",Int::class,"activity id")],
        responses = [OpenApiResponse("200", [OpenApiContent(Running::class)])]
    )
    fun getActivityById(ctx: Context) {
        val activity = this.runningDAO.findById(ctx.pathParam("id").toInt())
        if (activity != null) {
            ctx.json(this.mapper.writeValueAsString(activity))
        }
    }

    @OpenApi(
        summary = "Add new running activity",
        operationId = "addRunningActivity",
        description = "This endpoint adds a new running activity into the system",
        tags = ["Running"],
        path= "/api/running",
        method = HttpMethod.POST,
        responses = [OpenApiResponse("200"), OpenApiResponse("500")]
    )
    fun addActivity(ctx: Context) {
        val activity = this.mapper.readValue<Running>(ctx.body())
        if (activity.userId == 0 && ctx.sessionAttribute<Int>("id") == null ){
            ctx.status(401).result("Unauthorized")
        }else {
            if(activity.userId == 0) {
                activity.userId = ctx.sessionAttribute<Int>("id")!!
            }
            val insertedRow =this.runningDAO.save(activity)
            if (insertedRow != null) {
                ctx.json(this.mapper.writeValueAsString(insertedRow))
            }else{
                ctx.status(500).result("Something went wrong")
            }
        }
    }


    @OpenApi(
        summary = "Deletes running activity",
        operationId = "deleteRunningActivity",
        description = "This endpoints deletes a running activity based on the ID provided",
        tags = ["Running"],
        path = "/api/running/{id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("id",Int::class, "ID of the activity")],
        responses = [OpenApiResponse("200"), OpenApiResponse("500")]
    )
    fun deleteActivity(ctx: Context){
        this.runningDAO.delete(ctx.pathParam("id").toInt())
    }


    @OpenApi(
        summary = "Updates a running activity",
        operationId = "updateRunningActivity",
        description = "This endpoint updates a running activity of the ID provided in path params with the values provided in the body",
        tags = ["Running"],
        path = "/api/running/{id}",
        method = HttpMethod.PATCH,
        requestBody = OpenApiRequestBody([OpenApiContent(Running::class)]),
        responses = [OpenApiResponse("200"), OpenApiResponse("500"), OpenApiResponse("404")]
    )
    fun updateActivity(ctx: Context) {
        val activityUpdates = this.mapper.readValue<Running>(ctx.body())
        val updatedActivity =  this.runningDAO.update(
            id = ctx.pathParam("id").toInt(),
             activityUpdates)
        if(updatedActivity != null){
            ctx.json(this.mapper.writeValueAsString(updatedActivity))
        }else{
            ctx.status(500).result("Internal Server Error")
        }
    }

    @OpenApi(
        summary = "Get running activities by user",
        operationId = "getRunningAcitivitiesByUser",
        description = "This endpoint returns all the running activities of the user provided",
        tags = ["Running"],
        path="/api/running/{userid}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("userId",Int::class,"Id of the user to get activities for")]
    )
    fun getActivitiesByUserID(ctx: Context): ArrayList<Running> {
        return this.runningDAO.findByUserId(ctx.pathParam("userId").toInt())
    }
}