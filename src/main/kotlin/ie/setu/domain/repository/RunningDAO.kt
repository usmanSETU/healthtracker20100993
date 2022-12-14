package ie.setu.domain.repository

import ie.setu.domain.Running
import ie.setu.domain.db.Runings
import ie.setu.utils.mapToRuning
import mu.KotlinLogging
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class RunningDAO {
    private val logger = KotlinLogging.logger(){}

    fun getAll():ArrayList<Running>{
        val runningList: ArrayList<Running> = arrayListOf()
        transaction {
            Runings.selectAll().map {
                runningList.add(mapToRuning(it)) }
        }
        return runningList
    }

    fun findById(id: Int): Running? {
        return transaction {
            Runings.select {
                Runings.id eq id}
                .map{ mapToRuning(it) }
                .firstOrNull()
        }
    }

    fun save(running: Running): Running? {
        val insertedId = transaction {
            Runings.insert {
                it[id] = running.id
                it[distance] = running.distance
                it[calories] = running.calories
                it[userId] = running.userId
                it[createdAt] = running.createdAt
            }
        } get Runings.id
        logger.info { "Inserted ID $insertedId" }

        return this.findById(insertedId)
    }

    fun delete(id: Int) {
        transaction {
            Runings.deleteWhere { Runings.id eq id }
        }
    }

    fun update(id: Int, running: Running): Running? {
        transaction {
            Runings.update({ Runings.id.eq(id) }) {
                it[distance] = running.distance
                it[calories] = running.calories
                it[userId] = running.userId
            }
        }
        return this.findById(id)
    }

    fun findByUserId(userId:Int): ArrayList<Running> {
        val runningList: ArrayList<Running> = arrayListOf()
        transaction {
            Runings.select(){
                Runings.userId eq userId
            }.map {
                runningList.add(mapToRuning(it))
            }
        }
        return runningList
    }
}