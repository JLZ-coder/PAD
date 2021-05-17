package es.ucm.fdi.mybooker.ui_enterprise.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.ucm.fdi.mybooker.R

class EnterpriseEditProfileFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    val root = inflater.inflate(R.layout.fragment_enterprise_edit_profile, container, false)

    return root
  }
}