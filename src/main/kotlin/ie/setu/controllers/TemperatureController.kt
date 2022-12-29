package ie.setu.controllers

import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.Temperature
import ie.setu.domain.repository.TemperatureDAO
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*
import mu.KotlinLogging

object TemperatureController {
    private val temperatureDAO = TemperatureDAO()
    private val logger = KotlinLogging.logger(){}
    private val mapper = jacksonObjectMapper().registerModule(JodaModule())

    @OpenApi(
        summary = "Get all temperature Activities",
        operationId = "getAllTemperatureActivities",
        description = "This endpoint returns all the activities of temperature present in the database",
        tags = ["Temperature"],
        path = "/api/temperature",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Temperature>::class)])]
    )
    fun getAllActivities(ctx: Context) {
        this.logger.info {ctx.sessionAttribute("id")}
        if(ctx.sessionAttribute<Int>("id") != null) {
            ctx.json(this.mapper.writeValueAsString(this.temperatureDAO.findByUserId(ctx.sessionAttribute<Int>("id")!!.toInt())))
        }else{
            ctx.json(this.mapper.writeValueAsString(this.temperatureDAO.findAll()))
        }
    }

    @OpenApi(
        summary = "Get temperature activity by ID",
        operationId = "getTemperatureActivityByID",
        description = "This endpoint returns single temperature activity record based on the ID provided",
        tags = ["Temperature"],
        path = "/api/temperature/{id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("id",Int::class,"activity id")],
        responses = [OpenApiResponse("200", [OpenApiContent(Temperature::class)])]
    )
    fun getActivityById(ctx: Context) {
        val activity = this.temperatureDAO.findById(ctx.pathParam("id").toInt())
        if (activity != null) {
            ctx.json(this.mapper.writeValueAsString(activity))
        }else{
            ctx.status(404).json("{'success':false,'message':'failed to get activity'}")
        }
    }

    @OpenApi(
        summary = "Add new temperature activity",
        operationId = "addTemperatureActivity",
        description = "This endpoint adds a new temperature activity into the system",
        tags = ["Temperature"],
        path= "/api/temperature",
        method = HttpMethod.POST,
        responses = [OpenApiResponse("200"), OpenApiResponse("500")]
    )
    fun addActivity(ctx: Context) {
        try {
            val mapper = jacksonObjectMapper().registerModule(JodaModule())
            val activity = mapper.readValue<Temperature>(ctx.body())
            if (activity.userId == 0 && ctx.sessionAttribute<Int>("id") == null) {
                ctx.status(401).result("Unauthorized")
            } else {
                if (activity.userId == 0) {
                    activity.userId = ctx.sessionAttribute<Int>("id")!!
                }
                val insertedRow = this.temperatureDAO.save(activity)
                if (insertedRow != null) {
                    ctx.json(this.mapper.writeValueAsString(insertedRow))
                } else {
                    ctx.status(500).result("Something went wrong")
                }
            }
        }catch (err:Exception){
            ctx.status(500).json("{'success':false,'message':'failed to create activity'}")
        }
    }


    @OpenApi(
        summary = "Deletes temperature activity",
        operationId = "deleteTemperatureActivity",
        description = "This endpoints deletes a temperature activity based on the ID provided",
        tags = ["Temperature"],
        path = "/api/temperature/{id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("id",Int::class, "ID of the activity")],
        responses = [OpenApiResponse("200"), OpenApiResponse("500")]
    )
    fun deleteActivity(ctx: Context){
        this.temperatureDAO.delete(ctx.pathParam("id").toInt())
    }


    @OpenApi(
        summary = "Updates a temperature activity",
        operationId = "updateTemperatureActivity",
        description = "This endpoint updates a temperature activity of the ID provided in path params with the values provided in the body",
        tags = ["Temperature"],
        path = "/api/temperature/{id}",
        method = HttpMethod.PATCH,
        requestBody = OpenApiRequestBody([OpenApiContent(Temperature::class)]),
        responses = [OpenApiResponse("200"), OpenApiResponse("500"), OpenApiResponse("404")]
    )
    fun updateActivity(ctx: Context) {
        try {
            val activityUpdates = this.mapper.readValue<Temperature>(ctx.body())
            val updatedActivity = this.temperatureDAO.update(
                id = ctx.pathParam("id").toInt(),
                activityUpdates
            )
            if (updatedActivity != null) {
                ctx.json(this.mapper.writeValueAsString(updatedActivity))
            } else {
                ctx.status(500).result("Internal Server Error")
            }
        }catch (err:Exception){
            ctx.status(500).json("{'success':false,'message':'failed to update activity'}")
        }
    }

    @OpenApi(
        summary = "Get temperature activities by user",
        operationId = "getTemperatureAcitivitiesByUser",
        description = "This endpoint returns all the temperature activities of the user provided",
        tags = ["Temperature"],
        path="/api/temperature/{userid}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("userId",Int::class,"Id of the user to get activities for")]
    )
    fun getActivitiesByUserID(ctx: Context): ArrayList<Temperature> {
        return this.temperatureDAO.findByUserId(ctx.pathParam("userId").toInt())
    }
}