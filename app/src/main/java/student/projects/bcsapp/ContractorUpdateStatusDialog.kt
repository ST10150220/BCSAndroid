package student.projects.bcsapp.contractor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import network.ApiClient
import network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import student.projects.bcsapp.R

class ContractorUpdateStatusDialog : DialogFragment() {

    private lateinit var tvClientName: TextView
    private lateinit var tvDescription: TextView
    private lateinit var spinnerStatus: Spinner
    private lateinit var btnUpdateStatus: Button
    private lateinit var btnBack: ImageButton
    private val apiService = ApiClient.instance.create(ApiService::class.java)

    private var requestId: String? = null
    private var clientName: String? = null
    private var description: String? = null
    private var currentStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.95).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contractor_update_status, container, false)

        // 🔹 Initialize UI
        tvClientName = view.findViewById(R.id.tvClientName)
        tvDescription = view.findViewById(R.id.tvDescription)
        spinnerStatus = view.findViewById(R.id.spinnerStatus)
        btnUpdateStatus = view.findViewById(R.id.btnUpdateStatus)
        btnBack = view.findViewById(R.id.btnBack)

        // 🔹 Get arguments
        requestId = arguments?.getString("requestId")
        clientName = arguments?.getString("clientName")
        description = arguments?.getString("description")
        currentStatus = arguments?.getString("currentStatus")

        // 🔹 Set text
        tvClientName.text = clientName
        tvDescription.text = description

        // 🔹 Spinner setup
        val statuses = arrayOf("Pending", "In Progress", "Completed")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = adapter

        val index = statuses.indexOf(currentStatus)
        if (index >= 0) spinnerStatus.setSelection(index)

        // 🔹 Update button
        btnUpdateStatus.setOnClickListener {
            val selectedStatus = spinnerStatus.selectedItem.toString()
            if (!requestId.isNullOrEmpty()) {
                updateMaintenanceStatus(requestId!!, selectedStatus)
            } else {
                Toast.makeText(requireContext(), "Request ID missing", Toast.LENGTH_SHORT).show()
            }
        }

        // 🔹 Back button
        btnBack.setOnClickListener {
            dismiss()
        }

        return view
    }

    private fun updateMaintenanceStatus(id: String, status: String) {
        val body = mapOf("status" to status)
        apiService.updateMaintenanceStatus(id, body).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Status updated successfully", Toast.LENGTH_SHORT).show()
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), "Failed to update status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
