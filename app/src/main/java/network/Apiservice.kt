package network

import retrofit2.Response
import retrofit2.Call
import retrofit2.http.*
interface ApiService {

    data class AssignContractorRequest(
        val contractorName: String
    )

    data class SendMessageRequest(
        val body: String,
        val projectId: String?,
        val receiverId: String,
        val senderId: String,
        val subject: String
    )

    data class Message(
        val id: String,
        val senderId: String,
        val receiverId: String,
        val projectId: String?,
        val subject: String,
        val body: String,
        var isRead: Boolean,
        val createdAt: String
    )

    // -------- MAINTENANCE --------

    @GET("maintenance/all")
    fun getAllMaintenance(): Call<MaintenanceResponse>

    @POST("maintenance/create")
    fun createRequest(@Body request: MaintenanceRequest): Call<MaintenanceResponse>

    @PATCH("maintenance/assign/{id}")
    fun assignContractor(
        @Path("id") maintenanceId: String,
        @Body body: AssignContractorRequest
    ): Call<Void>

    @PATCH("maintenance/update/{id}")
    fun updateMaintenanceStatus(
        @Path("id") id: String,
        @Body status: Map<String, String>
    ): Call<Void>

    @DELETE("maintenance/delete/{id}")
    fun deleteMaintenance(@Path("id") id: String): Call<Void>

    @POST("messages/send")
    fun sendMessage(@Body request: SendMessageRequest): Call<Void>

    @GET("messages/user/{userId}")
    fun getUserMessages(@Path("userId") userId: String): Call<MessagesResponse>

    @PUT("messages/read/{messageId}")
    fun markMessageRead(@Path("messageId") messageId: String): Call<Void>
}
