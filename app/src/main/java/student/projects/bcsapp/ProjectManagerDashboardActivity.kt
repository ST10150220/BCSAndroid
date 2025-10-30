package student.projects.bcsapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import network.ApiClient
import network.ApiService
import network.MaintenanceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProjectManagerDashboardActivity : AppCompatActivity() {

    private lateinit var recyclerRequests: RecyclerView
    private lateinit var adapter: MaintenanceAdapter
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_manager_dashboard)

        recyclerRequests = findViewById(R.id.recyclerRequests)
        recyclerRequests.layoutManager = LinearLayoutManager(this)

        adapter = MaintenanceAdapter(emptyList())
        recyclerRequests.adapter = adapter

        apiService = ApiClient.instance.create(ApiService::class.java)

        loadMaintenanceRequests()
    }

    private fun loadMaintenanceRequests() {
        Log.d("API_DEBUG", "Starting API call to get maintenance requests...")

        apiService.getAllMaintenance().enqueue(object : Callback<MaintenanceResponse> {
            override fun onResponse(
                call: Call<MaintenanceResponse>,
                response: Response<MaintenanceResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val maintenanceList = response.body()!!.data
                    Log.d("API_DEBUG", "Received ${maintenanceList.size} items")
                    adapter.setData(maintenanceList)
                } else {
                    Log.e("API_ERROR", "Failed to get requests: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MaintenanceResponse>, t: Throwable) {
                Log.e("API_FAILURE", "Error loading requests: ${t.message}")
            }
        })
    }
}
