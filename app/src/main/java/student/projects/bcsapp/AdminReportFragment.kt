package student.projects.bcsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminReportFragment : Fragment() {

    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var etReportType: EditText
    private lateinit var btnSubmit: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_report, container, false)

        etTitle = view.findViewById(R.id.etTitle)
        etDescription = view.findViewById(R.id.etDescription)
        etReportType = view.findViewById(R.id.etReportType)
        btnSubmit = view.findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val reportType = etReportType.text.toString().trim()
            val createdById = auth.currentUser?.uid ?: "unknown"

            if (title.isEmpty() || description.isEmpty() || reportType.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all info", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val reportData = hashMapOf(
                "title" to title,
                "description" to description,
                "reportType" to reportType,
                "createdAt" to Timestamp.now(),
                "createdById" to createdById
            )

            db.collection("AdminReports").add(reportData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Report Submitted", Toast.LENGTH_SHORT).show()
                    clearFields()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Report could not be submitted", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }

    private fun clearFields() {
        etTitle.text.clear()
        etDescription.text.clear()
        etReportType.text.clear()
    }
}
