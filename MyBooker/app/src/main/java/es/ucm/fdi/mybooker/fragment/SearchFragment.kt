package es.ucm.fdi.mybooker.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.adapters.EnterpriseAdapter
import es.ucm.fdi.mybooker.objects.itemEnterprise
import java.lang.Exception
import java.util.zip.Inflater

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "type"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment()
{

    private var type: String? = null

    private var db = FirebaseFirestore.getInstance()
    private lateinit var enterprises: MutableList<itemEnterprise>

    private lateinit var mRecyclerView : RecyclerView
    private lateinit var emptyText: TextView

    override fun onCreate(savedInstanceState: Bundle?)
    {

        // ClientScheduleAdapter
        super.onCreate(savedInstanceState)

        Log.i("savedInstanceState", savedInstanceState.toString())
        if (savedInstanceState != null) {
            this.type = savedInstanceState.getString(ARG_PARAM1)
        }

        Log.i("NONONONO" , this.type.toString())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {

        val view: View = inflater.inflate(R.layout.fragment_search, container, false)

        emptyText = view.findViewById(R.id.empty_view)
        emptyText.visibility = View.GONE

        mRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        if (type == "" || type == null) {
            FirebaseCrashlytics.getInstance().recordException(Exception("El parámetro de búsqueda type ha llegado vacío"))
        }

        Log.i("EL TYYYPE", type.toString())

        when(type) {
            "all" -> {
                searchAll(inflater)
            }
            else -> {
                searchEnterprisesByCathegory(type, inflater)
            }
        }
        // Inflate the layout for this fragment
        return view
    }

    /**
     * Busca las empresas por categoría
     */
    private fun searchEnterprisesByCathegory(category: String?, inflater: LayoutInflater)
    {

        Log.i("category", category.toString())
        db.collection("enterprises").whereEqualTo(ARG_PARAM1, category).get()
            .addOnSuccessListener { documents ->
                enterprises = ArrayList()
                for (document in documents) {
                    buildObjectAdapter(document.data["name"].toString(), document.data["profileImg"].toString(), document.data["address"].toString(), document.data["type"].toString())
                    Log.i("AAAAAAA", "${document.id} => ${document.data}")
                }
                if(enterprises.isNotEmpty()) {
                    setAdapter(inflater.context, mRecyclerView)
                } else {
                    emptyText.visibility = View.VISIBLE
                    emptyText.text = "No se han encontrado empresas con esos parámetros"
                    mRecyclerView.visibility = View.GONE
                }
            }.addOnFailureListener { exception ->
                Log.w("ERROROROROR ", "Error getting documents: ", exception)
            }
    }

    /**
     * Busca todas las empresas
     */
    private fun searchAll(inflater: LayoutInflater)
    {

        Log.i("entra en all", "entramos bor")

        db.collection("enterprises").get()
            .addOnSuccessListener { documents ->
                enterprises = ArrayList()
                for (document in documents) {

                    buildObjectAdapter(document.data["name"].toString(), document.data["profileImg"].toString(), document.data["address"].toString(), document.data["type"].toString())
                }
                if(enterprises.isNotEmpty()) {
                    setAdapter(inflater.context, mRecyclerView)
                } else {
                    emptyText.visibility = View.VISIBLE
                    emptyText.text = "No se han encontrado empresas con esos parámetros"
                    mRecyclerView.visibility = View.GONE
                }
            }.addOnFailureListener { exception ->
                Log.w("ERROROROROR ", "Error getting documents: ", exception)
            }
    }


    private fun setAdapter(context: android.content.Context, mRecyclerView: RecyclerView)
    {

        val mAdapter : EnterpriseAdapter = EnterpriseAdapter(enterprises)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.adapter = mAdapter
    }

    private fun buildObjectAdapter(name: String, img: String, address: String, type: String)
    {

        enterprises.add(itemEnterprise(img, name, address, type))
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(searchParam: String): SearchFragment?{

            val myFragment = SearchFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, searchParam)
            myFragment.arguments = args

            Log.i("ASDASDASDASD", args.toString())
            return myFragment
        }

    }
}