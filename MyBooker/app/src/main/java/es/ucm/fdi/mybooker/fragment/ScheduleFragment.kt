package es.ucm.fdi.mybooker.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import es.ucm.fdi.mybooker.R
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.RuntimeException

// Lo utilizaremos para recuperar, de la base de datos, las citas que tenga el usuario
private const val ARG_PARAM1 = "email"

//Boton logout
private lateinit var mLogoutbtn : Button
/**
 * A simple [Fragment] subclass.
 * Use the [scheduleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScheduleFragment() : Fragment() {
    // TODO: Rename and change types of parameters
    private var email: String? = ""

    override fun onCreate(savedInstanceState: Bundle?)
    {

        super.onCreate(savedInstanceState)
        arguments?.let {
            this.email = it.getString(ARG_PARAM1)
        }

        try {
            checkUserSchedule(email)
        }catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.message?.let { Log.d("ERROR MALO: ", it) }
        }
        // throw RuntimeException("Test Crash")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    companion object {
        @JvmStatic
        //fun newInstance(email: String?): ScheduleFragment = ScheduleFragment()
        fun newInstance(email: String?): ScheduleFragment? {
            val myFragment = ScheduleFragment()
            val args = Bundle()
            args.putString("email", email)
            myFragment.arguments = args
            return myFragment
        }
    }

    private fun checkUserSchedule(email: String?)
    {

        if (email == null || email == "")
            throw Exception("Nos ha llegado email vacío")
    }
}