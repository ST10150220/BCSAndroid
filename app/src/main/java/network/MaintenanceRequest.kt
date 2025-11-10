package network

import com.google.firebase.Timestamp
import com.google.rpc.Status

data class MaintenanceRequest(
    var id: String = "",
    val clientName: String = "",
    val email: String = "",
    val description: String = "",
    val status: String = "",
    val imageUrl: String? = null,
    val createdAt: String? = null,
    val assignedContractor: String? = null
)
