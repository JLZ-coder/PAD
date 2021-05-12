package es.ucm.fdi.mybooker.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil.setContentView
import com.google.firebase.auth.FirebaseAuth
import es.ucm.fdi.mybooker.ActivityLogin
import es.ucm.fdi.mybooker.R

/**
 * A simple [Fragment] subclass.
 * Use the [profileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var email: String? = null
    private var name: String? = null
    //Vista
    private lateinit var nameText : TextView
    private lateinit var emailText : TextView
    //Boton logout
    private lateinit var mLogoutbtn : Button
    //Firebase
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        arguments?.let {
            name = it.getString("name")
            email = it.getString("email")
        }
        nameText = view?.findViewById<TextView>(R.id.nombreTextView)!!
        emailText = view?.findViewById<TextView>(R.id.emailTextView)!!

        nameText.text = name
        emailText.text = email

        //Este boton irá en el perfil del usuario
        mLogoutbtn = view?.findViewById<Button>(R.id.logout)!!
        mLogoutbtn.setOnClickListener {
            mAuth.signOut()
            val i = Intent(this@ProfileFragment.context, ActivityLogin::class.java)
            startActivity(i)

        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment profileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() : ProfileFragment  = ProfileFragment()

    }


}