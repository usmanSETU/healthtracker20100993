package ie.setu.domain.repository

import ie.setu.domain.Running
import ie.setu.domain.Temperature
import ie.setu.domain.db.Runings
import mu.KotlinLogging
import org.jetbrains.exposed.sql.transactions.transaction
import ie.setu.domain.db.Temperatures
import ie.setu.utils.mapToRuning
import ie.setu.utils.mapToTemperature
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class TemperatureDAO {

    val logger = KotlinLogging.logger(){}

    fun findAll():ArrayList<Temperature>{
        val temperatureList: ArrayList<Temperature> = arrayListOf()
        transaction {
            Temperatures.selectAll().map {
                temperatureList.add(mapToTemperature(it)) }
        }
        return temperatureList
    }

    fun findById(id: Int): Temperature? {
        return transaction {
            Temperatures.select {
                Temperatures.id eq id}
                .map{ mapToTemperature(it) }
                .firstOrNull()
        }
    }

    fun save(temperatureData: Temperature): Temperature? {
        val insertedId = transaction {
            Temperatures.insert {
                it[id] = temperatureData.id
                it[temperature] = temperatureData.temperature
                it[userId] = temperatureData.userId
                it[createdAt] = temperatureData.createdAt
            }
        } get Temperatures.id
        logger.info { "Inserted ID $insertedId" }

        return this.findById(insertedId)
    }

    fun delete(id: Int) {
        transaction {
            Temperatures.deleteWhere { Temperatures.id eq id }
        }
    }

    fun update(id: Int, updateData: Temperature): Temperature? {
        transaction {
            Temperatures.update({ Temperatures.id.eq(id) }) {
                it[temperature] = updateData.temperature
                it[userId] = updateData.userId
            }
        }
        return this.findById(id)
    }

    fun findByUserId(userId:Int): ArrayList<Temperature> {
        val temperatureList: ArrayList<Temperature> = arrayListOf()
        transaction {
            Temperatures.select(){
                Temperatures.userId eq userId
            }.map {
                temperatureList.add(mapToTemperature(it))
            }
        }
        return temperatureList
    }
}
