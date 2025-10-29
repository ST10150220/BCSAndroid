package student.projects.bcsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import network.ApiClient
import network.ApiService
import network.Maintenance
import student.projects.bcsapp.R

class ProjectManagerDashboardActivity : AppCompatActivity() {

    private lateinit var recyclerRequests: RecyclerView
    private lateinit var adapter: MaintenanceAdapter // <-- declare here

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_manager_dashboard)

        recyclerRequests = findViewById(R.id.recyclerRequests)
        recyclerRequests.layoutManager = LinearLayoutManager(this)

        adapter = MaintenanceAdapter() // <-- initialize here
        recyclerRequests.adapter = adapter

        fetchMaintenanceRequests()
    }

    private fun fetchMaintenanceRequests() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Make the API call and get the response wrapped in MaintenanceResponse
                val response = ApiClient.instance.create(ApiService::class.java).getAllMaintenance().execute()

                if (response.isSuccessful && response.body() != null) {
                    // Extract the list of maintenance requests from the response
                    val requestsList = response.body()?.data ?: emptyList()

                    withContext(Dispatchers.Main) {
                        // Update the UI with the maintenance requests
                        adapter.submitList(requestsList)
                    }
                } else {
                    // Handle error, e.g., show a toast or log the error
                    withContext(Dispatchers.Main) {
                        Log.e("API", "Error: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle exception, e.g., show a toast or error message to the user
            }
        }
    }
}