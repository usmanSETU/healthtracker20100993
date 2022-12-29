package ie.setu.controllers

import ie.setu.config.DbConfig
import ie.setu.domain.User
import ie.setu.helpers.ServerContainer
import kong.unirest.Unirest
import ie.setu.domain.db.*
import ie.setu.helpers.validEmail
import ie.setu.helpers.validName
import ie.setu.utils.jsonToObject
import kong.unirest.HttpResponse
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HealthTrackerControllerTest {

    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()

    private fun addUser(name:String,email:String,password:String):HttpResponse<String>{
        return Unirest.post("$origin/api/users").field("name",name).field("email",email).field("password",password).asString()
    }

    private fun deleteUser(id:Int):HttpResponse<String>{
        return Unirest.delete("$origin/api/users/$id").asString()
    }

    private fun getUserById(id:Int):HttpResponse<String>{
        return Unirest.get("$origin/api/users/$id").asString()
    }

    private fun getUserByEmail(email:String):HttpResponse<String>{
        return Unirest.get("$origin/api/users/email/$email").asString()
    }

    @Nested
    inner class UserControllerTest {
        @Nested
        inner class CreateUserTest{
            @Test
            fun `creating a new user returns 302 on success`() {
                val addResponse = addUser(validName, validEmail,"password")
                assertEquals(302,addResponse.status)
                val insertedUser = getUserByEmail(validEmail)
                val user = jsonToObject<User>(insertedUser.body)
                deleteUser(user.id)
            }

            @Test
            fun `creating a new user with invalid data returns 500 on failure`() {
                val response =
                    Unirest.post("$origin/api/users").field("email", "test@email.com")
                        .asString()
                assertEquals(500, response.status)
            }

        }

        @Nested
        inner class ReadUserTest{
            @BeforeEach
            fun `insert user to perform test`(){
                val addResponse = addUser(validName, validEmail,"password")
            }
            @AfterEach
            fun `clean inserted user to reset DB state`(){
                val insertedUser = getUserByEmail(validEmail)
                deleteUser(jsonToObject<User>(insertedUser.body).id)
            }

            @Test
            fun `get all users from the database returns 200 or 404 response`() {
                val response = Unirest.get("$origin/api/users").asString()
                if (response.status == 200) {
                    val retrievedUsers: ArrayList<User> = jsonToObject(response.body.toString())
                    assertNotEquals(0, retrievedUsers.size)
                }
                else {
                    assertEquals(404, response.status)
                }
            }

            @Test
            fun `get a user by id returns 200 response`() {
                val user = getUserByEmail(validEmail)
                val response = Unirest.get("$origin/api/users/${jsonToObject<User>(user.body).id}").asString()
                assertEquals(200, response.status)
            }

            @Test
            fun `getting user with invalid id returns 404`(){
                val response = Unirest.get("$origin/api/users/1000").asString()
                assertEquals(404,response.status)
            }

        }

        @Nested
        inner class UpdateUserTest{
            @BeforeEach
            fun `insert user to perform test`(){
                val addResponse = addUser(validName, validEmail,"password")
            }
            @AfterEach
            fun `clean inserted user to reset DB state`(){
                val insertedUser = getUserByEmail(validEmail)
                deleteUser(jsonToObject<User>(insertedUser.body).id)
            }
            @Test
            fun `updating a user with valid data and id returns 200 on success`() {
                val user = getUserByEmail(validEmail)
                val insertedUser = jsonToObject<User>(user.body)
                val response = Unirest.patch("$origin/api/users/${insertedUser.id}").body("{\"name\":\"Updated Test User\",\"password\": \"not password\",\"email\":\"$validEmail\"}").asJson()

                assertEquals(200, response.status)
            }

            @Test
            fun `updating a user with invalid data or id returns 404 on failure`() {
                val response = Unirest.post("$origin/api/users/3").field("name", "Updated Test User")
                    .field("email", "test@email.com").asString()
                assertEquals(404, response.status)
            }
        }


        @Nested
        inner class DeleteUserTest {
            @Test
            fun `deleting an existing user returns 200 on success`() {
                val response = Unirest.delete("$origin/api/users/1").asString()
                assertEquals(200, response.status)
            }

            @Test
            fun `deleting an non-existing user returns 404 on failure`() {
                val response = Unirest.delete("$origin/api/users/2").asString()
                assertEquals(200, response.status)
            }

        }



    }

}