package ie.setu.controllers

import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.Activity
import ie.setu.domain.repository.ActivitiesDAO
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*
import mu.KotlinLogging

object ActivityController {
    private val ActivityDAO = ActivitiesDAO()
    private val logger = KotlinLogging.logger(){}
    private val mapper = jacksonObjectMapper().registerModule(JodaModule())

    @OpenApi(
        summary = "Get all activities",
        operationId = "getAllActivities",
        description = "This endpoint returns all the activities present in the database",
        tags = ["Activities"],
        path = "/api/activities",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Activity>::class)])]
    )
    fun getAllActivities(ctx: Context) {
        logger.info {ctx.sessionAttribute("id")}
        if(ctx.sessionAttribute<Int>("id") != null) {
            val userActivities = ActivityDAO.findByUserId(ctx.sessionAttribute<Int>("id")!!.toInt());
            ctx.json(this.mapper.writeValueAsString(userActivities))
        }else{
            ctx.json(this.mapper.writeValueAsString(ActivityDAO.getAll()))
        }
    }

    @OpenApi(
        summary = "Get activity by ID",
        operationId = "getActivityByID",
        description = "This endpoint returns single activity record based on the ID provided",
        tags = ["Activities"],
        path = "/api/activities/{id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("id",Int::class,"activity id")],
        responses = [OpenApiResponse("200", [OpenApiContent(Activity::class)])]
    )
    fun getActivityById(ctx: Context) {
        val activity = ActivityDAO.findById(ctx.pathParam("id").toInt())
        if (activity != null) {
            ctx.json(this.mapper.writeValueAsString(activity))
        }
    }

    @OpenApi(
        summary = "Add new activity",
        operationId = "addActivity",
        description = "This endpoint adds a new activity into the system",
        tags = ["Activities"],
        path= "/api/activities",
        method = HttpMethod.POST,
        responses = [OpenApiResponse("200"),OpenApiResponse("500")]
    )
    fun addActivity(ctx: Context) {
        val activity = this.mapper.readValue<Activity>(ctx.body())
        if (activity.userId == 0 && ctx.sessionAttribute<Int>("id") == null ){
             ctx.status(401).result("Unauthorized")
        }else {
            if(activity.userId == 0) {
                activity.userId = ctx.sessionAttribute<Int>("id")!!
            }
            val insertedRow = ActivityDAO.save(activity)
            if (insertedRow != null) {
                ctx.json(this.mapper.writeValueAsString(insertedRow))
            }else{
                ctx.status(500).result("Something went wrong")
            }
        }
    }



    @OpenApi(
        summary = "Deletes activity",
        operationId = "deleteActivity",
        description = "This endpoints deletes an activity based on the ID provided",
        tags = ["Activities"],
        path = "/api/activities/{id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("id",Int::class, "ID of the activity")],
        responses = [OpenApiResponse("200"),OpenApiResponse("500")]
    )
    fun deleteActivity(ctx: Context){
        ActivityDAO.delete(ctx.pathParam("id").toInt())
    }

    @OpenApi(
        summary = "Updates an Activity",
        operationId = "updateActivity",
        description = "This endpoint updates an activity of the ID provided in path params with the values provided in the body",
        tags = ["Activities"],
        path = "/api/activities/{id}",
        method = HttpMethod.PATCH,
        requestBody = OpenApiRequestBody([OpenApiContent(Activity::class)]),
        responses = [OpenApiResponse("200"),OpenApiResponse("500"),OpenApiResponse("404")]
    )
    fun updateActivity(ctx: Context) {
        val activityUpdates = this.mapper.readValue<Activity>(ctx.body())
        val updatedActivity =  ActivityDAO.update(
            id = ctx.pathParam("id").toInt(),
            activity = activityUpdates)
        if(updatedActivity != null){
            ctx.json(this.mapper.writeValueAsString(updatedActivity))
        }else{
            ctx.status(500).result("Internal Server Error")
        }
    }

    @OpenApi(
        summary = "Get Activities by user",
        operationId = "getAcitivitiesByUser",
        description = "This endpoint returns all the activities of the user provided",
        tags = ["Activities"],
        path="/api/activities/{userid}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("userId",Int::class,"Id of the user to get activities for")]
    )
    fun getActivitiesByUserID(ctx: Context): ArrayList<Activity> {
        return ActivityDAO.findByUserId(ctx.pathParam("userId").toInt())
    }
}