package ie.setu.helpers

import ie.setu.domain.User

val nonExistingEmail = "112233445566778testUser@xxxxx.xx"
val validName = "Test User 1"
val validEmail = "testuser1@test.com"

val users = arrayListOf<User>(
    User(name = "Usman Chughtai", email = "usman@setu.com", id = 1),
    User(name = "Alice", email = "alice@setu.ie", id = 2),
    User(name = "John", email = "john@gmail.com", id = 3),
    User(name = "Steve", email = "steve@apple.com", id = 4)
)