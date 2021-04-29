package es.ucm.fdi.mybooker.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.objects.itemEnterprise

class EnterpriseAdapter(val enterprises: List<itemEnterprise>) : RecyclerView.Adapter<EnterpriseAdapter.EnterpriseHolder>()
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
        holder.render(enterprises[position])
    }

    class EnterpriseHolder(val view: View): RecyclerView.ViewHolder(view)
    {

        private val entName = view.findViewById(R.id.entName) as TextView
        private val entCategory = view.findViewById(R.id.entCatego) as TextView
        private val entAddress = view.findViewById(R.id.entAddress) as TextView
        private val entImg = view.findViewById(R.id.entImg) as ImageView

        fun render(enterprise: itemEnterprise) {

            Picasso.with(view.context).load(enterprise.enterpriseImg).into(entImg)
            entName.text = enterprise.enterpriseName
            entCategory.text = enterprise.enterpriseCategory
            entAddress.text = enterprise.enterpriseAddress
        }
    }
}