package student.projects.bcsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import network.Maintenance

class MaintenanceAdapter : ListAdapter<Maintenance, MaintenanceAdapter.MaintenanceViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaintenanceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_maintenance_request, parent, false)
        return MaintenanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaintenanceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MaintenanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvClient: TextView = itemView.findViewById(R.id.tvClient)
        private val tvContractor: TextView = itemView.findViewById(R.id.tvContractor)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val ivThumbnail: ImageView = itemView.findViewById(R.id.ivThumbnail)

        fun bind(maintenance: Maintenance) {
            tvTitle.text = maintenance.description ?: "No description"
            tvClient.text = "Client: ${maintenance.clientName ?: "N/A"}"
            tvContractor.text = "Contractor: ${maintenance.contractorName ?: "N/A"}" // Changed to assignedContractor
            tvStatus.text = "Status: ${maintenance.status ?: "N/A"}"

            // Load image if available
            if (!maintenance.imageUrl.isNullOrEmpty()) {
                Glide.with(itemView)
                    .load(maintenance.imageUrl)  // Use the provided image URL
                    .into(ivThumbnail)
            } else {
                ivThumbnail.setImageResource(R.drawable.placeholer_image)  // Optional placeholder image
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Maintenance>() {
        override fun areItemsTheSame(oldItem: Maintenance, newItem: Maintenance): Boolean {
            return oldItem.id == newItem.id  // Use a unique identifier for comparing items
        }

        override fun areContentsTheSame(oldItem: Maintenance, newItem: Maintenance): Boolean {
            return oldItem == newItem
        }
    }
}

