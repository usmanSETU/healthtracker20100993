/*
package ie.setu.repository

import ie.setu.domain.Activity
import ie.setu.domain.User
import ie.setu.domain.db.Activities
import ie.setu.domain.db.Users
import ie.setu.domain.repository.ActivitiesDAO
import ie.setu.domain.repository.UserDAO
import ie.setu.helpers.activities
import ie.setu.helpers.nonExistingEmail
import ie.setu.helpers.users
import junit.framework.TestCase.assertEquals
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

//retrieving some test data from Fixtures
val user4 = activities.get(0)
val user5 = activities.get(1)
val user6 = activities.get(2)

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
                val activityDTO = populateUserTable()

                //Act & Assert
                assertEquals(3, activityDTO.getAll().size)
            }
        }

        @Test
        fun `get activity by id that doesn't exist, results in no user returned`() {
            transaction {

                //Arrange - create and populate table with three users
                val activityDAO = populateUserTable()

                //Act & Assert
                assertEquals(null, activityDAO.findById(4))
            }
        }

        @Test
        fun `get activity by id that exists, results in a correct user returned`() {
            transaction {
                //Arrange - create and populate table with three users
                SchemaUtils.create(Activities)
                val activityDAO = ActivitiesDAO()
                activityDAO.save(user4)
                activityDAO.save(user5)
                activityDAO.save(user6)

                //Act & Assert
                assertEquals(null, activityDAO.findById(4))
            }

        }
        @Test
        fun `get all activities over empty table returns none`() {
            transaction {

                //Arrange - create and setup userDAO object
                SchemaUtils.create(Activities)
                val activityDTO = ActivitiesDAO()

                //Act & Assert
                assertEquals(0, activityDTO.getAll().size)
            }
        }


    }

    @Nested
    inner class CreateActivities {
        @Test
        fun `multiple activities added to table can be retrieved successfully`() {
            transaction {

                //Arrange - create and populate table with three users
                val activityDAO = populateUserTable()

                //Act & Assert
                assertEquals(3, activityDAO.getAll().size)
                assertEquals(user1, activityDAO.findById(user1.id))
                assertEquals(user2, activityDAO.findById(user2.id))
                assertEquals(user3, activityDAO.findById(user3.id))
            }
        }
    }

    @Nested
    inner class DeleteActivity {
        @Test
        fun `deleting a non-existant activity in table results in no deletion`() {
            transaction {

                //Arrange - create and populate table with three activities
                val activityDAO = populateUserTable()

                //Act & Assert
                assertEquals(3, activityDAO.getAll().size)
                activityDAO.delete(4)
                assertEquals(3, activityDAO.getAll().size)
            }
        }

        @Test
        fun `deleting an existing activity in table results in record being deleted`() {
            transaction {

                //Arrange - create and populate table with three users
                val acitivityDAO = populateUserTable()

                //Act & Assert
                assertEquals(3, acitivityDAO.getAll().size)
                acitivityDAO.delete(user3.id)
                assertEquals(2, acitivityDAO.getAll().size)
            }
        }
    }

    @Nested
    inner class UpdateActivities {

        @Test
        fun `updating existing activity in table results in successful update`() {
            transaction {

                //Arrange - create and populate table with three users
                val acitivityDAO = populateUserTable()

                //Act & Assert
                val user3Updated = User(3, "new username", "new@email.ie", password="password")
                acitivityDAO.update(user3.id, user3Updated)
                assertEquals(user3Updated, acitivityDAO.findById(3))
            }
        }

        @Test
        fun `updating non-existant activity in table results in no updates`() {
            transaction {

                //Arrange - create and populate table with three users
                val acitivityDAO = populateUserTable()

                //Act & Assert
                val user4Updated = User(4, "new username", "new@email.ie", "password1")
                acitivityDAO.update(4, user4Updated)
                assertEquals(null, acitivityDAO.findById(4))
                assertEquals(3, acitivityDAO.getAll().size)
            }
        }
    }

    internal fun populateUserTable(): UserDAO{
        SchemaUtils.create(Activities)
        val userDAO = UserDAO()
        userDAO.save(user1)
        userDAO.save(user2)
        userDAO.save(user3)
        return userDAO
    }
}
*/
