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
        val PGUSER = "eflinqga"
        val PGPASSWORD = "0We1w7m9NbtoJssZQ89waH8rr4tg7KQR"
        val PGHOST = "lucky.db.elephantsql.com"
        val PGPORT = "5432"
        val PGDATABASE = "eflinqga"

        //url format should be jdbc:postgresql://host:port/database
        val url = "jdbc:postgresql://$PGHOST:$PGPORT/$PGDATABASE"

        val dbConfig = Database.connect(url,
            driver="org.postgresql.Driver",
            user = PGUSER,
            password = PGPASSWORD

        )
        transaction{
            SchemaUtils.createMissingTablesAndColumns(Users,Activities)
        }

        logger.info{"DbConfig name = " + dbConfig.name}
        logger.info{"DbConfig url = " + dbConfig.url}

        return dbConfig
    }

}