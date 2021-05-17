package es.ucm.fdi.mybooker.ui_enterprise.profile

import android.content.Intent
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
import es.ucm.fdi.mybooker.ActivityLogin
import es.ucm.fdi.mybooker.R

class EnterpriseProfileFragment : Fragment() {

  private lateinit var notificationsViewModel: EnterpriseProfileViewModel

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    notificationsViewModel =
            ViewModelProvider(this).get(EnterpriseProfileViewModel::class.java)
    val root = inflater.inflate(R.layout.fragment_enterprise_profile, container, false)
    val name: TextView = root.findViewById(R.id.label_enterprise_name)
    notificationsViewModel.name.observe(viewLifecycleOwner, Observer {
      name.text = it
    })
    val email: TextView = root.findViewById(R.id.label_enterprise_email)
    notificationsViewModel.email.observe(viewLifecycleOwner, Observer {
      email.text = it
    })
    val category: TextView = root.findViewById(R.id.label_enterprise_category)
    notificationsViewModel.category.observe(viewLifecycleOwner, Observer {
      category.text = it
    })
    val location: TextView = root.findViewById(R.id.label_enterprise_location)
    notificationsViewModel.location.observe(viewLifecycleOwner, Observer {
      location.text = it
    })
    val cp: TextView = root.findViewById(R.id.label_enterprise_cp)
    notificationsViewModel.cp.observe(viewLifecycleOwner, Observer {
      cp.text = it
    })

    val editprofile_btn: Button = root.findViewById(R.id.button_enterprise_editprofile)
    editprofile_btn.setOnClickListener {view->
      view.findNavController().navigate(R.id.action_navigation_notifications_to_enterpriseEditProfileFragment)
    }

    val logout_btn: Button = root.findViewById(R.id.logout_enterprise)
    logout_btn.setOnClickListener {
      notificationsViewModel.mAuth.signOut()
      val i = Intent(activity, ActivityLogin::class.java)
      startActivity(i)
    }
    return root
  }
}