package student.projects.bcsapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class AssignContractorFragment : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var tvClientName: TextView
    private lateinit var tvDescription: TextView
    private lateinit var etContractor: EditText
    private lateinit var btnAssign: Button
    private lateinit var progressBar: ProgressBar

    private var maintenanceId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_assign_contractor, container, false)

        firestore = FirebaseFirestore.getInstance()

        tvClientName = view.findViewById(R.id.tvClientName)
        tvDescription = view.findViewById(R.id.tvDescription)
        etContractor = view.findViewById(R.id.etContractor)
        btnAssign = view.findViewById(R.id.btnAssign)
        progressBar = view.findViewById(R.id.progressBarAssign)

        maintenanceId = arguments?.getString("maintenanceId")

        if (maintenanceId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please select a Request from the previous page first", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return view
        }

        loadMaintenanceDetails(maintenanceId!!)

        btnAssign.setOnClickListener {
            val contractorName = etContractor.text?.toString()?.trim()
            if (contractorName.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please enter a contractor name", Toast.LENGTH_SHORT).show()
            } else {
                assignContractor(maintenanceId!!, contractorName)
            }
        }

        return view
    }

    private fun loadMaintenanceDetails(id: String) {
        progressBar.visibility = View.VISIBLE

        firestore.collection("maintenanceRequests").document(id)
            .get()
            .addOnSuccessListener { doc ->
                progressBar.visibility = View.GONE
                if (doc.exists()) {
                    tvClientName.text = doc.getString("clientName") ?: "N/A"
                    tvDescription.text = doc.getString("description") ?: "No description available"
                } else {
                    Toast.makeText(requireContext(), "Request not found", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Log.e("FIRESTORE_ERROR", "Error loading details", e)
                Toast.makeText(requireContext(), "Error loading details", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
    }

    private fun assignContractor(documentId: String, contractorName: String) {
        progressBar.visibility = View.VISIBLE

        val updates = mapOf(
            "assignedContractor" to contractorName,
            "status" to "Assigned"
        )

        firestore.collection("maintenanceRequests").document(documentId)
            .update(updates)
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Contractor assigned successfully!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener { e ->
                progressBar.visibility = View.GONE
                Log.e("FIRESTORE_UPDATE_ERROR", e.message ?: "Unknown error")
                Toast.makeText(requireContext(), "Failed to assign: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        fun newInstance(maintenanceId: String): AssignContractorFragment {
            val fragment = AssignContractorFragment()
            val args = Bundle()
            args.putString("maintenanceId", maintenanceId)
            fragment.arguments = args
            return fragment
        }
    }
}