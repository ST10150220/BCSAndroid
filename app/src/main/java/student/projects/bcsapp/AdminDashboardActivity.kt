package student.projects.bcsapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.charts.PieChart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var chart: PieChart
    private lateinit var btnRegister: Button
    private lateinit var btnReport: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        btnRegister = findViewById<Button>(R.id.btnRegisterUser)
        btnReport = findViewById<Button>(R.id.btnReport)
        chart = findViewById<PieChart>(R.id.chart)

        val db = FirebaseFirestore.getInstance()


        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterUserActivity::class.java))
            finish()
        }

        btnReport.setOnClickListener {
            startActivity((Intent(this, AdminReportActivity::class.java)))
            finish()
        }

        loadChartData()
    }

    private fun loadChartData() {
        db.collection("maintenanceRequests")
            .get()
            .addOnSuccessListener { documents ->
                var pendingCount = 0
                var approvedCount = 0

                for (document in documents) {
                    val status = document.getString("status")
                    when (status) {
                        "Pending" -> pendingCount++
                        "Approved" -> approvedCount++
                    }
                }

                val entries = listOf(
                    com.github.mikephil.charting.data.PieEntry(pendingCount.toFloat(), "Pending"),
                    com.github.mikephil.charting.data.PieEntry(approvedCount.toFloat(), "Approved")
                )

                val dataSet = com.github.mikephil.charting.data.PieDataSet(entries, "Maintenance Requests")
                dataSet.colors = listOf(
                    Color.parseColor("#FFC107"),
                    Color.parseColor("#4CAF50")
                )
                dataSet.valueTextColor = Color.BLACK
                dataSet.valueTextSize = 14f

                val pieData = com.github.mikephil.charting.data.PieData(dataSet)

                chart.data = pieData
                chart.setUsePercentValues(true)
                chart.description.isEnabled = false
                chart.isDrawHoleEnabled = true
                chart.setHoleColor(Color.WHITE)
                chart.setEntryLabelColor(Color.BLACK)
                chart.setEntryLabelTextSize(12f)
                chart.centerText = "Requests Overview"
                chart.animateY(1200)
                chart.invalidate()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
}
