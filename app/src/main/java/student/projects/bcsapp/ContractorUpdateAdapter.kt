package student.projects.bcsapp.contractor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import network.Maintenance
import student.projects.bcsapp.R

class ContractorUpdateAdapter(
    private val list: List<Maintenance>,
    private val onClick: (Maintenance) -> Unit
) : RecyclerView.Adapter<ContractorUpdateAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvClientName: TextView = itemView.findViewById(R.id.tvClientName)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_maintenance_request, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvClientName.text = item.clientName ?: "No Name"
        holder.tvDescription.text = item.description
        holder.tvStatus.text = item.status
        holder.itemView.setOnClickListener { onClick(item) }
    }
}
