package com.example.repository

import com.example.data.model.User
import com.example.data.table.UserTable
import com.example.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class Repo {
    suspend fun addUser(user: User) {
        dbQuery {
           UserTable.insert { userTable ->
               userTable[UserTable.email] = user.email
               userTable[UserTable.hashPassword] = user.hashPassword
               userTable[UserTable.userName] = user.userName
           }
        }
    }
    suspend fun findUserByEmail(email : String) = dbQuery {
        UserTable.select {
            UserTable.email.eq(email) }
            .map { rowToUser (it) }
    }
    private fun rowToUser(row : ResultRow?) : User? {
        if (row == null) {
            return null
        }
        return User(
            email = row[UserTable.email],
            userName = row[UserTable.userName],
            hashPassword = row[UserTable.hashPassword]
        )
    }
}