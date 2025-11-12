package student.projects.bcsapp.contractor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import network.MaintenanceRequest
import student.projects.bcsapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ContractorRequestsAdapter(
    private val requests: List<MaintenanceRequest>,
    private val onClick: (MaintenanceRequest) -> Unit
) : RecyclerView.Adapter<ContractorRequestsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvClientName: TextView = view.findViewById(R.id.tvClientName)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvCreatedAt: TextView = view.findViewById(R.id.tvCreatedAt)
        val imageRequest: ImageView = view.findViewById(R.id.imageRequest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contractor_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val req = requests[position]
        holder.tvClientName.text = req.clientName
        holder.tvDescription.text = req.description
        holder.tvStatus.text = "Status: ${req.status}"
        val createdAtFormatted = when (val dateValue = req.createdAt) {
            is String -> dateValue
            is Map<*, *> -> {
                val seconds = (dateValue["_seconds"] as? Number)?.toLong() ?: 0L
                val date = Date(seconds * 1000)
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(date)
            }
            else -> "Unknown"
        }

        holder.tvCreatedAt.text = createdAtFormatted


        if (!req.imageUrl.isNullOrEmpty()) {
            holder.imageRequest.visibility = View.VISIBLE
            Glide.with(holder.itemView.context).load(req.imageUrl).into(holder.imageRequest)
        } else {
            holder.imageRequest.visibility = View.GONE
        }

        // 🔹 Handle click to open ContractorUpdateStatusFragment
        holder.itemView.setOnClickListener { onClick(req) }
    }

    override fun getItemCount() = requests.size
}
