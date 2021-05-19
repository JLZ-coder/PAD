package es.ucm.fdi.mybooker.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.ClipData
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.objects.ItemHours
import java.util.*
import kotlin.collections.ArrayList

class ReservateFragment : DialogFragment() {
    private var message: String = ""
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(message)
                .setTitle(R.string.titleReserva)
                .setPositiveButton("Aceptar",
                    DialogInterface.OnClickListener { dialog, id -> //Regresar a fragment Home
                        // FIRE ZE MISSILES!
                    })
                .setNegativeButton("Cancelar",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun createMsg(hours: ArrayList<ItemHours>){
        for(i in hours){
            message += i.start + " a " + i.end + "\n"
        }
    }

    companion object {
        fun newInstance(hours: ArrayList<ItemHours>): ReservateFragment{
            val fragment = ReservateFragment()
            fragment.createMsg(hours)
            return fragment
        }
    }
}