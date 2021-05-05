package es.ucm.fdi.mybooker.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.mybooker.ActivityLogin
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.adapters.ReserveAdapter
import es.ucm.fdi.mybooker.databinding.FragmentHomeBinding
import es.ucm.fdi.mybooker.objects.itemReserve

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var reserveAdapter: ReserveAdapter

    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        binding = DataBindingUtil.inflate<FragmentHomeBinding>(inflater, R.layout.fragment_home,container,false)

        binding.empresaTitle.text = "name"

        val uno = itemReserve("15:00", "YO", 1)
        val dos = itemReserve("15:10", "YO TAMBIEN", 1)
        reserveAdapter = ReserveAdapter(listOf(
            uno, dos
        ))
        binding.empresaResumen.layoutManager = LinearLayoutManager(activity)
        binding.empresaResumen.adapter = reserveAdapter

        binding.logout.setOnClickListener() {
            mAuth.signOut()
            val i = Intent(activity, ActivityLogin::class.java)
            startActivity(i)
        }

        return binding.root
    }
}