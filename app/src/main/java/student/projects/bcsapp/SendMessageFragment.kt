package ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import model.User
import network.ApiService
import student.projects.bcsapp.R

class SendMessageFragment : Fragment() {

    private lateinit var etSubject: EditText
    private lateinit var etBody: EditText
    private lateinit var spinnerUsers: Spinner
    private lateinit var btnSend: Button

    private var userList = mutableListOf<User>()
    private var selectedUserId: String? = null
    private val firestore = FirebaseFirestore.getInstance()
    private var usersListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_send_message, container, false)

        etSubject = view.findViewById(R.id.etSubject)
        etBody = view.findViewById(R.id.etBody)
        spinnerUsers = view.findViewById(R.id.spinnerUsers)
        btnSend = view.findViewById(R.id.btnSend)

        fetchUsersFromFirestore() // fetch users dynamically
        btnSend.setOnClickListener { sendMessage() }

        return view
    }

    // ------------------- Firestore Fetch -------------------
    private fun fetchUsersFromFirestore() {
        usersListener = firestore.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "Error fetching users", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    userList.clear()
                    for (doc in snapshot.documents) {
                        val user = User(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            email = doc.getString("email") ?: "",
                            role = doc.getString("role") ?: ""
                        )
                        userList.add(user)
                    }
                    setupUserSpinner() // repopulate spinner
                }
            }
    }

    private fun setupUserSpinner() {
        val userNames = userList.map { "${it.name} (${it.role})" } // show role too
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, userNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUsers.adapter = adapter

        spinnerUsers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                selectedUserId = userList[position].id
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        usersListener?.remove() // remove listener to avoid memory leaks
    }

    // ------------------- Send Message (unchanged) -------------------
    private fun sendMessage() {
        val subject = etSubject.text.toString()
        val body = etBody.text.toString()
        val receiverId = selectedUserId ?: return

        if (subject.isBlank() || body.isBlank()) {
            Toast.makeText(requireContext(), "Subject and body cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val request = ApiService.SendMessageRequest(
            body = body,
            projectId = "PROJECT_ID_123", // Replace with actual project id
            receiverId = receiverId,
            subject = subject
        )

        ApiClient.instance.create(ApiService::class.java)
            .sendMessage(request)
            .enqueue(object : retrofit2.Callback<Void> {
                override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Message sent", Toast.LENGTH_SHORT).show()
                        etSubject.text.clear()
                        etBody.text.clear()
                    } else {
                        Toast.makeText(requireContext(), "Failed to send", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}