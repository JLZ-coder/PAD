package es.ucm.fdi.mybooker.ui_enterprise.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class EnterpriseProfileViewModel : ViewModel() {

    private var db = FirebaseFirestore.getInstance()
    var mAuth = FirebaseAuth.getInstance()
    private val userId = mAuth.currentUser.uid

    private val _name = MutableLiveData<String>().apply {
        value = "Nombre empresa"
    }
    val name: LiveData<String> = _name

    private val _email = MutableLiveData<String>().apply {
        value = "Email empresa"
    }
    val email: LiveData<String> = _email

    private val _category = MutableLiveData<String>().apply {
        value = "Categoria empresa"
    }
    val category: LiveData<String> = _category

    private val _location = MutableLiveData<String>().apply {
        value = "Location empresa"
    }
    val location: LiveData<String> = _location

    private val _cp = MutableLiveData<String>().apply {
        value = "CP empresa"
    }
    val cp: LiveData<String> = _cp

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

                        _name.value = name
                        _email.value = email
                        _category.value = "Categoría: " + category
                        _location.value = "Ubicación: " + location
                        _cp.value = "CP: " + cp.toString()
                    } else {
                        mAuth.signOut()
                    }
                }
            }
    }
}