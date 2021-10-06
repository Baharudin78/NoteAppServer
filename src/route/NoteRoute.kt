package com.example.route

import com.example.data.model.Note
import com.example.data.model.SimpleResponse
import com.example.data.model.User
import com.example.repository.Repo
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val NOTES = "$API_VERSION/notes"
const val CREATE_NOTES = "$NOTES/create"
const val UPDATE_NOTES = "$NOTES/update"
const val DELETE_NOTES = "$NOTES/delete"

@Location(CREATE_NOTES)
class NoteCreateRoute

@Location(NOTES)
class NoteGetRoute

@Location(UPDATE_NOTES)
class NoteUpdateRoute

@Location(DELETE_NOTES)
class NoteDeleteRoute

fun Route.NoteRoute(
    db :Repo,
    hashFunction : (String) -> String
) {
    authenticate("jwt") {
        post<NoteCreateRoute> {
            val note = try {
                call.receive<Note>()
            }catch (e:Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing FIeld"))
                return@post
            }
            try {
                val email = call.principal<User>()!!.email
                db.addNote(note, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note created succesfully"))
            }catch (e : Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message?: "Some Problem occured"))
            }
        }
        get<NoteGetRoute> {
            try {
                val email = call.principal<User>()!!.email
                val notes = db.getAllNote(email)
                call.respond(HttpStatusCode.OK, notes)
            }catch (e:Exception) {
                call.respond(HttpStatusCode.Conflict, emptyList<Note>())
            }
        }
        post<NoteUpdateRoute> {
            val note = try {
                call.receive<Note>()
            }catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false,"Missing field"))
                return@post
            }
            try {
                val email = call.principal<User>()!!.email
                db.updateNote(note, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note Updated Succesfully"))
            }catch (e:Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?: "Problem Occured"))
            }
        }
        delete<NoteDeleteRoute> {
            val noteId = try {
                call.request.queryParameters["id"]!!
            }catch (e:Exception) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Query Parameter is not present"))
                return@delete
            }
            try {
                val email = call.principal<User>()!!.email
                db.deleteNote(noteId, email)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Note deleted succesfully"))
            }catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, SimpleResponse(false, e.message ?:"Some Error occured"))
            }
        }

    }

}