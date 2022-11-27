package ie.setu.utils

import org.eclipse.jetty.server.session.*
import org.postgresql.Driver;
import java.io.File
import java.sql.Connection

fun fileSessionHandler() = SessionHandler().apply {
    sessionCache = DefaultSessionCache(this).apply {
        sessionDataStore = FileSessionDataStore().apply {
            val baseDir = File(System.getProperty("java.io.tmpdir"))
            this.storeDir = File(baseDir, "javalin-session-store").apply { mkdir() }
        }
    }
    httpOnly = true
}

fun sqlSessionHandler(url:String): SessionHandler = SessionHandler().apply {
    sessionCache = DefaultSessionCache(this).apply {
        sessionDataStore = JDBCSessionDataStoreFactory().apply {
            setDatabaseAdaptor(DatabaseAdaptor().apply {
                setDriverInfo(Driver(), url)
//                datasource = db // you can set data source here (for connection pooling, etc)
            })
        }.getSessionDataStore(sessionHandler)
    }
    httpOnly = true
}