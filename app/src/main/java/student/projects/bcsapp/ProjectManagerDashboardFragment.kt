package student.projects.bcsapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import network.ApiClient
import network.ApiService
import network.MaintenanceRequest
import network.MaintenanceResponse

class ProjectManagerDashboardFragment : Fragment() {

    private lateinit var recyclerRequests: RecyclerView
    private lateinit var adapter: MaintenanceRequestAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var fabRefresh: FloatingActionButton
    private lateinit var etSearch: EditText
    private lateinit var spinnerStatus: Spinner
    private lateinit var apiService: ApiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_project_manager_dashboard, container, false)

        recyclerRequests = view.findViewById(R.id.recyclerViewRequests)
        progressBar = view.findViewById(R.id.progressBarAssign)
        fabRefresh = view.findViewById(R.id.fabRefresh)
        etSearch = view.findViewById(R.id.etSearch)
        spinnerStatus = view.findViewById(R.id.spinnerStatus)

        // ✅ Populate spinner with status_array
        val statusAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.status_array)
        )
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = statusAdapter

        recyclerRequests.layoutManager = LinearLayoutManager(requireContext())

        adapter = MaintenanceRequestAdapter(emptyList()) { selectedRequest ->
            val fragment = AssignContractorFragment.newInstance(selectedRequest.id)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerRequests.adapter = adapter
        apiService = ApiClient.instance.create(ApiService::class.java)

        loadMaintenanceRequests()

        fabRefresh.setOnClickListener { loadMaintenanceRequests() }

        // Filtering logic (null-safe)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val selectedStatus = spinnerStatus.selectedItem?.toString() ?: ""
                adapter.filter(s.toString(), selectedStatus)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedStatus = parent.getItemAtPosition(position)?.toString() ?: ""
                val searchQuery = etSearch.text?.toString() ?: ""
                adapter.filter(searchQuery, selectedStatus)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        return view
    }

    private fun loadMaintenanceRequests() {
        progressBar.visibility = View.VISIBLE
        Log.d("API_DEBUG", "Fetching maintenance requests...")

        apiService.getAllMaintenance().enqueue(object : retrofit2.Callback<MaintenanceResponse> {
            override fun onResponse(
                call: retrofit2.Call<MaintenanceResponse>,
                response: retrofit2.Response<MaintenanceResponse>
            ) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body() != null) {
                    val maintenanceList = response.body()!!.data

                    val requestList: List<MaintenanceRequest> = maintenanceList.map { item ->
                        MaintenanceRequest(
                            id = item.id ?: "",
                            clientName = item.clientName ?: item.title ?: "",
                            email = "",
                            description = item.description,
                            status = item.status,
                            imageUrl = item.imageUrl
                        )
                    }

                    adapter.updateData(requestList)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to fetch data: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("API_ERROR", "Response code: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<MaintenanceResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Error loading requests: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("API_FAILURE", "Error: ${t.message}")
            }
        })
    }
}