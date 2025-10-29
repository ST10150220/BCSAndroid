package network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiRepository {
    private val api = ApiClient.instance.create(ApiService::class.java)

    fun fetchMaintenanceRequests(onResult: (List<Maintenance>?) -> Unit) {
        api.getAllMaintenance().enqueue(object : Callback<MaintenanceResponse> {
            override fun onResponse(call: Call<MaintenanceResponse>, response: Response<MaintenanceResponse>) {
                if (response.isSuccessful) {
                    onResult(response.body()?.data) // Extract the list from the wrapper
                } else {
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<MaintenanceResponse>, t: Throwable) {
                onResult(null)
            }
        })
    }
}

