package student.projects.bcsapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.firebase.firestore.FirebaseFirestore
import network.MaintenanceRequest

class AdminHomeFragment : Fragment() {

    private val maintenanceRequests = mutableListOf<MaintenanceRequest>()
    private lateinit var adapter: MaintenanceRequestAdapter
    private lateinit var chart: PieChart
    private lateinit var rvMaintenanceRequests: RecyclerView
    private lateinit var spinnerFilter: Spinner
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.admin_home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chart = view.findViewById(R.id.chart)
        rvMaintenanceRequests = view.findViewById(R.id.rvMaintenanceRequests)
        spinnerFilter = view.findViewById(R.id.spinnerFilter)

        setupRecyclerView()
        setupFilter()
        loadMaintenanceRequests()
    }

    private fun setupRecyclerView() {
        adapter = MaintenanceRequestAdapter(maintenanceRequests)
        rvMaintenanceRequests.layoutManager = LinearLayoutManager(requireContext())
        rvMaintenanceRequests.adapter = adapter
    }

    private fun setupFilter() {
        val statuses = listOf("All", "Assigned", "Pending", "In Progress", "Completed")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = spinnerAdapter

        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = statuses[position]
                filterRequests(selected)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadMaintenanceRequests() {
        db.collection("maintenanceRequests")
            .get()
            .addOnSuccessListener { result ->
                maintenanceRequests.clear()
                for (doc in result) {
                    val request = doc.toObject(MaintenanceRequest::class.java)
                    request.id = doc.id
                    maintenanceRequests.add(request)
                }
                adapter.updateData(maintenanceRequests)
                updatePieChart(maintenanceRequests)
            }
            .addOnFailureListener { e ->
                // Handle errors here
            }
    }

    private fun filterRequests(status: String) {
        val filtered = if (status == "All") {
            maintenanceRequests
        } else {
            maintenanceRequests.filter { it.status == status }
        }
        adapter.updateData(filtered)
        updatePieChart(filtered)
    }

    private fun updatePieChart(requests: List<MaintenanceRequest>) {
        val statusCounts = requests.groupingBy { it.status }.eachCount()

        val entries = mutableListOf<PieEntry>()
        for ((status, count) in statusCounts) {
            entries.add(PieEntry(count.toFloat(), status)) // the status will only be used in legend
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            Color.parseColor("#FFB74D"), // Pending
            Color.parseColor("#4DB6AC"), // Assigned/In Progress
            Color.parseColor("#BA68C8"), // Completed
            Color.parseColor("#64B5F6")  // Other
        )
        dataSet.valueTextColor = Color.DKGRAY
        dataSet.valueTextSize = 14f
        dataSet.sliceSpace = 2f
        dataSet.setDrawValues(true) // show values on slices
        dataSet.valueFormatter = PercentFormatter() // show values as percentages
        dataSet.valueFormatter = PercentFormatter(chart) // ensure it formats as percent of total
        dataSet.valueLinePart1OffsetPercentage = 80f
        dataSet.valueLinePart1Length = 0.2f
        dataSet.valueLinePart2Length = 0.4f
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE // optional: outside for clarity

        val data = PieData(dataSet)
        chart.data = data

        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.isDrawHoleEnabled = true
        chart.holeRadius = 40f
        chart.transparentCircleRadius = 45f
        chart.setHoleColor(Color.WHITE)
        chart.setTransparentCircleColor(Color.LTGRAY)
        chart.setTransparentCircleAlpha(80)
        chart.centerText = "" // remove center text
        chart.setCenterTextSize(0f)

        // Legend
        val legend = chart.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.textColor = Color.DKGRAY
        legend.textSize = 12f
        legend.isWordWrapEnabled = true

        chart.animateY(1200)
        chart.invalidate()
    }


}
