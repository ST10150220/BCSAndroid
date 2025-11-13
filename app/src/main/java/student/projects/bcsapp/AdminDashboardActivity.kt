package student.projects.bcsapp

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import network.SendMessageFragment

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var chart: PieChart
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        chart = findViewById(R.id.chart)

        loadChartData()

        val navView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        navView.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_messaging -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, SendMessageFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.nav_reports -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, AdminReportFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.nav_register_user -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, RegisterUserFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                R.id.nav_admin_dashboard -> {
                    supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    true
                }
                else -> false
            }
        }
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
