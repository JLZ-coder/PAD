package es.ucm.fdi.mybooker.fragment

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
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.adapters.EnterpriseAdapter
import es.ucm.fdi.mybooker.objects.itemEnterprise
import java.lang.Exception

private const val ARG_PARAM1 = "type"

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
    /**
     * Busca las empresas por nombre
     */
    private fun searchEnterprisesByName(name:String, inflater: LayoutInflater){

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
                    enterprises.add(itemEnterprise(document.data["profileImg"].toString(), document.data["name"].toString(), document.data["address"].toString(), document.data["type"].toString()))
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

        val mAdapter : EnterpriseAdapter = EnterpriseAdapter(enterprises, this)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.adapter = mAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance(): SearchFragment = SearchFragment()
    }

    override fun openItemClick(position: Int) {
        Toast.makeText(this@SearchFragment.context, "entra", Toast.LENGTH_SHORT).show()
    }
}