package student.projects.bcsapp.projectmanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.TextView
import android.widget.Toast
import student.projects.bcsapp.Report
import student.projects.bcsapp.R

class ProjectManagerUpdateReportFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: ReportsAdapter
    private val reports = mutableListOf<Report>()
    private val filteredReports = mutableListOf<Report>()

    private lateinit var tvNoResults: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvReports = view.findViewById<RecyclerView>(R.id.rvReports)
        val searchView = view.findViewById<SearchView>(R.id.searchViewReports)
        tvNoResults = view.findViewById(R.id.tvNoResults)

        rvReports.layoutManager = LinearLayoutManager(requireContext())
        adapter = ReportsAdapter(filteredReports) { report ->
            updateReportStatus(report)
        }
        rvReports.adapter = adapter

        loadReports()

        // 🔹 Setup search filter
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterReports(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterReports(newText)
                return true
            }
        })
    }

    private fun loadReports() {
        db.collection("reports")
            .get()
            .addOnSuccessListener { snapshot ->
                reports.clear()
                for (doc in snapshot.documents) {
                    val report = doc.toObject(Report::class.java)
                    if (report != null) {
                        report.id = doc.id
                        reports.add(report)
                    }
                }
                // Show all reports initially
                filteredReports.clear()
                filteredReports.addAll(reports)
                adapter.notifyDataSetChanged()
                toggleNoResults()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to load reports: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun updateReportStatus(report: Report) {
        db.collection("reports").document(report.id)
            .update("status", report.status)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Status updated to ${report.status}", Toast.LENGTH_SHORT).show()
                loadReports()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to update: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun filterReports(query: String?) {
        filteredReports.clear()
        if (query.isNullOrBlank()) {
            filteredReports.addAll(reports)
        } else {
            val lowerQuery = query.lowercase()
            filteredReports.addAll(reports.filter { it.taskName.lowercase().contains(lowerQuery) })
        }
        adapter.notifyDataSetChanged()
        toggleNoResults()
    }

    private fun toggleNoResults() {
        tvNoResults.visibility = if (filteredReports.isEmpty()) View.VISIBLE else View.GONE
    }
}
