package network

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiRepository {
    private val api = ApiClient.instance.create(ApiService::class.java)

    fun fetchMaintenanceRequests(onResult: (List<Maintenance>?) -> Unit) {
        api.getAllMaintenance().enqueue(object : Callback<MaintenanceResponse> {
            override fun onResponse(
                call: Call<MaintenanceResponse>,
                response: Response<MaintenanceResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val maintenanceList = response.body()!!.data
                    // Pass this list to adapter or repository consumer
                }
            }

            override fun onFailure(call: Call<MaintenanceResponse>, t: Throwable) {
                Log.e("API_FAILURE", "Error: ${t.message}")
            }
        })
    }
}
