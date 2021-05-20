package es.ucm.fdi.mybooker.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import es.ucm.fdi.mybooker.R


/**
 * A simple [Fragment] subclass.
 * Use the [homeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("DEPRECATION")
class HomeFragment : Fragment()
{
    var callback: Actualizar? = null

   interface Actualizar{
        fun actualizarStack(fragment: Fragment, tag:String)
    }
    private  var currentTag: String = "homeFragment"
    //private  var currentFragment: Fragment? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {

        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        activity?.title = "Inicio"
        setButtonsListeners(view)

        return view
    }

    private fun setButtonsListeners(view: View)
    {

        val searchAll: CardView = view.findViewById(R.id.todas)
        searchAll.setOnClickListener {
            changeFragment(SearchFragment.newInstance(), "all")
            return@setOnClickListener
        }
        val searchSalud: CardView = view.findViewById(R.id.salud)
        searchSalud.setOnClickListener {
            changeFragment(SearchFragment.newInstance(), "Salud")
            return@setOnClickListener
        }
        val searchAsesoria: CardView = view.findViewById(R.id.asesoria)
        searchAsesoria.setOnClickListener {
            changeFragment(SearchFragment.newInstance(), "Asesoría")
            return@setOnClickListener
        }
        val searchBelleza: CardView = view.findViewById(R.id.belleza)
        searchBelleza.setOnClickListener {
            changeFragment(SearchFragment.newInstance(), "Belleza")
            return@setOnClickListener
        }
        val searchOcio: CardView = view.findViewById(R.id.ocio)
        searchOcio.setOnClickListener {
            changeFragment(SearchFragment.newInstance(), "Ocio")
            return@setOnClickListener
        }
        val searchRestaurant: CardView = view.findViewById(R.id.restaurant)
        searchRestaurant.setOnClickListener {
            changeFragment(SearchFragment.newInstance(), "Restaurantes")
            return@setOnClickListener
        }
        val searchSports: CardView = view.findViewById(R.id.deportes)
        searchSports.setOnClickListener {
            changeFragment(SearchFragment.newInstance(), "Deportes")
            return@setOnClickListener
        }
    }

    private fun changeFragment(fragment: Fragment, category: String)
    {

        val bn: Bundle = Bundle()
        bn.putString("category", category)
        bn.putString("name", "")
        fragment.arguments = bn
        callback?.actualizarStack(fragment, "searchFragment")
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Actualizar) {
            callback = context
        }

    }

    override fun onDetach() {
        super.onDetach()
        callback = null
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