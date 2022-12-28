package ie.setu.repository

import ie.setu.domain.Running
import ie.setu.domain.db.BloodPressures
import ie.setu.domain.db.Runings
import ie.setu.domain.db.Users
import ie.setu.domain.repository.BloodPressureDAO
import ie.setu.domain.repository.RunningDAO
import ie.setu.domain.repository.UserDAO
import ie.setu.helpers.bloodPressureActivities
import ie.setu.helpers.runningAcitivities
import ie.setu.helpers.users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.transactionScope
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RunningDAOTest{
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
                Assertions.assertEquals(3,activityDTO.getAll().size)
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
                val newActivity = Running(1,25,150,1, DateTime.now())
                activityDTO.save(newActivity)
                Assertions.assertEquals(4,activityDTO.getAll().size)
            }
        }
    }

    @Nested
    inner class UpdateActivityTest{
        @Test
        fun`updating an activity with valid should save the new data`() {
            transaction {
                val activityDTO = populateActivitiesTable()
                val updatedActivity = Running(1, 45, 250, 1, DateTime.now())
                activityDTO.update(1, updatedActivity)
                activityDTO.findById(1)?.let {
                    Assertions.assertEquals(updatedActivity.calories, it.calories)
                    Assertions.assertEquals(updatedActivity.distance, it.distance)
                }
            }
        }

        @Test
        fun`updating an activity with non-existing id should not make any changes and return null`() {
            transaction {
                val activityDTO = populateActivitiesTable()
                val updatedActivity = Running(1, 45, 250, 1, DateTime.now())
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
                Assertions.assertEquals(2, activityDTO.getAll().size)
            }
        }

        @Test
        fun`Deleting an activity with non-existing id should not make any changes in table`() {
            transaction {
                val activityDTO = populateActivitiesTable()
                activityDTO.delete(10)
                Assertions.assertEquals(3, activityDTO.getAll().size)
            }
        }
    }


    fun populateActivitiesTable(): RunningDAO {
        SchemaUtils.create(Users, Runings)
        val runningDAO = RunningDAO()
        val userDAO = UserDAO()
        users.forEach { user -> userDAO.save(user) }
        runningAcitivities.forEach { runningActivity -> runningDAO.save(runningActivity) }
        return runningDAO;
    }
}