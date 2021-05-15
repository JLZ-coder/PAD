package es.ucm.fdi.mybooker.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import es.ucm.fdi.mybooker.objects.itemShift

class HomeViewModel : ViewModel() {

    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()
    private val userId = mAuth.currentUser.uid
    //private val userId = "sXSFYya07ZZPZwNciwSd2i9tnxf1"

    private val _empty_recycler_text = MutableLiveData<String>().apply {
        value = "No hay ningún turno"
    }
    val empty_recycler_text: LiveData<String> = _empty_recycler_text

    val query: Query = db.collection("shifts").whereEqualTo("id_enterprise", userId)
    val options = FirestoreRecyclerOptions.Builder<itemShift>().setQuery(query, itemShift::class.java).build()

    init {
        setUp()
    }

    private fun setUp() {

    }
}