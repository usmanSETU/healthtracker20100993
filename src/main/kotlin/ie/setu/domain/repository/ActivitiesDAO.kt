package ie.setu.domain.repository

import ie.setu.domain.Activity
import ie.setu.domain.db.Activities
import ie.setu.utils.mapToActivities
import mu.KotlinLogging
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ActivitiesDAO {

    private val logger = KotlinLogging.logger(){}

    fun getAll():ArrayList<Activity>{
        val activitiesList: ArrayList<Activity> = arrayListOf()
        transaction {
            Activities.selectAll().map {
                activitiesList.add(mapToActivities(it)) }
        }
        return activitiesList
    }

    fun findById(id: Int): Activity?{
        return transaction {
            Activities.select {
                Activities.id eq id}
                .map{ mapToActivities(it) }
                .firstOrNull()
        }
    }

    fun save(activity: Activity): Activity? {
        val insertedId = transaction {
            Activities.insert {
                it[calories] = activity.calories
                it[activityName] = activity.activityName
                it[userId] = activity.userId
            }
        } get Activities.id
        logger.info { "Inserted ID $insertedId" }

        return this.findById(insertedId)
    }

    fun delete(id: Int) {
        transaction {
            Activities.deleteWhere { Activities.id eq id }
        }
    }

    fun update(id: Int, activity: Activity):Activity? {
        transaction {
            Activities.update({ Activities.id.eq(id) }) {
                it[calories] = activity.calories
                it[activityName] = activity.activityName
            }
        }
        return this.findById(id)
    }

    fun findByUserId(userId:Int): ArrayList<Activity> {
        val activitiesList: ArrayList<Activity> = arrayListOf()
        transaction {
            Activities.select(){
                Activities.userId eq userId
            }.map {
                activitiesList.add(mapToActivities(it))
            }
        }
        return activitiesList
    }

}