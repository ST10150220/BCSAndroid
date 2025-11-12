package student.projects.bcsapp.projectmanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import student.projects.bcsapp.R
import student.projects.bcsapp.Report

class ReportsAdapter(
    private val reports: List<Report>,
    private val onClick: (Report) -> Unit
) : RecyclerView.Adapter<ReportsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTaskName: TextView = view.findViewById(R.id.tvTaskName)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnUpdate: Button = view.findViewById(R.id.btnUpdate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = reports.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report = reports[position]
        holder.tvTaskName.text = report.taskName
        holder.tvStatus.text = "Status: ${report.status}"

        holder.btnUpdate.setOnClickListener { view ->
            // Create a popup menu
            val popup = android.widget.PopupMenu(view.context, view)
            popup.menu.add("Approved")
            popup.menu.add("Pending")
            popup.menu.add("Completed")

            popup.setOnMenuItemClickListener { menuItem ->
                val newStatus = menuItem.title.toString()
                onClick(report.copy(status = newStatus))
                true
            }
            popup.show()
        }
    }

}
