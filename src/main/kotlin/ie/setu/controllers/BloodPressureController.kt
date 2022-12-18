package ie.setu.controllers

import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.Activity
import ie.setu.domain.BloodPressure
import ie.setu.domain.repository.BloodPressureDAO
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*
import mu.KotlinLogging

object BloodPressureController {
    private val bloodPressureDAO = BloodPressureDAO()
    private val logger = KotlinLogging.logger(){}
    private val mapper = jacksonObjectMapper().registerModule(JodaModule())


    @OpenApi(
        summary = "Get all Blood Pressure Activities",
        operationId = "getAllBloodPressureActivities",
        description = "This endpoint returns all the activities of blood pressure present in the database",
        tags = ["BloodPressure"],
        path = "/api/blood-pressure",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<BloodPressure>::class)])]
    )
    fun getAllActivities(ctx: Context) {
        this.logger.info {ctx.sessionAttribute("id")}
        if(ctx.sessionAttribute<Int>("id") != null) {
            ctx.json(this.mapper.writeValueAsString(this.bloodPressureDAO.findByUserId(ctx.sessionAttribute<Int>("id")!!.toInt())))
        }else{
            ctx.json(this.mapper.writeValueAsString(this.bloodPressureDAO.getAll()))
        }
    }

    @OpenApi(
        summary = "Get blood pressure activity by ID",
        operationId = "getBloodPressureActivityByID",
        description = "This endpoint returns single blood pressure activity record based on the ID provided",
        tags = ["BloodPressure"],
        path = "/api/blood-pressure/{id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("id",Int::class,"activity id")],
        responses = [OpenApiResponse("200", [OpenApiContent(BloodPressure::class)])]
    )
    fun getActivityById(ctx: Context) {
        val activity = this.bloodPressureDAO.findById(ctx.pathParam("id").toInt())
        if (activity != null) {
            ctx.json(this.mapper.writeValueAsString(activity))
        }
    }

    @OpenApi(
        summary = "Add new blood pressure activity",
        operationId = "addBloodPressureActivity",
        description = "This endpoint adds a new blood pressure activity into the system",
        tags = ["BloodPressure"],
        path= "/api/blood-pressure",
        method = HttpMethod.POST,
        responses = [OpenApiResponse("200"),OpenApiResponse("500")]
    )
    fun addActivity(ctx: Context) {
        val activity = this.mapper.readValue<BloodPressure>(ctx.body())
        if (activity.userId == 0 && ctx.sessionAttribute<Int>("id") == null ){
            ctx.status(401).result("Unauthorized")
        }else {
            if(activity.userId == 0) {
                activity.userId = ctx.sessionAttribute<Int>("id")!!
            }
            val insertedRow =this.bloodPressureDAO.save(activity)
            if (insertedRow != null) {
                ctx.json(this.mapper.writeValueAsString(insertedRow))
            }else{
                ctx.status(500).result("Something went wrong")
            }
        }
    }


    @OpenApi(
        summary = "Deletes blood pressure activity",
        operationId = "deleteBloodPressureActivity",
        description = "This endpoints deletes a blood pressure activity based on the ID provided",
        tags = ["BloodPressure"],
        path = "/api/blood-pressure/{id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("id",Int::class, "ID of the activity")],
        responses = [OpenApiResponse("200"),OpenApiResponse("500")]
    )
    fun deleteActivity(ctx: Context){
        this.bloodPressureDAO.delete(ctx.pathParam("id").toInt())
    }


    @OpenApi(
        summary = "Updates a blood pressure activity",
        operationId = "updateBloodPressureActivity",
        description = "This endpoint updates a blood pressure activity of the ID provided in path params with the values provided in the body",
        tags = ["BloodPressure"],
        path = "/api/blood-pressure/{id}",
        method = HttpMethod.PATCH,
        requestBody = OpenApiRequestBody([OpenApiContent(BloodPressure::class)]),
        responses = [OpenApiResponse("200"),OpenApiResponse("500"),OpenApiResponse("404")]
    )
    fun updateActivity(ctx: Context) {
        val activityUpdates = this.mapper.readValue<BloodPressure>(ctx.body())
        val updatedActivity =  this.bloodPressureDAO.update(
            id = ctx.pathParam("id").toInt(),
            bloodPressure = activityUpdates)
        if(updatedActivity != null){
            ctx.json(this.mapper.writeValueAsString(updatedActivity))
        }else{
            ctx.status(500).result("Internal Server Error")
        }
    }

    @OpenApi(
        summary = "Get blood pressure activities by user",
        operationId = "getBloodPressureAcitivitiesByUser",
        description = "This endpoint returns all the blood pressure activities of the user provided",
        tags = ["BloodPressure"],
        path="/api/blood-pressure/{userid}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("userId",Int::class,"Id of the user to get activities for")]
    )
    fun getActivitiesByUserID(ctx: Context): ArrayList<BloodPressure> {
        return this.bloodPressureDAO.findByUserId(ctx.pathParam("userId").toInt())
    }

}