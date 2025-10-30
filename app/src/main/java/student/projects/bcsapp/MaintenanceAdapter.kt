package student.projects.bcsapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import network.Maintenance

class MaintenanceAdapter(
    private var maintenanceList: List<Maintenance>
) : RecyclerView.Adapter<MaintenanceAdapter.MaintenanceViewHolder>() {

    class MaintenanceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTitle: TextView = view.findViewById(R.id.txtClientName)
        val txtDescription: TextView = view.findViewById(R.id.txtDescription)
        val imgRequest: ImageView = view.findViewById(R.id.imgMaintenance)
        val txtStatus: TextView = view.findViewById(R.id.txtStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaintenanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_maintenance_request, parent, false)
        return MaintenanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaintenanceViewHolder, position: Int) {
        val item = maintenanceList[position]

        // Use clientName if exists, otherwise use title
        holder.txtTitle.text = item.clientName ?: item.title ?: "No Title"

        holder.txtDescription.text = item.description
        holder.txtStatus.text = item.status

        // Load image: prefer imageUrl, else first from images
        val image = item.imageUrl ?: item.images?.firstOrNull()
        if (image != null) {
            Glide.with(holder.itemView.context)
                .load(image)
                .into(holder.imgRequest)
        } else {
            holder.imgRequest.setImageResource(R.drawable.placeholer_image)
        }
    }

    override fun getItemCount(): Int = maintenanceList.size

    fun setData(list: List<Maintenance>) {
        maintenanceList = list
        notifyDataSetChanged()
    }
}
