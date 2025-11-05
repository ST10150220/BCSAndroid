package network

data class MaintenanceRequest(
    val clientName: String,
    val description: String,
    val imageUrl: String? = null
)
