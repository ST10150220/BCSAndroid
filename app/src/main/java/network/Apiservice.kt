package network


import retrofit2.Call
import retrofit2.http.*

// -------- DATA CLASSES --------
data class Invoice(
    val id: String? = null,
    val clientName: String? = null,
    val quotationId: String? = null,
    val items: List<String>? = null,
    val totalAmount: Double,
    val dueDate: String,
    val paymentMethod: String? = null,
    val paymentStatus: String? = null
)

data class Maintenance(
    val id: String?= null,
    val clientName: String?= null,
    val contractorName: String? = null,
    val createdAt: String?= null,
    val description: String?= null,
    val imageUrl: String? = null,
    val status: String?= null
)

data class MaintenanceResponse(
    val success: Boolean,
    val data: List<Maintenance>
)

data class Message(
    val id: String? = null,
    val senderId: String,
    val receiverId: String,
    val projectId: String? = null,
    val subject: String? = null,
    val body: String,
    val isRead: Boolean? = null
)


// -------- API SERVICE --------
interface ApiService {

    // -------- INVOICES --------
    @GET("invoice/all")
    fun getAllInvoices(): Call<List<Invoice>>

    @GET("invoice/{id}")
    fun getInvoiceById(@Path("id") id: String): Call<Invoice>

    @POST("invoice/create")
    fun createInvoice(@Body invoice: Invoice): Call<Invoice>

    @PUT("invoice/update/{id}")
    fun updateInvoiceStatus(
        @Path("id") id: String,
        @Body status: Map<String, String>
    ): Call<Void>

    @DELETE("invoice/delete/{id}")
    fun deleteInvoice(@Path("id") id: String): Call<Void>

    @POST("invoice/paypal/create-order")
    fun createPaypalOrder(@Body data: Map<String, String>): Call<Map<String, Any>>

    @POST("invoice/paypal/capture-payment")
    fun capturePaypalPayment(@Body data: Map<String, String>): Call<Map<String, Any>>


    // -------- MAINTENANCE --------
    @GET("maintenance/all")
    fun getAllMaintenance(): Call<MaintenanceResponse>

    @POST("maintenance/create")
    fun createMaintenance(@Body maintenance: Maintenance): Call<Maintenance>

    @PATCH("maintenance/assign/{id}")
    fun assignContractor(
        @Path("id") id: String,
        @Body contractor: Map<String, String>
    ): Call<Void>

    @PATCH("maintenance/update/{id}")
    fun updateMaintenanceStatus(
        @Path("id") id: String,
        @Body status: Map<String, String>
    ): Call<Void>


    // -------- MESSAGES --------
    @POST("messages/send")
    fun sendMessage(@Body message: Message): Call<Message>

    @GET("messages/user/{userId}")
    fun getUserMessages(@Path("userId") userId: String): Call<List<Message>>

    @PUT("messages/read/{messageId}")
    fun markMessageRead(@Path("messageId") messageId: String): Call<Void>
}
