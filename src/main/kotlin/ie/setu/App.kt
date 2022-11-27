package ie.setu

import ie.setu.config.DbConfig
import ie.setu.config.JavalinConfig

fun main() {

    val db = DbConfig().getDbConnection()
    JavalinConfig().startJavalinService(db)

}