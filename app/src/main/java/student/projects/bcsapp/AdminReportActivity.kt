package student.projects.bcsapp

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class AdminReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_report) // your XML file name

        val chart = findViewById<HorizontalBarChart>(R.id.ganttChart)

        // Example data: each bar represents a task duration
        val entries = listOf(
            BarEntry(0f, 5f), // Task 1 (5 hours)
            BarEntry(1f, 8f), // Task 2 (8 hours)
            BarEntry(2f, 3f), // Task 3 (3 hours)
            BarEntry(3f, 6f)  // Task 4 (6 hours)
        )

        val dataSet = BarDataSet(entries, "Tasks Completed")
        dataSet.color = Color.parseColor("#4CAF50") // green bars
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 12f

        val barData = BarData(dataSet)
        barData.barWidth = 0.5f

        // Assign data to chart
        chart.data = barData

        // Custom X-axis labels
        val tasks = listOf("Task A", "Task B", "Task C", "Task D")
        val xAxis = chart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(tasks)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.textSize = 12f
        xAxis.labelCount = tasks.size

        // Remove extra chart styling for simplicity
        chart.axisRight.isEnabled = false
        chart.description.isEnabled = false
        chart.legend.isEnabled = true
        chart.setFitBars(true)
        chart.animateY(1500)

        chart.invalidate() // refresh chart
    }
}
