package es.ucm.fdi.mybooker.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.adapters.ShiftFirestoreAdapter

class HomeFragment : Fragment() {

  private lateinit var homeViewModel: HomeViewModel
  private lateinit var mAdapter: ShiftFirestoreAdapter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_enterprise_home, container, false)

    val empty_textview: TextView = root.findViewById(R.id.shifts_empty_view)
    homeViewModel.empty_recycler_text.observe(viewLifecycleOwner, Observer {
      empty_textview.text = it
    })

    val turnos: RecyclerView = root.findViewById(R.id.shifts_recycler)
    turnos.layoutManager = LinearLayoutManager(activity)
    turnos.adapter = homeViewModel.firebase_adapter
    mAdapter = homeViewModel.firebase_adapter

    mAdapter.registerAdapterDataObserver(object : AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart : Int, itemCount: Int) {
          val totalNumberOfItems = itemCount
          if (totalNumberOfItems == 1) {
            turnos.visibility = View.VISIBLE
            empty_textview.visibility = View.GONE
          }
        }
        override fun onItemRangeRemoved(positionStart : Int, itemCount: Int) {
          val totalNumberOfItems = itemCount
          if (totalNumberOfItems <= 1) {
            turnos.visibility = View.INVISIBLE
            empty_textview.visibility = View.VISIBLE
          }
        }
      })

    val add_shift_btn: Button = root.findViewById(R.id.add_shift_button)
    add_shift_btn.setOnClickListener { view : View ->
      view.findNavController().navigate(R.id.action_navigation_home_to_navigation_add_shift)
    }

    return root
  }

  override fun onStart() {
    super.onStart()
    mAdapter!!.startListening()
  }

  override fun onStop() {
    super.onStop()
    mAdapter!!.stopListening()
  }
}