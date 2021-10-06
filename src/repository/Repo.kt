package com.example.repository

import com.example.data.model.Note
import com.example.data.model.User
import com.example.data.table.NoteTable
import com.example.data.table.UserTable
import com.example.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*

class Repo {
    suspend fun addUser(user: User) {
        dbQuery {
           UserTable.insert { userTable ->
               userTable[email] = user.email
               userTable[hashPassword] = user.hashPassword
               userTable[userName] = user.userName
           }
        }
    }
    suspend fun findUserByEmail(email : String) = dbQuery {
        UserTable.select {
            UserTable.email.eq(email) }
            .map { rowToUser (it) }
            .singleOrNull()
    }
     private fun rowToUser(row : ResultRow?):User? {
        if (row == null) {
            return null
        }
        return User(
            email = row[UserTable.email],
            hashPassword = row[UserTable.hashPassword],
            userName = row[UserTable.userName]

        )
    }
    suspend fun addNote(note : Note, email: String) {
        dbQuery {
            NoteTable.insert {noteTable ->
                noteTable[id] = note.id
                noteTable[userEmail] = email
                noteTable[noteTitle] = note.noteTitle
                noteTable[description] = note.description
                noteTable[date] = note.date
            }
        }
    }
    suspend fun getAllNote(email: String) : List<Note> = dbQuery {
        NoteTable.select {
            NoteTable.userEmail.eq(email)
        }.mapNotNull { rowToNote(it) }
    }
    suspend fun updateNote(note: Note, email: String) {
        dbQuery {
            NoteTable.update(
                where = {
                    NoteTable.userEmail.eq(email) and NoteTable.id.eq(note.id)
                }
            ) {noteTable ->
                noteTable[noteTitle] = note.noteTitle
                noteTable[description] = note.description
                noteTable[date] = note.date
            }
        }
    }
    suspend fun deleteNote(id : String, email: String) {
        dbQuery {
            NoteTable.deleteWhere {
                NoteTable.id.eq(id) and NoteTable.userEmail.eq(email)
            }
        }
    }
    private fun rowToNote(row : ResultRow?):Note? {
        if (row == null) {
            return null
        }
        return Note(
            id = row[NoteTable.id],
            noteTitle = row[NoteTable.noteTitle],
            description = row[NoteTable.description],
            date = row[NoteTable.date]
        )
    }
}