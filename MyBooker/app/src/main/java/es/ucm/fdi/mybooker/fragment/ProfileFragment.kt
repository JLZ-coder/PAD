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
import es.ucm.fdi.mybooker.ActivityLogin
import es.ucm.fdi.mybooker.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


//Boton logout
private lateinit var mLogoutbtn : Button
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            name = it.getString("name")
            email = it.getString("email")
        }
        Toast.makeText(this@ProfileFragment.context,  name, Toast.LENGTH_SHORT).show()



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile, container, false)

        Toast.makeText(this@ProfileFragment.context,  email, Toast.LENGTH_SHORT).show()
        nameText = view.findViewById(R.id.nombreTextView)
        emailText = view.findViewById(R.id.emailTextView)

        
        nameText.text = name
        emailText.text = email

        //Este boton irá en el perfil del usuario
       mLogoutbtn = view.findViewById(R.id.logout)
       mLogoutbtn.setOnClickListener() {
           val i = Intent(this@ProfileFragment.context, ActivityLogin::class.java)
           startActivity(i)
       }



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
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