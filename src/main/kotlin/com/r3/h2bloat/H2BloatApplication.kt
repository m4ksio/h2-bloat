package com.r3.h2bloat

import org.hibernate.Session
import org.hibernate.cfg.Configuration
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Lob


@Entity
data class BloatedEntity(
    @Id
    var id: String,

    @Lob
    var content: ByteArray
)

val startTime = System.currentTimeMillis().toString()

var counter = 0

fun main(args: Array<String>) {

    val sessionFactory = Configuration()
            .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
            .setProperty("hibernate.connection.url", "jdbc:h2:./database/test;DB_CLOSE_ON_EXIT=TRUE;")
            .setProperty("hibernate.connection.username", "sa")
            .setProperty("hibernate.connection.password", "")
            .setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
            .setProperty("hibernate.hbm2ddl.auto", "create")
            .addAnnotatedClass(BloatedEntity::class.java)
            .buildSessionFactory()



    sessionFactory.use {
        it.openSession().use {

            it.inSession()
        }
    }
}

fun Session.inSession() {

    val random = Random()

    (1..1000).forEach {

        val transaction = beginTransaction()

        (1..1000).forEach {

            val bytes = ByteArray(25 * 1024) // 25kB

            random.nextBytes(bytes)

            val entity = BloatedEntity(startTime + "_" + counter++, bytes)
            persist(entity)
        }
        flush()
        clear()

        transaction.commit()
    }

}
