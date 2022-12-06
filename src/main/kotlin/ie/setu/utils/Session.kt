package ie.setu.utils

import org.eclipse.jetty.server.session.*

//fun fileSessionHandler() = SessionHandler().apply {
//    sessionCache = DefaultSessionCache(this).apply {
//        sessionDataStore = FileSessionDataStore().apply {
//            val baseDir = File(System.getProperty("java.io.tmpdir"))
//            this.storeDir = File(baseDir, "javalin-session-store").apply { mkdir() }
//        }
//    }
//    httpOnly = true
//}

fun sqlSessionHandler(): SessionHandler = SessionHandler().apply {
    sessionCache = DefaultSessionCache(this).apply {
        sessionDataStore = JDBCSessionDataStoreFactory().apply {
            setDatabaseAdaptor(DatabaseAdaptor().apply {
                setDriverInfo("org.postgresql.Driver",
                    "jdbc:postgresql://lucky.db.elephantsql.com:5432/eflinqga?sslmode=require&user=eflinqga&password=0We1w7m9NbtoJssZQ89waH8rr4tg7KQR")
//                datasource = db // you can set data source here (for connection pooling, etc)
            })
        }.getSessionDataStore(sessionHandler)
    }
    httpOnly = true
}