package student.projects.bcsapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import network.ApiClient
import network.ApiService
import network.MaintenanceRequest
import network.MaintenanceResponse
import student.projects.bcsapp.databinding.FragmentRequestFormBinding
import retrofit2.Response
import retrofit2.Call
import retrofit2.Callback

class RequestFormFragment : Fragment() {

    private var _binding: FragmentRequestFormBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ClientRequestAdapter
    private var clientName: String = ""

    companion object {
        private const val ARG_CLIENT_NAME = "client_name"

        fun newInstance(clientName: String): RequestFormFragment {
            val fragment = RequestFormFragment()
            val bundle = Bundle()
            bundle.putString(ARG_CLIENT_NAME, clientName)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clientName = arguments?.getString(ARG_CLIENT_NAME) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView adapter
        adapter = ClientRequestAdapter(emptyList())
        binding.requestsRecyclerView.adapter = adapter

        // Load the client's existing requests
        fetchClientRequests()

        // Keep all existing submit button logic intact
        binding.submitButton.setOnClickListener {
            val clientName = binding.clientNameEditText.text.toString().trim()
            val description = binding.descriptionEditText.text.toString().trim()
            val imageUrl = binding.imageUrlEditText.text.toString().trim()

            if (clientName.isEmpty() || description.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = MaintenanceRequest(
                clientName = clientName,
                description = description,
                imageUrl = imageUrl
            )

            submitRequest(request)
        }

    }

    private fun submitRequest(request: MaintenanceRequest) {
        Log.d("API_DEBUG", "Submit button clicked")
        Log.d("API_DEBUG", "Creating API call for request: $request")

        val apiService = ApiClient.instance.create(ApiService::class.java)
        val call = apiService.createRequest(request)

        call.enqueue(object : retrofit2.Callback<MaintenanceResponse> {
            override fun onResponse(
                call: retrofit2.Call<MaintenanceResponse>,
                response: retrofit2.Response<MaintenanceResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && responseBody.success) {
                        val newRequest = responseBody.data.firstOrNull()
                        Toast.makeText(requireContext(), "Request submitted successfully!", Toast.LENGTH_SHORT).show()
                        clearForm()

                        // Optionally add the new request to RecyclerView or refresh the list
                        fetchClientRequests()
                    } else {
                        Toast.makeText(requireContext(), "Error submitting request", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("API_FAILURE", "Unsuccessful response: ${response.code()}")
                    Toast.makeText(requireContext(), "API Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<MaintenanceResponse>, t: Throwable) {
                Log.e("API_FAILURE", "API call failed: ${t.message}", t)
                Toast.makeText(requireContext(), "API call failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun fetchClientRequests() {
        lifecycleScope.launch {
            try {
                // Get the Call object
                val call: retrofit2.Call<MaintenanceResponse> = ApiClient.instance
                    .create(ApiService::class.java)
                    .getAllMaintenance()            // Execute the call to get the response object
                val response = call.execute() // synchronous execution, safe inside coroutine

                // Now you can safely log the details of the response
                Log.d("ClientDashboard", "Response code: ${response.code()}")
                Log.d("ClientDashboard", "Response body: ${response.body()}")
                Log.d("ClientDashboard", "Response message: ${response.message()}")
                if (!response.isSuccessful) {
                    Log.d("ClientDashboard", "Response error: ${response.errorBody()?.string()}")
                }


                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()?.data ?: emptyList()

                    // Filter only this client's requests
                    val clientRequests = data.filter { it.clientName == clientName }

                    // Update the adapter
                    adapter.updateData(clientRequests)
                } else {
                    Toast.makeText(requireContext(), "Failed to load requests", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "Failed to load requests: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun clearForm() {
        binding.descriptionEditText.text?.clear()
        binding.imageUrlEditText.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
