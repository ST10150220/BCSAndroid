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
    private lateinit var adapter: MessageAdapter

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

        adapter = MessageAdapter()
        listView.adapter = adapter

        // Get logged-in user ID from FirebaseAuth
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        if (userId.isEmpty()) {
            showEmptyMessage("User not logged in")
        } else {
            fetchMessages()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val msg = messages[position]

            // Show dialog with full message
            android.app.AlertDialog.Builder(requireContext())
                .setTitle(msg.subject)
                .setMessage(msg.body)
                .setPositiveButton("OK") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()
                .show()

            // Mark as read
            markAsRead(msg)

            // Show toast
            Toast.makeText(requireContext(), "Message has been read", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun fetchMessages() {
        progressBar.visibility = View.VISIBLE
        tvEmpty.visibility = View.GONE
        apiService.getUserMessages(userId).enqueue(object : Callback<MessagesResponse> {
            override fun onResponse(call: Call<MessagesResponse>, response: Response<MessagesResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    messages.clear()
                    messages.addAll(response.body()!!.data)
                    updateList()
                } else {
                    showEmptyMessage("Failed to load messages")
                }
            }

            override fun onFailure(call: Call<MessagesResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Log.e("MessagesFragment", "Error loading messages", t)
                showEmptyMessage("Error loading messages")
            }
        })
    }

    private fun updateList() {
        if (messages.isEmpty()) {
            showEmptyMessage("No messages available")
        } else {
            tvEmpty.visibility = View.GONE
            listView.visibility = View.VISIBLE
            adapter.notifyDataSetChanged()
        }
    }

    private fun showEmptyMessage(message: String) {
        tvEmpty.text = message
        tvEmpty.visibility = View.VISIBLE
        listView.visibility = View.GONE
    }

    private fun markAsRead(message: Message) {
        if (message.isRead) return

        apiService.markMessageRead(message.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    message.isRead = true
                    adapter.notifyDataSetChanged()
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

    // Custom adapter to make messages look like cards
    inner class MessageAdapter : BaseAdapter() {
        override fun getCount(): Int = messages.size
        override fun getItem(position: Int): Any = messages[position]
        override fun getItemId(position: Int): Long = position.toLong()
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(requireContext())
                .inflate(R.layout.item_message, parent, false)
            val msg = messages[position]

            val tvSubject = view.findViewById<TextView>(R.id.tvMessageSubject)
            val tvPreview = view.findViewById<TextView>(R.id.tvMessagePreview)

            tvSubject.text = msg.subject
            tvPreview.text = msg.body.take(60) + if (msg.body.length > 60) "..." else ""

            // Gray out read messages
            if (msg.isRead) {
                tvSubject.setTextColor(0xFF94A3B8.toInt())
                tvPreview.setTextColor(0xFF94A3B8.toInt())
            } else {
                tvSubject.setTextColor(0xFF1E293B.toInt())
                tvPreview.setTextColor(0xFF475569.toInt())
            }

            return view
        }
    }
}
