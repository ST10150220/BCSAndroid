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
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.google.firebase.firestore.FirebaseFirestore

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var chart: HorizontalBarChart
    private lateinit var btnRegister: Button
    private lateinit var btnReport: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        btnRegister = findViewById<Button>(R.id.btnRegisterUser)
        btnReport = findViewById<Button>(R.id.btnReport)
        chart = findViewById<HorizontalBarChart>(R.id.chart)

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
                    BarEntry(0f, pendingCount.toFloat()),
                    BarEntry(1f, approvedCount.toFloat())
                )

                val dataSet = BarDataSet(entries, "Maintenance Requests")
                dataSet.color = Color.parseColor("#4CAF50")
                dataSet.valueTextColor = Color.BLACK
                dataSet.valueTextSize = 12f

                val barData = BarData(dataSet)
                barData.barWidth = 0.5f

                chart.data = barData

                val labels = listOf("Pending", "Approved")
                val xAxis = chart.xAxis
                xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.textSize = 12f
                xAxis.labelCount = labels.size

                chart.axisRight.isEnabled = false
                chart.description.isEnabled = false
                chart.legend.isEnabled = true
                chart.setFitBars(true)
                chart.animateY(1500)
                chart.invalidate()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
}
