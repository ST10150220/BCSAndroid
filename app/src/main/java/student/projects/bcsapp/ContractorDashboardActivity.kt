package student.projects.bcsapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import student.projects.bcsapp.contractor.ContractorRequestsFragment
import student.projects.bcsapp.contractor.ContractorUploadFragment

class ContractorDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contractor_dashboard)

        val tvContractorName = findViewById<TextView>(R.id.tvContractorName)
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val contractorName = sharedPref.getString("userName", "Contractor")
        tvContractorName.text = contractorName

        val bottomNav = findViewById<BottomNavigationView>(R.id.contractor_bottom_nav)

        loadFragment(ContractorRequestsFragment())

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_view_requests -> {
                    loadFragment(ContractorRequestsFragment())
                    true
                }
                R.id.nav_upload_report -> {
                    loadFragment(ContractorUploadFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.contractor_fragment_container, fragment)
            .commit()
    }
}
