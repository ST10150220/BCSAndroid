package student.projects.bcsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterUserFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var spinnerRole: Spinner
    private lateinit var btnRegister: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register_user, container, false)

        etName = view.findViewById(R.id.etName)
        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        spinnerRole = view.findViewById(R.id.spinnerRole)
        btnRegister = view.findViewById(R.id.btnRegister)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        spinnerRole.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Select Role", "Project Manager", "Client", "Contractor")
        )

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val role = spinnerRole.selectedItem.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role == "Select Role") {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val userId = result.user?.uid
                    if (userId != null) {
                        val user = hashMapOf(
                            "name" to name,
                            "email" to email,
                            "role" to role
                        )
                        db.collection("Users").document(userId).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "User Registered Successfully", Toast.LENGTH_SHORT).show()
                                clearFields()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Auth Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        return view
    }

    private fun clearFields() {
        etName.text.clear()
        etEmail.text.clear()
        etPassword.text.clear()
        spinnerRole.setSelection(0)
    }
}
