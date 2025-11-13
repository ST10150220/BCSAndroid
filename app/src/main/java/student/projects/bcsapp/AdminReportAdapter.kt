package student.projects.bcsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminReportAdapter(
    private val reportList: List<AdminReport>
) : RecyclerView.Adapter<AdminReportAdapter.ReportViewHolder>() {

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvReportType: TextView = itemView.findViewById(R.id.tvReportType)
        val tvCreatedAt: TextView = itemView.findViewById(R.id.tvCreatedAt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reportList[position]
        holder.tvTitle.text = report.title
        holder.tvDescription.text = report.description
        holder.tvReportType.text = report.reportType
        holder.tvCreatedAt.text = report.createdAt?.toDate().toString()
    }

    override fun getItemCount(): Int = reportList.size
}
