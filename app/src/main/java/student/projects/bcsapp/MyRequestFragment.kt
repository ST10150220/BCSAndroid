package student.projects.bcsapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import network.Maintenance

class MyRequestsFragment : Fragment() {

    private lateinit var adapter: ClientRequestAdapter
    private val repository = MaintenanceRepository()
    private val requests = mutableListOf<Maintenance>() // changed type to Maintenance

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewRequests)
        val tvNoRequests = view.findViewById<TextView>(R.id.tvNoRequests)

        adapter = ClientRequestAdapter(requests)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Get client email from SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val clientEmail = sharedPref.getString("userEmail", "")?.trim() ?: ""
        Log.d("MyRequestsFragment", "Fetching requests for email: '$clientEmail'")

        repository.getClientMaintenanceRequests(clientEmail) { list ->
            Log.d("MyRequestsFragment", "Received ${list.size} requests")

            // Convert MaintenanceRequest -> Maintenance for the adapter
            val convertedList = list.map { request ->
                Maintenance(
                    id = request.id,
                    clientName = request.clientName,
                    title = "Maintenance Request", // or use a field from request if you have it
                    description = request.description,
                    imageUrl = request.imageUrl,
                    images = null,
                    assignedContractor = null,
                    assignedTo = null,
                    status = request.status,
                    createdAt = null,
                    updatedAt = null
                )
            }

            requests.clear()
            requests.addAll(convertedList)
            adapter.updateData(requests) // use adapter updateData function

            // Show/hide "No requests" message
            tvNoRequests.visibility = if (convertedList.isEmpty()) View.VISIBLE else View.GONE
        }

    }
}
