package ie.setu.controllers

import ie.setu.config.DbConfig
import ie.setu.domain.*
import ie.setu.helpers.ServerContainer
import kong.unirest.Unirest
import ie.setu.domain.db.*
import ie.setu.helpers.validEmail
import ie.setu.helpers.validName
import ie.setu.utils.jsonNodeToObject
import ie.setu.utils.jsonToObject
import kong.unirest.Callback
import kong.unirest.HttpResponse
import org.joda.time.DateTime
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

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class ActivityControllerTest{
        @BeforeAll
        fun `setup`() {
            val user = addUser(validName, validEmail, "password")
        }

        @AfterAll
        fun `teardown`() {
            val user = jsonToObject<User>(getUserByEmail(validEmail).body)
            deleteUser(user.id)
        }

        @Nested
        inner class CreateActivityTest {
            @Test
            fun `Creating an activity with valid data should return 200 status`() {
                val user = jsonToObject<User>(getUserByEmail(validEmail).body)
                val activityResponse = Unirest
                    .post("$origin/api/activities")
                    .body("{\"activityName\":\"activity\",\"calories\":\"25\",\"userId\":\"${user.id}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                assertEquals(200, activityResponse.status)
                val activity = jsonNodeToObject<Activity>(activityResponse)
                Unirest.delete("$origin/api/activities/${activity.id}")
            }

            @Test
            fun `Creating an activity with invalid data should return 500 status`() {
                val user = jsonToObject<User>(getUserByEmail(validEmail).body)
                val activityResponse = Unirest
                    .post("$origin/api/activities")
                    .body("{\"activityName\":\"activity\",\"calories\":\"25\",\"userId\":\"${user.id}\"}")
                    .asJson()
                assertEquals(500, activityResponse.status)
            }

            @Test
            fun `Creating an activity with invalid userId should return 401 status`() {
                val activityResponse = Unirest
                    .post("$origin/api/activities")
                    .body("{\"activityName\":\"activity\",\"calories\":\"25\",\"userId\":\"0\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                assertEquals(500, activityResponse.status)
            }
        }

        @Nested
        inner  class ReadActivityTest {
            @Test
            fun `get all activities should return 200`(){
                val activityResponse = Unirest.get("$origin/api/activities").asString()
                assertEquals(200,activityResponse.status)
            }
            @Test
            fun `get an activity by id should return 200 status`(){
                val userResponse = getUserByEmail(validEmail)
                val user = jsonToObject<User>(getUserByEmail(validEmail).body)
                val activityResponse = Unirest
                    .post("$origin/api/activities")
                    .body("{\"activityName\":\"activity\",\"calories\":\"25\",\"userId\":\"${user.id}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                val activity = jsonNodeToObject<Activity>(activityResponse)

                val getActivityResponse = Unirest.get("$origin/api/activities/${activity.id}").asJson()
                assertEquals(200,getActivityResponse.status)
                Unirest.delete("$origin/api/activities/${activity.id}")
            }

            @Test
            fun `getting an activity by non-existing id should return 404 status`(){
                val activityResponse = Unirest.get("$origin/api/activities/${0}").asString()
                assertEquals(404,activityResponse.status)
            }
        }

        @Nested
        inner class UpdateActivityTest{
            @Test
            fun`updating an activity with valid data should return 200 status`(){
                val user = jsonToObject<User>(getUserByEmail(validEmail).body)
                val activityResponse = Unirest
                    .post("$origin/api/activities")
                    .body("{\"activityName\":\"activity\",\"calories\":\"25\",\"userId\":\"${user.id}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                val activity = jsonNodeToObject<Activity>(activityResponse)
                val updatedActivityReponse = Unirest
                    .patch("$origin/api/activities/${activity.id}")
                    .body("{\"activityName\":\"updated activity\",\"calories\":\"125\",\"userId\":\"${user.id}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                assertEquals(200,updatedActivityReponse.status)
                Unirest.delete("$origin/api/activities/${activity.id}")
            }

            @Test
            fun`updating an activity with invalid data should return 500 status`(){
                val updatedActivityReponse = Unirest
                    .patch("$origin/api/activities/${0}")
                    .body("{\"activityName\":\"updated activity\",\"calories\":\"125\",\"userId\":\"${0}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                assertEquals(500,updatedActivityReponse.status)
            }
        }

        @Nested
        inner class DeleteActivityTest{
            @Test
            fun`deleting an activity with valid id should remove it from table`(){
                val user = jsonToObject<User>(getUserByEmail(validEmail).body)
                val activityResponse = Unirest
                    .post("$origin/api/activities")
                    .body("{\"activityName\":\"activity\",\"calories\":\"25\",\"userId\":\"${user.id}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                val activity = jsonNodeToObject<Activity>(activityResponse)
                Unirest.delete("$origin/api/activities/${activity.id}").asJsonAsync(Callback {
                    val getActivityResponse = Unirest.get("$origin/api/activities/${activity.id}").asString()
                    assertEquals(404,getActivityResponse.status)
                })
            }

            @Test
            fun`deleting an activity with non-existing id should not make any changes`(){
                val deleteReponse = Unirest.delete("$origin/api/activities/0").asString()
                assertEquals(200,deleteReponse.status)
            }
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class BloodPressureActivityControllerTest{
        @BeforeAll
        fun `setup`() {
            val user = addUser(validName, validEmail, "password")
        }

        @AfterAll
        fun `teardown`() {
            val user = jsonToObject<User>(getUserByEmail(validEmail).body)
            deleteUser(user.id)
        }

        @Nested
        inner class CreateActivityTest {
            @Test
            fun `Creating an activity with valid data should return 200 status`() {
                val user = jsonToObject<User>(getUserByEmail(validEmail).body)
                val activityResponse = Unirest
                    .post("$origin/api/blood-pressure")
                    .body("{\"pulse\":\"72\",\"systolic\":\"120\",\"diastolic\":\"80\",\"userId\":\"${user.id}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                assertEquals(200, activityResponse.status)
                val activity = jsonNodeToObject<BloodPressure>(activityResponse)
                Unirest.delete("$origin/api/blood-pressure/${activity.id}")
            }

            @Test
            fun `Creating an activity with invalid data should return 500 status`() {
                val user = jsonToObject<User>(getUserByEmail(validEmail).body)
                val activityResponse = Unirest
                    .post("$origin/api/blood-pressure")
                    .body("{\"activityName\":\"activity\",\"calories\":\"25\",\"userId\":\"${user.id}\"}")
                    .asJson()
                assertEquals(500, activityResponse.status)
            }

            @Test
            fun `Creating an activity with invalid userId should return 401 status`() {
                val activityResponse = Unirest
                    .post("$origin/api/activities")
                    .body("{\"pulse\":\"72\",\"systolic\":\"120\",\"diastolic\":\"80\",\"userId\":\"${0}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                assertEquals(500, activityResponse.status)
            }
        }

        @Nested
        inner  class ReadActivityTest {
            @Test
            fun `get all activities should return 200`(){
                val activityResponse = Unirest.get("$origin/api/blood-pressure").asString()
                assertEquals(200,activityResponse.status)
            }
            @Test
            fun `get an activity by id should return 200 status`(){
                val userResponse = getUserByEmail(validEmail)
                val user = jsonToObject<User>(getUserByEmail(validEmail).body)
                val activityResponse = Unirest
                    .post("$origin/api/blood-pressure")
                    .body("{\"pulse\":\"72\",\"systolic\":\"120\",\"diastolic\":\"80\",\"userId\":\"${user.id}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                val activity = jsonNodeToObject<BloodPressure>(activityResponse)

                val getActivityResponse = Unirest.get("$origin/api/blood-pressure/${activity.id}").asJson()
                assertEquals(200,getActivityResponse.status)
                Unirest.delete("$origin/api/blood-pressure/${activity.id}")
            }

            @Test
            fun `getting an activity by non-existing id should return 404 status`(){
                val activityResponse = Unirest.get("$origin/api/blood-pressure/${0}").asString()
                assertEquals(404,activityResponse.status)
            }
        }

        @Nested
        inner class UpdateActivityTest{
            @Test
            fun`updating an activity with valid data should return 200 status`(){
                val user = jsonToObject<User>(getUserByEmail(validEmail).body)
                val activityResponse = Unirest
                    .post("$origin/api/blood-pressure")
                    .body("{\"pulse\":\"72\",\"systolic\":\"120\",\"diastolic\":\"80\",\"userId\":\"${user.id}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                val activity = jsonNodeToObject<BloodPressure>(activityResponse)
                val updatedActivityReponse = Unirest
                    .patch("$origin/api/blood-pressure/${activity.id}")
                    .body("{\"pulse\":\"92\",\"systolic\":\"150\",\"diastolic\":\"100\",\"userId\":\"${user.id}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                assertEquals(200,updatedActivityReponse.status)
                Unirest.delete("$origin/api/blood-pressure/${activity.id}")
            }

            @Test
            fun`updating an activity with invalid data should return 500 status`(){
                val updatedActivityReponse = Unirest
                    .patch("$origin/api/blood-pressure/${0}")
                    .body("{\"pulse\":\"72\",\"systolic\":\"120\",\"diastolic\":\"80\",\"userId\":\"${0}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                assertEquals(500,updatedActivityReponse.status)
            }
        }

        @Nested
        inner class DeleteActivityTest{
            @Test
            fun`deleting an activity with valid id should remove it from table`(){
                val user = jsonToObject<User>(getUserByEmail(validEmail).body)
                val activityResponse = Unirest
                    .post("$origin/api/blood-pressure")
                    .body("{\"pulse\":\"72\",\"systolic\":\"120\",\"diastolic\":\"80\",\"userId\":\"${user.id}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                val activity = jsonNodeToObject<BloodPressure>(activityResponse)
                Unirest.delete("$origin/api/blood-pressure/${activity.id}").asJsonAsync(Callback {
                    val getActivityResponse = Unirest.get("$origin/api/blood-pressure/${activity.id}").asString()
                    assertEquals(404,getActivityResponse.status)
                })
            }

            @Test
            fun`deleting an activity with non-existing id should not make any changes`(){
                val deleteReponse = Unirest.delete("$origin/api/blood-pressure/0").asString()
                assertEquals(200,deleteReponse.status)
            }
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class RunningActivityControllerTest{
        @BeforeAll
        fun `setup`() {
            val user = addUser(validName, validEmail, "password")
        }

        @AfterAll
        fun `teardown`() {
            val user = jsonToObject<User>(getUserByEmail(validEmail).body)
            deleteUser(user.id)
        }

        @Nested
        inner class CreateActivityTest {
            @Test
            fun `Creating an activity with valid data should return 200 status`() {
                val user = jsonToObject<User>(getUserByEmail(validEmail).body)
                val activityResponse = Unirest
                    .post("$origin/api/running")
                    .body("{\"distance\":\"72\",\"calories\":\"120\",\"userId\":\"${user.id}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                assertEquals(200, activityResponse.status)
                val activity = jsonNodeToObject<Running>(activityResponse)
                Unirest.delete("$origin/api/running/${activity.id}")
            }

            @Test
            fun `Creating an activity with invalid data should return 500 status`() {
                val user = jsonToObject<User>(getUserByEmail(validEmail).body)
                val activityResponse = Unirest
                    .post("$origin/api/running")
                    .body("{\"activityName\":\"activity\",\"calories\":\"25\",\"userId\":\"${user.id}\"}")
                    .asJson()
                assertEquals(500, activityResponse.status)
            }

            @Test
            fun `Creating an activity with invalid userId should return 401 status`() {
                val activityResponse = Unirest
                    .post("$origin/api/running")
                    .body("{\"distance\":\"72\",\"calories\":\"120\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                assertEquals(500, activityResponse.status)
            }
        }

        @Nested
        inner  class ReadActivityTest {
            @Test
            fun `get all activities should return 200`(){
                val activityResponse = Unirest.get("$origin/api/running").asString()
                assertEquals(200,activityResponse.status)
            }
            @Test
            fun `get an activity by id should return 200 status`(){
                val userResponse = getUserByEmail(validEmail)
                val user = jsonToObject<User>(getUserByEmail(validEmail).body)
                val activityResponse = Unirest
                    .post("$origin/api/running")
                    .body("{\"distance\":\"72\",\"calories\":\"120\",\"userId\":\"${user.id}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                val activity = jsonNodeToObject<Running>(activityResponse)

                val getActivityResponse = Unirest.get("$origin/api/running/${activity.id}").asJson()
                assertEquals(200,getActivityResponse.status)
                Unirest.delete("$origin/api/running/${activity.id}")
            }

            @Test
            fun `getting an activity by non-existing id should return 404 status`(){
                val activityResponse = Unirest.get("$origin/api/running/${0}").asString()
                assertEquals(404,activityResponse.status)
            }
        }

        @Nested
        inner class UpdateActivityTest{
            @Test
            fun`updating an activity with valid data should return 200 status`(){
                val user = jsonToObject<User>(getUserByEmail(validEmail).body)
                val activityResponse = Unirest
                    .post("$origin/api/running")
                    .body("{\"distance\":\"72\",\"calories\":\"120\",\"userId\":\"${user.id}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                val activity = jsonNodeToObject<Running>(activityResponse)
                val updatedActivityResponse = Unirest
                    .patch("$origin/api/running/${activity.id}")
                    .body("{\"distance\":\"100\",\"calories\":\"150\",\"userId\":\"${user.id}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                assertEquals(200,updatedActivityResponse.status)
                Unirest.delete("$origin/api/running/${activity.id}")
            }

            @Test
            fun`updating an activity with invalid data should return 500 status`(){
                val updatedActivityReponse = Unirest
                    .patch("$origin/api/running/${0}")
                    .body("{\"calories\":\"120\",\"userId\":\"${0}\"}")
                    .asJson()
                assertEquals(500,updatedActivityReponse.status)
            }
        }

        @Nested
        inner class DeleteActivityTest{
            @Test
            fun`deleting an activity with valid id should remove it from table`(){
                val user = jsonToObject<User>(getUserByEmail(validEmail).body)
                val activityResponse = Unirest
                    .post("$origin/api/running")
                    .body("{\"distance\":\"72\",\"calories\":\"120\",\"userId\":\"${user.id}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                val activity = jsonNodeToObject<Running>(activityResponse)
                Unirest.delete("$origin/api/running/${activity.id}").asJsonAsync(Callback {
                    val getActivityResponse = Unirest.get("$origin/api/running/${activity.id}").asString()
                    assertEquals(404,getActivityResponse.status)
                })
            }

            @Test
            fun`deleting an activity with non-existing id should not make any changes`(){
                val deleteReponse = Unirest.delete("$origin/api/running/0").asString()
                assertEquals(200,deleteReponse.status)
            }
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class TemperatureActivityControllerTest{
        @BeforeAll
        fun `setup`() {
            val user = addUser(validName, validEmail, "password")
        }

        @AfterAll
        fun `teardown`() {
            val user = jsonToObject<User>(getUserByEmail(validEmail).body)
            deleteUser(user.id)
        }

        @Nested
        inner class CreateActivityTest {
            @Test
            fun `Creating an activity with valid data should return 200 status`() {
                val user = jsonToObject<User>(getUserByEmail(validEmail).body)
                val activityResponse = Unirest
                    .post("$origin/api/temperature")
                    .body("{\"temperature\":\"72\",\"userId\":\"${user.id}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                assertEquals(200, activityResponse.status)
                val activity = jsonNodeToObject<Temperature>(activityResponse)
                Unirest.delete("$origin/api/temperature/${activity.id}")
            }

            @Test
            fun `Creating an activity with invalid data should return 500 status`() {
                val user = jsonToObject<User>(getUserByEmail(validEmail).body)
                val activityResponse = Unirest
                    .post("$origin/api/temperature")
                    .body("{\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                assertEquals(500, activityResponse.status)
            }

            @Test
            fun `Creating an activity with invalid userId should return 401 status`() {
                val activityResponse = Unirest
                    .post("$origin/api/temperature")
                    .body("{\"temperature\":\"72\",\"userId\":\"${0}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                assertEquals(500, activityResponse.status)
            }
        }

        @Nested
        inner  class ReadActivityTest {
            @Test
            fun `get all activities should return 200`(){
                val activityResponse = Unirest.get("$origin/api/temperature").asString()
                assertEquals(200,activityResponse.status)
            }
            @Test
            fun `get an activity by id should return 200 status`(){
                val userResponse = getUserByEmail(validEmail)
                val user = jsonToObject<User>(getUserByEmail(validEmail).body)
                val activityResponse = Unirest
                    .post("$origin/api/temperature")
                    .body("{\"temperature\":\"72\",\"userId\":\"${user.id}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                val activity = jsonNodeToObject<Temperature>(activityResponse)

                val getActivityResponse = Unirest.get("$origin/api/temperature/${activity.id}").asJson()
                assertEquals(200,getActivityResponse.status)
                Unirest.delete("$origin/api/temperature/${activity.id}")
            }

            @Test
            fun `getting an activity by non-existing id should return 404 status`(){
                val activityResponse = Unirest.get("$origin/api/temperature/${0}").asString()
                assertEquals(404,activityResponse.status)
            }
        }

        @Nested
        inner class UpdateActivityTest{
            @Test
            fun`updating an activity with valid data should return 200 status`(){
                val user = jsonToObject<User>(getUserByEmail(validEmail).body)
                val activityResponse = Unirest
                    .post("$origin/api/temperature")
                    .body("{\"temperature\":\"72\",\"userId\":\"${user.id}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                val activity = jsonNodeToObject<Temperature>(activityResponse)
                val updatedActivityResponse = Unirest
                    .patch("$origin/api/temperature/${activity.id}")
                    .body("{\"temperature\":\"102\",\"userId\":\"${user.id}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                assertEquals(200,updatedActivityResponse.status)
                Unirest.delete("$origin/api/temperature/${activity.id}")
            }

            @Test
            fun`updating an activity with invalid data should return 500 status`(){
                val updatedActivityReponse = Unirest
                    .patch("$origin/api/temperature/${0}")
                    .body("{\"temperature\":\"72\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                assertEquals(500,updatedActivityReponse.status)
            }
        }

        @Nested
        inner class DeleteActivityTest{
            @Test
            fun`deleting an activity with valid id should remove it from table`(){
                val user = jsonToObject<User>(getUserByEmail(validEmail).body)
                val activityResponse = Unirest
                    .post("$origin/api/temperature")
                    .body("{\"temperature\":\"72\",\"userId\":\"${user.id}\",\"createdAt\":\"${DateTime.now()}\"}")
                    .asJson()
                val activity = jsonNodeToObject<Temperature>(activityResponse)
                Unirest.delete("$origin/api/temperature/${activity.id}").asJsonAsync(Callback {
                    val getActivityResponse = Unirest.get("$origin/api/temperature/${activity.id}").asString()
                    assertEquals(404,getActivityResponse.status)
                })
            }

            @Test
            fun`deleting an activity with non-existing id should not make any changes`(){
                val deleteReponse = Unirest.delete("$origin/api/temperature/0").asString()
                assertEquals(200,deleteReponse.status)
            }
        }

    }


}
