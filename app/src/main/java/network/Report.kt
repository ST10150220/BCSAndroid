package student.projects.bcsapp

import com.google.firebase.firestore.Blob

data class Report(
    var id: String = "",
    val taskName: String = "",
    val fileName: String = "",
    val fileData: Blob? = null,
    var status: String = "pending"  // default pending
)
