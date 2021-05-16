package es.ucm.fdi.mybooker.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import es.ucm.fdi.mybooker.R

/**
 * A simple [Fragment] subclass.
 * Use the [homeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment()
{

    private  var currentFragment: Fragment? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {

        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        setButtonsListeners(view)

        return view
    }

    private fun setButtonsListeners(view: View)
    {

        val searchAll: CardView = view.findViewById(R.id.todas)
        searchAll.setOnClickListener {
            currentFragment = SearchFragment.newInstance("all")
            changeFragment(currentFragment as SearchFragment)
            return@setOnClickListener
        }
        val searchSalud: CardView = view.findViewById(R.id.salud)
        searchSalud.setOnClickListener {

            currentFragment = ScheduleFragment.newInstance("salud")!!
            changeFragment(currentFragment as ScheduleFragment)
            return@setOnClickListener
        }
        val searchAsesoria: CardView = view.findViewById(R.id.asesoria)
        searchAsesoria.setOnClickListener {
            currentFragment = SearchFragment.newInstance("asesoria")
            changeFragment(currentFragment as SearchFragment)
            return@setOnClickListener
        }
        val searchBelleza: CardView = view.findViewById(R.id.belleza)
        searchBelleza.setOnClickListener {
            currentFragment = SearchFragment.newInstance("belleza")
            changeFragment(currentFragment as SearchFragment)
            return@setOnClickListener
        }
        val searchOcio: CardView = view.findViewById(R.id.ocio)
        searchOcio.setOnClickListener {
            currentFragment = SearchFragment.newInstance("ocio")
            changeFragment(currentFragment as SearchFragment)
            return@setOnClickListener
        }
        val searchRestaurant: CardView = view.findViewById(R.id.restaurant)
        searchRestaurant.setOnClickListener {
            currentFragment = SearchFragment.newInstance("restaurant")
            changeFragment(currentFragment as SearchFragment)
            return@setOnClickListener
        }
    }


    private fun changeFragment(fragment: Fragment)
    {

        val searchFragment = SearchFragment()
        val fragmentManager: FragmentManager? = fragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.container, searchFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment homeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() : HomeFragment  = HomeFragment()
    }
}