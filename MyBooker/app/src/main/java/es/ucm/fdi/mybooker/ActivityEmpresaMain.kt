package es.ucm.fdi.mybooker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.mybooker.adapters.ReserveAdapter
import es.ucm.fdi.mybooker.databinding.ActivityEmpresaMainBinding
import es.ucm.fdi.mybooker.objects.itemReserve

class ActivityEmpresaMain : AppCompatActivity() {

    // TODO: HAbrá q estructurar esto bien. Era puna prueba para el login de usuarios
    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()

    private lateinit var binding: ActivityEmpresaMainBinding
    private lateinit var reserveAdapter : ReserveAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empresa_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_empresa_main)

        val bundle: Bundle? = intent.extras
        val email = bundle?.getString("email")
        val name = bundle?.getString("name")

        val uno = itemReserve("uno")
        val dos = itemReserve("dos")
        reserveAdapter = ReserveAdapter(listOf(
            uno, dos
        ))
        binding.empresaResumen.layoutManager = LinearLayoutManager(this)
        binding.empresaResumen.adapter = reserveAdapter

        //setUpRecyclerView()
        // setUp(email ?: "no user found", provider ?: "empty", name ?: "no name")
    }

}