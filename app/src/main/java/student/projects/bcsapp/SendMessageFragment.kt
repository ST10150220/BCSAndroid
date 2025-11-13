package network

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import model.User
import network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import student.projects.bcsapp.R

class SendMessageFragment : Fragment() {

    private lateinit var lvUsers: ListView
    private lateinit var etSubject: EditText
    private lateinit var etBody: EditText
    private lateinit var btnSend: Button
    private lateinit var tvSelectedUser: TextView

    private val firestore = FirebaseFirestore.getInstance()
    private var usersListener: ListenerRegistration? = null
    private val userList = mutableListOf<User>()
    private var selectedUser: User? = null

    private val TAG = "SendMessageFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_send_message, container, false)

        lvUsers = view.findViewById(R.id.lvUsers)
        etSubject = view.findViewById(R.id.etSubject)
        etBody = view.findViewById(R.id.etBody)
        btnSend = view.findViewById(R.id.btnSend)
        tvSelectedUser = view.findViewById(R.id.tvSelectedUser)

        fetchUsersFromFirestore()
        btnSend.setOnClickListener { sendMessage() }

        return view
    }

    private fun fetchUsersFromFirestore() {
        Log.d(TAG, "Fetching users...")
        usersListener = firestore.collection("Users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error fetching users: ${error.message}")
                    Toast.makeText(requireContext(), "Error fetching users", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot == null || snapshot.isEmpty) {
                    Log.w(TAG, "No users found.")
                    Toast.makeText(requireContext(), "No users found", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                userList.clear()
                for (doc in snapshot.documents) {
                    val user = User(
                        id = doc.id,
                        name = doc.getString("name") ?: "Unknown",
                        email = doc.getString("email") ?: "",
                        role = doc.getString("role") ?: ""
                    )
                    userList.add(user)
                }

                val userNames = userList.map { "${it.name} (${it.role})" }
                lvUsers.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, userNames)

                lvUsers.setOnItemClickListener { _, _, position, _ ->
                    selectedUser = userList[position]
                    tvSelectedUser.text = "Selected: ${selectedUser?.name} (${selectedUser?.role})"
                    Log.d(TAG, "Selected user: ${selectedUser?.name}, id: ${selectedUser?.id}")
                }
            }
    }

    private fun sendMessage() {
        val subject = etSubject.text.toString().trim()
        val body = etBody.text.toString().trim()
        val senderId = FirebaseAuth.getInstance().currentUser?.uid
        val receiverId = selectedUser?.id

        if (senderId == null) {
            Toast.makeText(requireContext(), "You must be logged in", Toast.LENGTH_SHORT).show()
            return
        }

        if (receiverId == null) {
            Toast.makeText(requireContext(), "Select a user first", Toast.LENGTH_SHORT).show()
            return
        }

        if (subject.isBlank() || body.isBlank()) {
            Toast.makeText(requireContext(), "Subject and body required", Toast.LENGTH_SHORT).show()
            return
        }

        val request = ApiService.SendMessageRequest(
            senderId = senderId,
            receiverId = receiverId,
            projectId = null,
            subject = subject,
            body = body
        )

        Log.d(TAG, "Sending message to $receiverId")

        ApiClient.instance.create(ApiService::class.java)
            .sendMessage(request)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Message sent!", Toast.LENGTH_SHORT).show()
                        etSubject.text.clear()
                        etBody.text.clear()
                        selectedUser = null
                        tvSelectedUser.text = "No user selected"
                    } else {
                        Log.e(TAG, "Failed: ${response.code()} ${response.message()}")
                        Toast.makeText(requireContext(), "Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e(TAG, "Error sending message: ${t.message}", t)
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        usersListener?.remove()
    }
}