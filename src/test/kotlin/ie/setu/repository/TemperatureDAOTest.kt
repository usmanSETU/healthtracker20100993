package ie.setu.repository

import ie.setu.domain.repository.TemperatureDAO
import ie.setu.helpers.temperatureActivities
import ie.setu.domain.Running
import ie.setu.domain.Temperature
import ie.setu.domain.db.Runings
import ie.setu.domain.db.Temperatures
import ie.setu.domain.db.Users
import ie.setu.domain.repository.UserDAO
import ie.setu.helpers.users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TemperatureDAOTest{
    companion object {
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @Nested
    inner class GetActivitiesTest{
        @Test
        fun `getting all activities from a populated table returns all rows`(){
            transaction {
                val activityDTO = populateActivitiesTable();
                Assertions.assertEquals(3,activityDTO.findAll().size)
            }
        }
        @Test
        fun `getting all activities from a populated table returns all records of a specific user`(){
            transaction {
                val activityDTO = populateActivitiesTable();
                Assertions.assertEquals(3,activityDTO.findByUserId(1).size)
            }
        }

        @Test
        fun`getting activities for a non-existing user should return empty list`(){
            transaction {
                val activityDTO = populateActivitiesTable()
                Assertions.assertEquals(0,activityDTO.findByUserId(4).size)
            }
        }

        @Test
        fun`getting activity for a non-existing id should return null`(){
            transaction {
                val activityDTO = populateActivitiesTable()
                Assertions.assertNull(activityDTO.findById(10))
            }
        }
    }

    @Nested
    inner class CreateActivityTest{
        @Test
        fun`create a new activity in the table`(){
            transaction {
                val activityDTO = populateActivitiesTable()
                val newActivity = Temperature(1,"100.0".toFloat(),1, DateTime.now())
                activityDTO.save(newActivity)
                Assertions.assertEquals(4,activityDTO.findAll().size)
            }
        }
    }

    @Nested
    inner class UpdateActivityTest{
        @Test
        fun`updating an activity with valid should save the new data`() {
            transaction {
                val activityDTO = populateActivitiesTable()
                val updatedActivity = Temperature(1,"102.0".toFloat(),1, DateTime.now())
                activityDTO.update(1, updatedActivity)
                activityDTO.findById(1)?.let {
                    Assertions.assertEquals(updatedActivity.temperature, it.temperature)
                }
            }
        }

        @Test
        fun`updating an activity with non-existing id should not make any changes and return null`() {
            transaction {
                val activityDTO = populateActivitiesTable()
                val updatedActivity = Temperature(1,"102.0".toFloat(),1, DateTime.now())
                Assertions.assertNull(activityDTO.update(10, updatedActivity))
            }
        }
    }

    @Nested
    inner class DeleteActivityTest{
        @Test
        fun`Deleting an activity with specific id should remove it from table`() {
            transaction {
                val activityDTO = populateActivitiesTable()
                activityDTO.delete(1)
                Assertions.assertEquals(2, activityDTO.findAll().size)
            }
        }

        @Test
        fun`Deleting an activity with non-existing id should not make any changes in table`() {
            transaction {
                val activityDTO = populateActivitiesTable()
                activityDTO.delete(10)
                Assertions.assertEquals(3, activityDTO.findAll().size)
            }
        }
    }


    fun populateActivitiesTable(): TemperatureDAO {
        SchemaUtils.create(Users, Temperatures)
        val temperatureDAO = TemperatureDAO()
        val userDAO = UserDAO()
        users.forEach { user -> userDAO.save(user) }
        temperatureActivities.forEach { temperatureActivity -> temperatureDAO.save(temperatureActivity) }
        return temperatureDAO;
    }
}