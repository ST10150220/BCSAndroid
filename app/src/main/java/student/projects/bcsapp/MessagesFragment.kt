package student.projects.bcsapp.ui.messages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import network.ApiService
import network.ApiService.Message
import network.MessagesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import student.projects.bcsapp.R

class MessagesFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private val apiService = ApiClient.instance.create(ApiService::class.java)
    private val messages = mutableListOf<Message>()
    private lateinit var adapter: ArrayAdapter<String>

    private var userId: String = ""

    companion object {
        fun newInstance(): MessagesFragment {
            return MessagesFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_messages, container, false)
        listView = view.findViewById(R.id.lvMessages)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmpty = view.findViewById(R.id.tvEmpty)

        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        // Get logged-in user ID from FirebaseAuth
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userId.isEmpty()) {
            tvEmpty.text = "User not logged in"
            tvEmpty.visibility = View.VISIBLE
        } else {
            fetchMessages()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val msg = messages[position]
            Toast.makeText(requireContext(), "Opening: ${msg.subject}", Toast.LENGTH_SHORT).show()
            markAsRead(msg)
        }

        return view
    }

    private fun fetchMessages() {
        progressBar.visibility = View.VISIBLE
        apiService.getUserMessages(userId).enqueue(object : Callback<MessagesResponse> {
            override fun onResponse(call: Call<MessagesResponse>, response: Response<MessagesResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    messages.clear()
                    messages.addAll(response.body()!!.data) // <- use .data
                    updateList()
                } else {
                    tvEmpty.text = "Failed to load messages"
                    tvEmpty.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<MessagesResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Log.e("MessagesFragment", "Error loading messages", t)
                tvEmpty.text = "Error loading messages"
                tvEmpty.visibility = View.VISIBLE
            }
        })
    }

    private fun updateList() {
        if (messages.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
        } else {
            tvEmpty.visibility = View.GONE
            adapter.clear()
            adapter.addAll(messages.map {
                val status = if (it.isRead) "✓" else "•"
                "$status ${it.subject} - ${it.body.take(40)}..."
            })
            adapter.notifyDataSetChanged()
        }
    }

    private fun markAsRead(message: Message) {
        if (message.isRead) return

        apiService.markMessageRead(message.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    message.isRead = true
                    updateList()
                    Log.d("MessagesFragment", "Marked as read: ${message.id}")
                } else {
                    Log.e("MessagesFragment", "Failed to mark as read: ${response.code()}")
                    Toast.makeText(requireContext(), "Failed to mark as read", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("MessagesFragment", "Error marking read", t)
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
