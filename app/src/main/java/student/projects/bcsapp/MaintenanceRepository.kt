package student.projects.bcsapp

import com.google.firebase.firestore.FirebaseFirestore
import network.MaintenanceRequest

class MaintenanceRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getClientMaintenanceRequests(clientEmail: String, onComplete: (List<MaintenanceRequest>) -> Unit) {
        db.collection("maintenanceRequests")
            .whereEqualTo("email", clientEmail)
            .get()
            .addOnSuccessListener { documents ->
                val requests = mutableListOf<MaintenanceRequest>()
                for (doc in documents) {
                    val request = doc.toObject(MaintenanceRequest::class.java)
                    requests.add(request)
                }
                onComplete(requests)
            }
            .addOnFailureListener {
                it.printStackTrace()
                onComplete(emptyList())
            }
    }
}
