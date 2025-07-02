package com.lamz.data

import com.lamz.model.TodoItem
import com.lamz.model.Todos
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(){
        Database.connect(
            url = "jdbc:mariadb://localhost:3306/todolist_db",
            driver = "org.mariadb.jdbc.Driver",
            user = "root",
            password = ""
        )

        transaction {
            SchemaUtils.create(Todos)
        }
    }
}