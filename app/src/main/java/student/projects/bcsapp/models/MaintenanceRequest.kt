package student.projects.bcsapp.models

data class MaintenanceRequest(
    val id: Int,
    val title: String,
    val description: String,
    val status: String,
    val dateRequested: String
)
