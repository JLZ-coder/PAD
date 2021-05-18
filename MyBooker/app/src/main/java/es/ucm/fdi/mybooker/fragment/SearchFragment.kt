package es.ucm.fdi.mybooker.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.adapters.EnterpriseAdapter
import es.ucm.fdi.mybooker.objects.itemEnterprise
import java.lang.Exception

private const val ARG_PARAM1 = "type"
private const val ARG_PARAM2 = "name"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment(), EnterpriseAdapter.onClickListener
{

    private var type: String? = null

    private var db = FirebaseFirestore.getInstance()
    private lateinit var enterprises: MutableList<itemEnterprise>

    private lateinit var mRecyclerView : RecyclerView
    private lateinit var emptyText: TextView

    var act: Actualizar? = null

    interface Actualizar{
        fun actualizarStackProfile(fragment: Fragment, tag: String)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {

        this.type = requireArguments().getString(ARG_PARAM1)

        //Toast.makeText(this@SearchFragment.context, this.type, Toast.LENGTH_SHORT).show();
        if (this.type == null || this.type == "") {
            // Nos ha llegado vacío o a null, seteamos a all y nos enviamos notificación a Firebase
            FirebaseCrashlytics.getInstance().recordException(Exception("El parámetro de búsqueda type ha llegado vacío"))
            this.type = "all"
        }

        Log.i("savedInstanceState", savedInstanceState.toString())
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)

        emptyText = view.findViewById(R.id.empty_view)
        emptyText.visibility = View.GONE

        mRecyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        when(type) {
            "all" -> {
                searchAll(inflater)
                true
            }
            else -> {
                searchEnterprisesByCathegory(type, inflater)
                true
            }
        }

        return view
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
    private fun searchEnterprisesByName(name:String, inflater: LayoutInflater)
    {

        db.collection("enterprises").whereEqualTo(ARG_PARAM1, name).get()
            .addOnSuccessListener { documents ->
                enterprises = ArrayList()
                for (document in documents) {
                    enterprises.add(itemEnterprise(document.data["profileImg"].toString(), document.data["name"].toString(), document.data["address"].toString(), document.data["type"].toString()))
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

        Log.i("category", category.toString())
        db.collection("enterprises").whereEqualTo(ARG_PARAM1, category).get()
            .addOnSuccessListener { documents ->
                enterprises = ArrayList()
                for (document in documents) {
                    enterprises.add(itemEnterprise(document.data["profileImg"].toString(), document.data["name"].toString(), document.data["address"].toString(), document.data["type"].toString()))
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

        Log.i("entra en all", "entramos bor")

        db.collection("enterprises").get()
            .addOnSuccessListener { documents ->
                enterprises = ArrayList()
                for (document in documents) {

                    // TODO, comprobar los espacios vacios
                    enterprises.add(itemEnterprise(document.data["profileImg"].toString(), document.data["name"].toString(),
                        document.data["address"].toString(), document.data["type"].toString()))
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