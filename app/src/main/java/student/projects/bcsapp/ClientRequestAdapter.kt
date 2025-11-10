package student.projects.bcsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import network.Maintenance

class ClientRequestAdapter(private var requests: List<Maintenance>) :
    RecyclerView.Adapter<ClientRequestAdapter.RequestViewHolder>() {

    class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvCreatedAt: TextView = itemView.findViewById(R.id.tvCreatedAt) // NEW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_client_request, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = requests[position]
        holder.tvTitle.text = request.title ?: "Maintenance Request"
        holder.tvDescription.text = request.description ?: "No description"
        holder.tvStatus.text = "Status: ${request.status ?: "Unknown"}"
        holder.tvCreatedAt.text = request.createdAt?.let { "Created: $it" } ?: ""
    }

    override fun getItemCount() = requests.size

    fun updateData(newRequests: List<Maintenance>) {
        requests = newRequests
        notifyDataSetChanged()
    }
}
