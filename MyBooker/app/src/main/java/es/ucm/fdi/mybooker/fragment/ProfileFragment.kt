package es.ucm.fdi.mybooker.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import es.ucm.fdi.mybooker.ActivityLogin
import es.ucm.fdi.mybooker.R

private const val GALLERY_INTENT = 1

/**
 * A simple [Fragment] subclass.
 * Use the [profileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    private var email: String? = null
    private var name: String? = null
    private var image: String? = null

    //Vista
    private lateinit var nameText : TextView
    private lateinit var emailText : TextView

    //Boton logout
    private lateinit var mLogoutbtn : Button
    // Botón cambiar Imagen
    private lateinit var mChangeImgBtn : Button
    //Boton Eliminar usuario
    private lateinit var mdeletebtn : Button
    private lateinit var imageView : ImageView

    //Firebase
    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()
    private val userId = mAuth.currentUser.uid
    private val mStorage: StorageReference = FirebaseStorage.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = "Perfil"
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {

        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            name = it.getString("name")
            email = it.getString("email")
            image = it.getString("profileImg")
        }

        nameText = view?.findViewById<TextView>(R.id.nombreTextView)!!
        emailText = view?.findViewById<TextView>(R.id.emailTextView)!!

        nameText.text = name
        emailText.text = email

        imageView = view?.findViewById<ImageView>(R.id.imageView)!!
        Picasso.with(view?.context).load(image).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(imageView)

        mChangeImgBtn = view?.findViewById<Button>(R.id.btn_add_user_img)!!
        mChangeImgBtn.setOnClickListener {
            val i: Intent = Intent(Intent.ACTION_PICK)
            i.type = "image/*"
            startActivityForResult(i, GALLERY_INTENT)
        }

        //Este boton irá en el perfil del usuario
        mLogoutbtn = view?.findViewById<Button>(R.id.logout)!!
        mLogoutbtn.setOnClickListener {
            mAuth.signOut()
            val i = Intent(this@ProfileFragment.context, ActivityLogin::class.java)
            startActivity(i)

        }
        mdeletebtn = view?.findViewById<Button>(R.id.button_delete_user)!!
        mdeletebtn.setOnClickListener { view->
            val alert = AlertDialog.Builder(activity)
            alert.setTitle("Eliminar cuenta")
            alert.setMessage("¿Estas seguro?")
            alert.setPositiveButton("SI, estoy seguro", object: DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    db.collection("users").document(userId).delete()
                        .addOnSuccessListener {
                            FirebaseAuth.getInstance().currentUser!!.delete()
                                .addOnSuccessListener {
                                    db.collection("reserves").whereEqualTo("id_cliente", userId).get()
                                        .addOnSuccessListener {
                                            db.collection("usersSchedule").whereEqualTo("id_cliente", userId).get()
                                                .addOnSuccessListener {
                                                    val i = Intent(activity, ActivityLogin::class.java)
                                                    startActivity(i)
                                                }
                                                .addOnFailureListener {
                                                    showAlert("Hubo un problema al eliminar al usuario")
                                                }
                                        }
                                        .addOnFailureListener {
                                            showAlert("Hubo un problema al eliminar al usuario")
                                        }
                                }
                                .addOnFailureListener {
                                    showAlert("Hubo un problema al eliminar al usuario")
                                }
                        }
                        .addOnFailureListener {
                            showAlert("Hubo un problema al eliminar al usuario")
                        }
                    if (dialog != null) {
                        dialog.dismiss()
                    }
                }
            })
            alert.setNegativeButton("Cancelar", object: DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    if (dialog != null) {
                        dialog.dismiss()
                    }
                }
            })

            alert.show()
        }
    }

    private fun showAlert(message: String?)
    {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("ERROR")
        builder.setMessage(message.toString())
        builder.setPositiveButton("Continuar", null)
        builder.show()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment profileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() : ProfileFragment  = ProfileFragment()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {

        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_INTENT){

            val uri: Uri? = data?.data
            if (uri != null) {
                val filePath: StorageReference = mStorage.child("user_images").child(uri?.lastPathSegment.toString())

                filePath.putFile(uri).addOnSuccessListener {

                    mStorage.child(filePath.path).downloadUrl.addOnSuccessListener {
                        db.collection("users").document(userId).update(
                            mapOf(
                                "profileImg" to it.toString()
                            )
                        )
                        Picasso.with(view?.context).load(it.toString()).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(imageView)
                    }.addOnFailureListener {
                        showAlert(it.message)
                    }
                }.addOnFailureListener{
                    showAlert(it.message)
                }
            }
        }
    }
}