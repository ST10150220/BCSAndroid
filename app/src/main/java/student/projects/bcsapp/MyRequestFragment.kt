package student.projects.bcsapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import network.MaintenanceRequest

class MyRequestsFragment : Fragment() {

    private lateinit var adapter: MaintenanceRequestAdapter
    private val repository = MaintenanceRepository()
    private val requests = mutableListOf< MaintenanceRequest>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewRequests)
        val tvNoRequests = view.findViewById<TextView>(R.id.tvNoRequests)

        adapter = MaintenanceRequestAdapter(requests)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Get client email from SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val clientEmail = sharedPref.getString("userEmail", "") ?: ""

        repository.getClientMaintenanceRequests(clientEmail) { list ->
            requests.clear()
            requests.addAll(list)
            adapter.notifyDataSetChanged()

            // Show/hide "No requests" message
            tvNoRequests.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }
    }

}
