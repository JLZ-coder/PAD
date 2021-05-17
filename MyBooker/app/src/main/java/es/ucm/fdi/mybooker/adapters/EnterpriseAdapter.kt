package es.ucm.fdi.mybooker.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.fragment.HomeFragment
import es.ucm.fdi.mybooker.objects.itemEnterprise


class EnterpriseAdapter(val enterprises: List<itemEnterprise>, var inter:onClickListener) : RecyclerView.Adapter<EnterpriseAdapter.EnterpriseHolder>()
{
    // Devolvemos el tamaño de la lista de empresas. Forma simplificada
    override fun getItemCount(): Int = enterprises.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EnterpriseHolder
    {

        val layoutInflater = LayoutInflater.from(parent.context)
        return EnterpriseHolder(layoutInflater.inflate(R.layout.item_enterprise, parent, false))
    }

    override fun onBindViewHolder(holder: EnterpriseHolder, position: Int)
    {
        holder.render(enterprises[position],inter)
    }

    inner class EnterpriseHolder(val view: View): RecyclerView.ViewHolder(view){

        private val entName = view.findViewById(R.id.entName) as TextView
        private val entCategory = view.findViewById(R.id.entCatego) as TextView
        private val entAddress = view.findViewById(R.id.entAddress) as TextView
        private val entImg = view.findViewById(R.id.entImg) as ImageView

        fun render(enterprise: itemEnterprise, action: onClickListener) {


            // TODO: Hacer que esto funcione bien
            if (enterprise.enterpriseImg != "" || enterprise.enterpriseImg != null) {

                val storage = Firebase.storage
                val imgUrl: String = enterprise.enterpriseImg
                imgUrl.replace("gs://mybooker-6c774.appspot.com", "", false)
                val storageReference = storage.getReferenceFromUrl("gs://mybooker-6c774.appspot.com").child(imgUrl)
                storageReference.downloadUrl.addOnSuccessListener {
                        uri -> Picasso.with(view.context).load(uri.toString()).into(entImg)
                }.addOnFailureListener {
                    Log.i("POLLAS ", "EN VINAGRE")
                }
            }

            entName.text = enterprise.enterpriseName
            entCategory.text = enterprise.enterpriseCategory
            entAddress.text = enterprise.enterpriseAddress

            itemView.setOnClickListener(){
                action.openItemClick(adapterPosition)
            }
        }

    }

    interface onClickListener {
        fun openItemClick(position: Int)
    }
}


