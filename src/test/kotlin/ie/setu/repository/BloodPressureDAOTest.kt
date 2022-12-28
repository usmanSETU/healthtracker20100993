package ie.setu.repository

import ie.setu.domain.BloodPressure
import ie.setu.domain.db.BloodPressures
import ie.setu.domain.db.Users
import ie.setu.domain.repository.BloodPressureDAO
import ie.setu.domain.repository.UserDAO
import ie.setu.helpers.bloodPressureActivities
import ie.setu.helpers.users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class BloodPressureDAOTest {
    companion object {

        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @Nested
    inner class GetBloodPressureActivities{
        @Test
        fun `getting all blood pressure activities from a populated table returns all rows`(){
            transaction {
                val activityDTO = populateActivitiesTable();
                Assertions.assertEquals(3,activityDTO.getAll().size)
            }
        }
        @Test
        fun `getting all blood pressure activities from a populated table returns all records of a specific user`(){
            transaction {
                val activityDTO = populateActivitiesTable();
                Assertions.assertEquals(3,activityDTO.findByUserId(1).size)
            }
        }
        @Test
        fun `getting all blood pressure activities from a populated table for a user not existing returns no record`(){
            transaction {
                val activityDTO = populateActivitiesTable();
                Assertions.assertEquals(0,activityDTO.findByUserId(5).size)
            }
        }

    }

    @Nested
    inner class UpdateBloodPressureActivities{
        @Test
        fun `update blood pressure activity for a specific id`(){
            transaction {
                val activityDTO = populateActivitiesTable();
                val updatedActivity = BloodPressure( id=1,pulse = 150, systolic =  230, diastolic = 180, userId = 1, createdAt = DateTime.now())
                activityDTO.update(1, updatedActivity)
                activityDTO.findById(1)?.let {
                    Assertions.assertEquals(it.pulse, updatedActivity.pulse)
                    Assertions.assertEquals(it.systolic, updatedActivity.systolic)
                    Assertions.assertEquals(it.diastolic, updatedActivity.diastolic)
                }
            }
        }

        @Test
        fun `update blood pressure activity for a specific non-existing id` (){
            transaction {
                val activityDTO = populateActivitiesTable()
                val updatedActivity= BloodPressure(1,72,120,60,1, DateTime.now())
                Assertions.assertNull(activityDTO.update(50,updatedActivity))
            }
        }


    }

    @Nested
    inner class CreateBloodPressureActivityTest{
        @Test
        fun`create a blood pressure activity`() {
            transaction {
                val activityDTO = populateActivitiesTable()
                val newActivity = BloodPressure(1, 72, 120, 80, 1, DateTime.now())
                activityDTO.save(newActivity)
                Assertions.assertEquals(4, activityDTO.getAll().size)
            }
        }
    }

    @Nested
    inner class DeleteBloodPressureActivityTest{
        @Test
        fun `delete an existing activity`() {
            transaction {
                val activityDTO = populateActivitiesTable()
                activityDTO.delete(1)
                Assertions.assertEquals(2, activityDTO.getAll().size)
            }
        }

        @Test
        fun `try to delete a non-existing activity`() {
            transaction {
                val activityDTO = populateActivitiesTable()
                activityDTO.delete(6)
                Assertions.assertEquals(3, activityDTO.getAll().size)
            }
        }
    }


    fun populateActivitiesTable():BloodPressureDAO{
        SchemaUtils.create(Users,BloodPressures)
        val bloodPressureDAO = BloodPressureDAO()
        val userDAO = UserDAO()
        users.forEach { user -> userDAO.save(user) }
        bloodPressureActivities.forEach { bloodPressure -> bloodPressureDAO.save(bloodPressure) }
        return bloodPressureDAO;
    }
}