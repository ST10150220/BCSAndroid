package network

import retrofit2.Call
import retrofit2.http.*
interface ApiService {

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

    @DELETE("maintenance/delete/{id}")
    fun deleteMaintenance(@Path("id") id: String): Call<Void>
}
