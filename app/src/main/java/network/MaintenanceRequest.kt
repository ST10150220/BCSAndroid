package network

import com.google.rpc.Status

data class MaintenanceRequest(
    val clientName: String = "",
    val email: String = "",
    val description: String = "",
    val status: String = "",
    val imageUrl: String? = null
)
