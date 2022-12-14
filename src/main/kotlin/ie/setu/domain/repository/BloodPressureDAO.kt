package ie.setu.domain.repository

import ie.setu.domain.BloodPressure
import ie.setu.domain.db.BloodPressures
import ie.setu.utils.mapToBloodPressure
import mu.KotlinLogging
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class BloodPressureDAO {
    private val logger = KotlinLogging.logger(){}

    fun getAll():ArrayList<BloodPressure>{
        val bloodPressureList: ArrayList<BloodPressure> = arrayListOf()
        transaction {
            BloodPressures.selectAll().map {
                bloodPressureList.add(mapToBloodPressure(it)) }
        }
        return bloodPressureList
    }

    fun findById(id: Int): BloodPressure? {
        return transaction {
            BloodPressures.select {
                BloodPressures.id eq id}
                .map{ mapToBloodPressure(it) }
                .firstOrNull()
        }
    }

    fun save(bloodPressure: BloodPressure): BloodPressure? {
        val insertedId = transaction {
            BloodPressures.insert {
                it[pulse] = bloodPressure.pulse
                it[systolic] = bloodPressure.systolic
                it[diastolic] = bloodPressure.diastolic
                it[userId] = bloodPressure.userId
                it[createdAt] = bloodPressure.createdAt
            }
        } get BloodPressures.id
        logger.info { "Inserted ID $insertedId" }

        return this.findById(insertedId)
    }

    fun delete(id: Int) {
        transaction {
            BloodPressures.deleteWhere { BloodPressures.id eq id }
        }
    }

    fun update(id: Int, bloodPressure: BloodPressure): BloodPressure? {
        transaction {
            BloodPressures.update({ BloodPressures.id.eq(id) }) {
                it[pulse] = bloodPressure.pulse
                it[systolic] = bloodPressure.systolic
                it[diastolic] = bloodPressure.diastolic
                it[userId] = bloodPressure.userId
            }
        }
        return this.findById(id)
    }

    fun findByUserId(userId:Int): ArrayList<BloodPressure> {
        val bloodPressureList: ArrayList<BloodPressure> = arrayListOf()
        transaction {
            BloodPressures.select(){
                BloodPressures.userId eq userId
            }.map {
                bloodPressureList.add(mapToBloodPressure(it))
            }
        }
        return bloodPressureList
    }

}