package es.ucm.fdi.mybooker.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.mybooker.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

//Botones categorías
private lateinit var sportBtn : Button
private lateinit var restaurantBtn : Button
private lateinit var leisureBtn : Button
private lateinit var healthBtn : Button
private lateinit var advisoryBtn : Button
private lateinit var beautyBtn : Button
private lateinit var mLogoutbtn : Button
/**
 * A simple [Fragment] subclass.
 * Use the [homeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        // SETEAMOS LOS LISTENER A LOS BOTONES
        //setButtonsListeners(view)

        // Inflate the layout for this fragment
        return view
    }

    /*private fun setButtonsListeners(view: View)
    {
        val searchAll: CardView = view.findViewById(R.id.todas)
        searchAll.setOnClickListener {
            searchAll()
        }
        val searchSalud: CardView = view.findViewById(R.id.salud)
        searchAll.setOnClickListener {
            searchEnterprisesByCathegory("salud")
        }
        val searchAsesoria: CardView = view.findViewById(R.id.asesoria)
        searchAll.setOnClickListener {
            searchEnterprisesByCathegory("asesoria")
        }
        val searchBelleza: CardView = view.findViewById(R.id.belleza)
        searchAll.setOnClickListener {
            searchEnterprisesByCathegory("belleza")
        }
        val searchOcio: CardView = view.findViewById(R.id.ocio)
        searchAll.setOnClickListener {
            searchEnterprisesByCathegory("ocio")
        }
        val searchRestaurant: CardView = view.findViewById(R.id.restaurant)
        searchAll.setOnClickListener {
            searchEnterprisesByCathegory("restaurant")
        }
    }
*/
    /**
     * Busca todas las empresas
     */
    private fun searchAll()
    {

        db.collection("enterprises").whereEqualTo("type", true).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("AAAAAAA", "${document.id} => ${document.data}")
                }
            }.addOnFailureListener { exception ->
                Log.w("ERROROROROR ", "Error getting documents: ", exception)
            }
    }

    /**
     * Busca las empresas por categoría
     */
    private fun searchEnterprisesByCathegory(category: String)
    {

        db.collection("enterprises").whereEqualTo("type", category).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("AAAAAAA", "${document.id} => ${document.data}")
                }
            }.addOnFailureListener { exception ->
                Log.w("ERROROROROR ", "Error getting documents: ", exception)
            }
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