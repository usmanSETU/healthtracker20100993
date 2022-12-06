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
                    "jdbc:postgresql://localhost:5432/health-tracker?user=postgres&password=password")
//                datasource = db // you can set data source here (for connection pooling, etc)
            })
        }.getSessionDataStore(sessionHandler)
    }
    httpOnly = true
}