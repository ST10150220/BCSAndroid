package model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Message(
    val body: String = "",
    @ServerTimestamp
    val createdAt: Timestamp? = null,
    val isRead: Boolean = false,
    val projectId: String = "",
    val receiverId: String = "",
    val senderId: String = "",
    val subject: String = "",
    val users: Users? = null
)

data class Users(
    val email: String = "",
    val name: String = "",
    val role: String = ""
)
