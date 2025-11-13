package student.projects.bcsapp

import com.google.firebase.Timestamp

data class AdminReport(
    val title: String = "",
    val description: String = "",
    val reportType: String = "",
    val createdAt: Timestamp? = null,
    val createdById: String = ""
)