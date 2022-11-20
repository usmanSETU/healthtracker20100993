package ie.setu.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.Activity
import ie.setu.domain.User
import ie.setu.domain.repository.ActivitiesDAO
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*

object ActivityController {
    private val ActivityDAO = ActivitiesDAO()

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
        ctx.json(ActivityDAO.getAll())
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
            ctx.json(activity)
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
        val mapper = jacksonObjectMapper()
        val activity = mapper.readValue<Activity>(ctx.body())
        ActivityDAO.save(activity)
        ctx.json(activity)
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
    fun updateActivity(ctx: Context){
        val mapper = jacksonObjectMapper()
        val activityUpdates = mapper.readValue<Activity>(ctx.body())
        ActivityDAO.update(
            id = ctx.pathParam("id").toInt(),
            activity = activityUpdates)
    }
}