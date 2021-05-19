package es.ucm.fdi.mybooker.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.adapters.EnterpriseAdapter
import es.ucm.fdi.mybooker.objects.itemEnterprise
import java.lang.Exception

private const val ARG_PARAM1 = "category"
private const val ARG_PARAM2 = "name"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment(), EnterpriseAdapter.onClickListener
{

    private var db = FirebaseFirestore.getInstance()
    private lateinit var enterprises: MutableList<itemEnterprise>

    private lateinit var mRecyclerView : RecyclerView
    private lateinit var emptyText: TextView

    private lateinit var inflater: LayoutInflater

    var act: Actualizar? = null

    interface Actualizar {
        fun actualizarStackProfile(fragment: Fragment, tag: String)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {

        this.inflater = inflater
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {

        super.onViewCreated(view, savedInstanceState)

        emptyText = view.findViewById(R.id.empty_view)
        emptyText.visibility = View.GONE

        mRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        if (requireArguments().getString(ARG_PARAM1) != "") {
            when(val type = arguments?.getString(ARG_PARAM1)) {
                "all" -> {
                    searchAll(inflater)
                    true
                }
                else -> {
                    searchEnterprisesByCathegory(type, inflater)
                    true
                }
            }
        } else if (requireArguments().getString(ARG_PARAM2) != "") {
            searchEnterprisesByName(arguments?.getString(ARG_PARAM2), inflater)
        }
    }

    private fun setNotFoundView()
    {
        emptyText.visibility = View.VISIBLE
        emptyText.text = "No se han encontrado empresas con esos parámetros"
        mRecyclerView.visibility = View.GONE
    }

    /**
     * Busca las empresas por nombre
     */
    private fun searchEnterprisesByName(name:String?, inflater: LayoutInflater)
    {

        // Para poder hacer búsquedas por un tozo de nombre
        db.collection("enterprises").orderBy("name").startAt(name).endAt(name+'\uf8ff').get()
            .addOnSuccessListener { documents ->
                enterprises = ArrayList()
                for (document in documents) {
                    enterprises.add(itemEnterprise(document.data["userId"].toString(), document.data["profileImg"].toString(), document.data["name"].toString(),
                        document.data["location"].toString(), document["cp"].toString(), document.data["category"].toString()))
                }
                if(enterprises.isNotEmpty()) {
                    setAdapter(inflater.context, mRecyclerView)
                } else {
                    setNotFoundView()
                }
            }.addOnFailureListener { exception ->
                FirebaseCrashlytics.getInstance().recordException(Exception("ERROR: Error getting documents ${exception.message}"))
            }
    }

    /**
     * Busca las empresas por categoría
     */
    private fun searchEnterprisesByCathegory(category: String?, inflater: LayoutInflater)
    {

        db.collection("enterprises").whereEqualTo(ARG_PARAM1, category).get()
            .addOnSuccessListener { documents ->
                enterprises = ArrayList()
                for (document in documents) {
                    enterprises.add(itemEnterprise(document.data["userId"].toString(), document.data["profileImg"].toString(), document.data["name"].toString(),
                       document.data["location"].toString(), document.getLong("cp")?.toInt().toString(), document.data["category"].toString()))
                }
                if(enterprises.isNotEmpty()) {
                    setAdapter(inflater.context, mRecyclerView)
                } else {
                    setNotFoundView()
                }
            }.addOnFailureListener { exception ->
                FirebaseCrashlytics.getInstance().recordException(Exception("ERROR: Error getting documents ${exception.message}"))
            }
    }

    /**
     * Busca todas las empresas
     */
    private fun searchAll(inflater: LayoutInflater)
    {

        db.collection("enterprises").get()
            .addOnSuccessListener { documents ->
                enterprises = ArrayList()
                for (document in documents) {
                    enterprises.add(itemEnterprise(document.data["userId"].toString(),document.data["profileImg"].toString(), document.data["name"].toString(),
                      document.data["location"].toString(), document["cp"].toString(), document.data["category"].toString()))
                }
                if(enterprises.isNotEmpty()) {
                    setAdapter(inflater.context, mRecyclerView)
                } else {
                    setNotFoundView()
                }
            }.addOnFailureListener { exception ->
                FirebaseCrashlytics.getInstance().recordException(Exception("ERROR: Error getting documents ${exception.message}"))
            }
    }


    private fun setAdapter(context: android.content.Context, mRecyclerView: RecyclerView)
    {

        val mAdapter = EnterpriseAdapter(enterprises, this)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.adapter = mAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance(): SearchFragment = SearchFragment()
    }

    //Habría que crear nuevo fragment con calendario
    //Segun id, buscar su horario
    //Pasar todo a través de un bundle
    override fun openItemClick(enterprise: itemEnterprise, position: Int) {
        val bundle = Bundle()
        val gson = Gson()

        val ser = gson.toJson(enterprise)

        bundle.putString("objectEnterprise",ser)

        var fragment = EnterpriseFragment.newInstance()
        fragment.arguments = bundle

        act?.actualizarStackProfile(fragment,"enterpriseFragment")

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SearchFragment.Actualizar) {
            act = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        act = null
    }
}