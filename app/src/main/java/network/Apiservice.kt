package network

import retrofit2.Call
import retrofit2.http.*

data class Invoice(
    val id: String? = null,
    val amount: Double,
    val description: String,
    val date: String
)

data class Maintenance(
    val id: String? = null,
    val title: String,
    val description: String,
    val status: String
)

data class Message(
    val senderId: String,
    val receiverId: String,
    val content: String,
    val timestamp: String? = null
)

interface ApiService {

    // -------- INVOICES --------
    @GET("invoices")
    fun getAllInvoices(): Call<List<Invoice>>

    @GET("invoices/{id}")
    fun getInvoiceById(@Path("id") id: String): Call<Invoice>

    @POST("invoices")
    fun createInvoice(@Body invoice: Invoice): Call<Invoice>

    @DELETE("invoices/{id}")
    fun deleteInvoice(@Path("id") id: String): Call<Void>


    // -------- MAINTENANCE --------
    @GET("maintenance")
    fun getAllMaintenance(): Call<List<Maintenance>>

    @GET("maintenance/{id}")
    fun getMaintenanceById(@Path("id") id: String): Call<Maintenance>

    @POST("maintenance")
    fun createMaintenance(@Body maintenance: Maintenance): Call<Maintenance>


    // -------- MESSAGES --------
    @GET("messages/{senderId}/{receiverId}")
    fun getMessages(
        @Path("senderId") senderId: String,
        @Path("receiverId") receiverId: String
    ): Call<List<Message>>

    @POST("messages")
    fun sendMessage(@Body message: Message): Call<Message>
}
