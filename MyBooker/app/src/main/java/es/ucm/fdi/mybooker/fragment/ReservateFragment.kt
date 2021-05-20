package es.ucm.fdi.mybooker.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import es.ucm.fdi.mybooker.R
import es.ucm.fdi.mybooker.objects.ItemHours
import java.util.*
import kotlin.collections.ArrayList

class ReservateFragment : DialogFragment() {
    private var message: String = ""
    private lateinit var listener: reservateDb


    interface reservateDb {
        fun onDialogPositiveClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage(message)
                .setTitle(R.string.titleReserva)
                .setPositiveButton("Aceptar") { dialog, id ->
                    listener.onDialogPositiveClick(this)
                }
                .setNegativeButton("Cancelar",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as reservateDb
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implement NoticeDialogListener"))
        }
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