package student.projects.bcsapp.projectmanager

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import student.projects.bcsapp.MainActivity
import student.projects.bcsapp.R

class LogoutFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var logoutListener: LogoutListener? = null

    interface LogoutListener {
        fun onLogoutCancelled()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LogoutListener) {
            logoutListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_logout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to log out?")

        builder.setPositiveButton("Yes") { _, _ ->
            auth.signOut()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
            logoutListener?.onLogoutCancelled()
        }

        builder.setOnDismissListener {
            logoutListener?.onLogoutCancelled()
        }

        builder.show()
    }

    override fun onDetach() {
        super.onDetach()
        logoutListener = null
    }
}
