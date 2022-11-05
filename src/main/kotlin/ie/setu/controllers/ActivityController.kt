package ie.setu.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.Activity
import ie.setu.domain.repository.ActivitiesDAO
import io.javalin.http.Context

object ActivityController {
    private val ActivityDAO = ActivitiesDAO()

    fun getAllActivities(ctx: Context) {
        ctx.json(ActivityDAO.getAll())
    }

    fun getActivityById(ctx: Context) {
        val activity = ActivityDAO.findById(ctx.pathParam("id").toInt())
        if (activity != null) {
            ctx.json(activity)
        }
    }

    fun addActivity(ctx: Context) {
        val mapper = jacksonObjectMapper()
        val activity = mapper.readValue<Activity>(ctx.body())
        ActivityDAO.save(activity)
        ctx.json(activity)
    }



    fun deleteActivity(ctx: Context){
        ActivityDAO.delete(ctx.pathParam("id").toInt())
    }

    fun updateActivity(ctx: Context){
        val mapper = jacksonObjectMapper()
        val activityUpdates = mapper.readValue<Activity>(ctx.body())
        ActivityDAO.update(
            id = ctx.pathParam("id").toInt(),
            activity = activityUpdates)
    }
}