package es.ucm.fdi.mybooker.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import es.ucm.fdi.mybooker.adapters.ReserveFirestoreAdapter
import es.ucm.fdi.mybooker.objects.itemReserve

class DashboardViewModel : ViewModel() {

    private var db = FirebaseFirestore.getInstance()
    var mAuth = FirebaseAuth.getInstance()
    private val userId = mAuth.currentUser.uid

    private val _title = MutableLiveData<String>().apply {
        value = "Nombre empresa"
    }
    val title: LiveData<String> = _title

    private val _n_reservas = MutableLiveData<String>().apply {
        value = "Estas reservas"
    }
    val n_reservas: LiveData<String> = _n_reservas

    val query: Query = db.collection("reserves").whereEqualTo("id_enterprise", userId)
    val options = FirestoreRecyclerOptions.Builder<itemReserve>().setQuery(query, itemReserve::class.java).build()
    val firebase_adapter = ReserveFirestoreAdapter(options)

    init {
        setUp()
    }

    private fun setUp() {
        db.collection("users").document(userId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful()) {
                    val document: DocumentSnapshot? = task.getResult()
                    if (document != null) {
                        val name = document.getString("name")
                        val email = document.getString("email")
                        val cp = document.get("cp") as Long
                        val category = document.getString("category")
                        val location = document.getString("location")

                        _title.value = name
                    } else {
                        mAuth.signOut()
                    }
                }
            }
    }
}