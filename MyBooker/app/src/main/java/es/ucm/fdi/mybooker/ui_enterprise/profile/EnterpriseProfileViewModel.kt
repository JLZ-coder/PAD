package es.ucm.fdi.mybooker.ui_enterprise.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import es.ucm.fdi.mybooker.objects.itemEnterprise_2

class EnterpriseProfileViewModel : ViewModel() {

    private var db = FirebaseFirestore.getInstance()
    var mAuth = FirebaseAuth.getInstance()
    private val userId = mAuth.currentUser.uid

    var enterprise : itemEnterprise_2? = null

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
        db.collection("enterprises").whereEqualTo("userId", userId).limit(1).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful()) {
                    val documents: QuerySnapshot? = task.getResult()
                    if (documents != null) {
                        for (document in documents) {
                            enterprise = document.toObject<itemEnterprise_2>()

                            _name.value = enterprise?.name
                            _email.value = enterprise?.email
                            _category.value = "Categoría: " + enterprise?.category
                            _location.value = "Ubicación: " + enterprise?.location
                            _cp.value = "CP: " + enterprise?.cp.toString()
                        }
                    }
                }
            }
    }
}