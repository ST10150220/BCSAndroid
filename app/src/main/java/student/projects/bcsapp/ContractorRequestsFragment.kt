package student.projects.bcsapp.contractor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import network.MaintenanceRequest
import student.projects.bcsapp.R

class ContractorRequestsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContractorRequestsAdapter
    private val requests = mutableListOf<MaintenanceRequest>()
    private lateinit var tvNoRequests: TextView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contractor_requests, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewContractorRequests)
        tvNoRequests = view.findViewById(R.id.tvNoRequests)

        adapter = ContractorRequestsAdapter(requests) { selectedRequest ->
            // 🔹 Show update status dialog
            val dialog = ContractorUpdateStatusDialog().apply {
                arguments = Bundle().apply {
                    putString("requestId", selectedRequest.id)
                    putString("clientName", selectedRequest.clientName)
                    putString("description", selectedRequest.description)
                    putString("currentStatus", selectedRequest.status)
                }
            }
            dialog.show(parentFragmentManager, "UpdateStatusDialog")
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        loadContractorRequests()
        return view
    }

    private fun loadContractorRequests() {
        val sharedPref = requireContext().getSharedPreferences("UserPrefs", AppCompatActivity.MODE_PRIVATE)
        val contractorName = sharedPref.getString("userName", null)

        if (contractorName == null) {
            tvNoRequests.text = "No contractor name found."
            tvNoRequests.visibility = View.VISIBLE
            return
        }

        db.collection("maintenanceRequests")
            .whereEqualTo("assignedContractor", contractorName)
            .get()
            .addOnSuccessListener { documents ->
                requests.clear()
                for (doc in documents) {
                    val request = doc.toObject(MaintenanceRequest::class.java)
                    request.id = doc.id
                    requests.add(request)
                }
                adapter.notifyDataSetChanged()
                tvNoRequests.visibility = if (requests.isEmpty()) View.VISIBLE else View.GONE
            }
            .addOnFailureListener { e ->
                tvNoRequests.text = "Error loading requests: ${e.message}"
                tvNoRequests.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Failed to load requests: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
