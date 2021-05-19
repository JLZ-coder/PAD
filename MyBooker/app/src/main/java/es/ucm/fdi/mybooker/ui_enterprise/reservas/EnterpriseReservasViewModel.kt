package es.ucm.fdi.mybooker.ui_enterprise.reservas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import es.ucm.fdi.mybooker.objects.itemReserve
import java.util.*

class EnterpriseReservasViewModel : ViewModel() {

    private var db = FirebaseFirestore.getInstance()
    var mAuth = FirebaseAuth.getInstance()
    private val userId = mAuth.currentUser.uid

    private val _title = MutableLiveData<String>().apply {
        value = "Nombre empresa"
    }
    val title: LiveData<String> = _title

    private val _n_reservas = MutableLiveData<String>().apply {
        value = "Aún no hay reservas para hoy"
    }
    val n_reservas: LiveData<String> = _n_reservas

    var options : FirestoreRecyclerOptions<itemReserve>? = null

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

                        _title.value = name
                    }
                }
            }

        val today = Calendar.getInstance(TimeZone.getTimeZone("UTC+2"))
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0);
        val start_of_day = today.time
        today.add(Calendar.DATE, 1)
        val end_of_day = today.time

        val query: Query = db.collection("reserves").whereGreaterThanOrEqualTo("hora", start_of_day)
            .whereLessThan("hora", end_of_day).whereEqualTo("id_enterprise", userId)
        options = FirestoreRecyclerOptions.Builder<itemReserve>().setQuery(query, itemReserve::class.java).build()
    }
}