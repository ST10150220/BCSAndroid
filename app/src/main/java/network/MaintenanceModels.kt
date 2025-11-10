package network

data class MaintenanceResponse(
    val success: Boolean,
    val data: List<Maintenance>
)
data class Maintenance(
    val id: String,
    val clientName: String?,
    val title: String?,
    val description: String,
    val imageUrl: String?,
    val images: List<String>?,
    val assignedContractor: String?,
    val assignedTo: String?,
    val status: String,
    val createdAt: String?,
    val updatedAt: String?
)
