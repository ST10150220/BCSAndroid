package network

data class MaintenanceResponse(
    val success: Boolean,
    val data: List<Maintenance>
)
data class Maintenance(
    val id: String,
    val clientName: String?,      // nullable because some items have title instead
    val title: String?,           // for items that use title
    val description: String,
    val imageUrl: String?,        // for single-image items
    val images: List<String>?,    // for items with multiple images
    val assignedContractor: String?,
    val assignedTo: String?,
    val status: String,
    val createdAt: String?,
    val updatedAt: String?
)
