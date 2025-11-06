package student.projects.bcsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import network.MaintenanceRequest


class MaintenanceRequestAdapter(
    private val requests: List<MaintenanceRequest>
) : RecyclerView.Adapter<MaintenanceRequestAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.txtClientName)
        val description: TextView = itemView.findViewById(R.id.txtDescription)
        val status: TextView = itemView.findViewById(R.id.txtStatus)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_maintenance_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = requests[position]
        holder.title.text = request.clientName
        holder.description.text = request.description
        holder.status.text = request.status
    }

    override fun getItemCount(): Int = requests.size
}