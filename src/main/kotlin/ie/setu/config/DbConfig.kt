package ie.setu.config
import ie.setu.domain.db.Activities
import ie.setu.domain.db.Users
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.name
import org.jetbrains.exposed.sql.transactions.transaction

class DbConfig{

    private val logger = KotlinLogging.logger {}

    //NOTE: you need the ?sslmode=require otherwise you get an error complaining about the ssl certificate
    fun getDbConnection() :Database{

        logger.info{"Starting DB Connection..."}

        val dbConfig = Database.connect(
            "jdbc:postgresql://lucky.db.elephantsql.com:5432/eflinqga?sslmode=require",
            driver = "org.postgresql.Driver",
            user = "eflinqga",
            password = "0We1w7m9NbtoJssZQ89waH8rr4tg7KQR"
        )
        
        transaction{
            SchemaUtils.createMissingTablesAndColumns(Users,Activities)
        }

        logger.info{"DbConfig name = " + dbConfig.name}
        logger.info{"DbConfig url = " + dbConfig.url}

        return dbConfig
    }

}