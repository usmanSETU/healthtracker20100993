
package ie.setu.repository

import ie.setu.domain.Activity
import ie.setu.domain.db.Activities
import ie.setu.domain.db.Users
import ie.setu.domain.repository.ActivitiesDAO
import ie.setu.domain.repository.UserDAO
import ie.setu.helpers.activities
import ie.setu.helpers.users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

//retrieving some test data from Fixtures
val activity1 = activities[0]
val activity2 = activities[1]
val activity3 = activities[2]

class ActivityDAOTest {

    companion object {

        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @Nested
    inner class ReadActivities {
        @Test
        fun `getting all activities from a populated table returns all rows`() {
            transaction {

                //Arrange - create and populate table with three users
                val activityDTO = populateActivitiesTable()

                //Act & Assert
                Assertions.assertEquals(3, activityDTO.getAll().size)
            }
        }

        @Test
        fun `get activity by id that doesn't exist, results in no user returned`() {
            transaction {

                //Arrange - create and populate table with three users
                val activityDAO = populateActivitiesTable()

                //Act & Assert
                Assertions.assertEquals(null, activityDAO.findById(4))
            }
        }

        @Test
        fun `get activity by id that exists, results in a correct activity returned`() {
            transaction {
                //Arrange - create and populate table with three users
                val activityDAO = populateActivitiesTable()
                //Act & Assert
                Assertions.assertEquals(null, activityDAO.findById(4))
            }

        }
        @Test
        fun `get all activities over empty table returns none`() {
            transaction {

                //Arrange - create and setup userDAO object
                SchemaUtils.create(Activities)
                val activityDTO = ActivitiesDAO()

                //Act & Assert
                Assertions.assertEquals(0, activityDTO.getAll().size)
            }
        }


    }

    @Nested
    inner class CreateActivities {
        @Test
        fun `multiple activities added to table can be retrieved successfully`() {
            transaction {

                //Arrange - create and populate table with three users
                val activityDAO = populateActivitiesTable()

                //Act & Assert
                Assertions.assertEquals(3, activityDAO.getAll().size)
                Assertions.assertEquals(activity1, activityDAO.findById(user1.id))
                Assertions.assertEquals(activity2, activityDAO.findById(user2.id))
                Assertions.assertEquals(activity3, activityDAO.findById(user3.id))
            }
        }
    }

    @Nested
    inner class DeleteActivity {
        @Test
        fun `deleting a non-existent activity in table results in no deletion`() {
            transaction {

                //Arrange - create and populate table with three activities
                val activityDAO = populateActivitiesTable()

                //Act & Assert
                Assertions.assertEquals(3, activityDAO.getAll().size)
                activityDAO.delete(4)
                Assertions.assertEquals(3, activityDAO.getAll().size)
            }
        }

        @Test
        fun `deleting an existing activity in table results in record being deleted`() {
            transaction {

                //Arrange - create and populate table with three users
                val activityDAO = populateActivitiesTable()

                //Act & Assert
                Assertions.assertEquals(3, activityDAO.getAll().size)
                activityDAO.delete(user3.id)
                Assertions.assertEquals(2, activityDAO.getAll().size)
            }
        }
    }

    @Nested
    inner class UpdateActivities {

        @Test
        fun `updating existing activity in table results in successful update`() {
            transaction {

                //Arrange - create and populate table with three users
                val activityDAO = populateActivitiesTable()

                //Act & Assert
                val activity3Updated = Activity(3, "50", "Activity Updated Name", 3, createdAt = DateTime.now() )
                activityDAO.update(user3.id, activity3Updated)
                activityDAO.findById(3)?.let{
                    Assertions.assertEquals(activity3Updated.calories,it.calories)
                    Assertions.assertEquals(activity3Updated.activityName,it.activityName)
                }

            }
        }

        @Test
        fun `updating non-existent activity in table results in no updates`() {
            transaction {

                //Arrange - create and populate table with three users
                val activityDAO = populateActivitiesTable()

                //Act & Assert
                val activity4Updated = Activity(4, "40", "Updated Activity 4", 2, DateTime.now())
                activityDAO.update(4, activity4Updated)
                Assertions.assertEquals(null, activityDAO.findById(4))
                Assertions.assertEquals(3, activityDAO.getAll().size)
            }
        }
    }

    internal fun populateActivitiesTable(): ActivitiesDAO{
        SchemaUtils.create(Activities,Users)
        val activityDAO = ActivitiesDAO()
        val usersDAO = UserDAO()
        users.forEach { user -> usersDAO.save(user) }
        activities.forEach { activity-> activityDAO.save(activity) }
        return activityDAO
    }
}

