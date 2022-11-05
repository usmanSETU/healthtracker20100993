package ie.setu.config

import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.name

class DbConfig{

    private val logger = KotlinLogging.logger {}

    //NOTE: you need the ?sslmode=require otherwise you get an error complaining about the ssl certificate
    fun getDbConnection() :Database{

        logger.info{"Starting DB Connection..."}

        val dbConfig = Database.connect(
            "jdbc:postgresql://localhost:5432/",
//            "jdbc:postgresql://ec2-54-227-248-71.compute-1.amazonaws.com:5432/ddjvur53gjatcb?sslmode=require",
            driver = "org.postgresql.Driver",
//            user = "bvsztsgdbonvvk",
//            password = "b077ea2d528fdc17f638f326aaadecc3c3ae1b5bcd7a10d859010d69cb4e5207"
            user="postgres",
            password=""
            )

        logger.info{"DbConfig name = " + dbConfig.name}
        logger.info{"DbConfig url = " + dbConfig.url}

        return dbConfig
    }

}