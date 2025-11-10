package student.projects.bcsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import network.MaintenanceRequest

class MaintenanceRequestAdapter(
    private var fullList: List<MaintenanceRequest>,
    private val itemClick: ((MaintenanceRequest) -> Unit)? = null
) : RecyclerView.Adapter<MaintenanceRequestAdapter.VH>() {

    private var filteredList: MutableList<MaintenanceRequest> = fullList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_maintenance_request, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = filteredList.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = filteredList[position]
        holder.bind(item)
        holder.itemView.setOnClickListener { itemClick?.invoke(item) }
    }

    fun updateData(newList: List<MaintenanceRequest>) {
        fullList = newList
        filteredList = fullList.toMutableList()
        notifyDataSetChanged()
    }

    fun filter(query: String, status: String) {
        val q = query.trim().lowercase()
        filteredList = fullList.filter { req ->
            val clientOrTitle = req.clientName
            val matchesStatus = status == "All" || req.status.equals(status, ignoreCase = true)
            val matchesQuery = q.isEmpty() ||
                    clientOrTitle.lowercase().contains(q) ||
                    req.description.lowercase().contains(q)
            matchesStatus && matchesQuery
        }.toMutableList()
        notifyDataSetChanged()
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvClient = itemView.findViewById<TextView>(R.id.tvClientName)
        private val tvDesc = itemView.findViewById<TextView>(R.id.tvDescription)
        private val tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)

        fun bind(req: MaintenanceRequest) {
            tvClient.text = req.clientName
            tvDesc.text = req.description
            tvStatus.text = req.status
        }
    }
}
