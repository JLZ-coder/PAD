package es.ucm.fdi.mybooker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.mybooker.databinding.ActivityLoginBinding

class ActivityLogin : AppCompatActivity()
{

    // TODO: HAbrá q estructurar esto bien. Era puna prueba para el login de usuarios
    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_login)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.btnRegisterUser.setOnClickListener {
            goRegisterUser()
        }

        binding.btnRegisterEnt.setOnClickListener {
            goRegisterEnt()
        }

        setUp()
    }

    private fun goRegisterUser()
    {

        val i = Intent(this, ActivityRegister::class.java).apply {
            putExtra("mail", findViewById<EditText>(R.id.editTextUser).text.toString())
        }
        i.putExtra("tipoUsuario", "usuario")
        startActivity(i)
    }

    private fun goRegisterEnt()
    {
        // TODO: Registrar empresa
        val i = Intent(this, ActivityRegister::class.java).apply {
            putExtra("mail", findViewById<EditText>(R.id.editTextUser).text.toString())
        }
        i.putExtra("tipoUsuario", "empresa")
        startActivity(i)
    }

    private fun setUp()
    {
        title = "Login"

        val userMail: EditText = findViewById<EditText>(R.id.editTextUser)
        val userPass: EditText = findViewById<EditText>(R.id.editTextPassword)

        val login = findViewById<Button>(R.id.btnLogIn)
        login.setOnClickListener {
            if (userPass.text.isNotEmpty() && userMail.text.isNotEmpty()) {
                mAuth.signInWithEmailAndPassword(
                    userMail.text.toString(),
                    userPass.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user_uid = mAuth.currentUser.uid
                        db.collection("users").document(user_uid).get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    //Log.d("", "DocumentSnapshot data: ${document.data}")
                                    val tipo = document["tipoUsuario"].toString()
                                    when (tipo) {
                                        "usuario" -> showUserInfo(user_uid)
                                        "empresa" -> showEmpresaInfo(user_uid)
                                        else -> Log.d("login_getDocument", "No such document")
                                    }
                                } else {
                                    Log.d("login_getDocument", "No such document")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d("login_getDocument", "get failed with ", exception)
                            }
                    }
                    else {
                        showAlert()
                    }
                }
            } else {
                showAlert()
            }
        }
    }

    private fun showAlert()
    {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage("El usuario no existe")
        //builder.setPositiveButton("Registrar usuario"){ _, _ -> goRegisterUser() }
        //builder.setPositiveButton("Resgistrar empresa"){ _, _ -> goRegisterEnt() }
        builder.setNegativeButton("Reintentar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showUserInfo(userId : String)
    {
        var name : String? = ""
        var email : String? = ""
        db.collection("users").document(userId).get()
            .addOnSuccessListener {document ->
                if (document != null) {
                    name = document.getString("name")
                    email = document.getString("email")
                } else {
                    mAuth.signOut()
                }
            }

        // TODO: Nos vamos a ir a la info del usuario cndo haga login, o a la empresa que clique, pero eso hay q mirarlo bien
        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("userName", name)
            putExtra("email", email)
        }
        startActivity(homeIntent);
    }

    private fun showEmpresaInfo(userId : String)
    {
        var name : String? = ""
        var email : String? = ""
        db.collection("users").document(userId).get()
            .addOnSuccessListener {document ->
                if (document != null) {
                    name = document.getString("name")
                    email = document.getString("email")
                } else {
                    mAuth.signOut()
                }
            }

        // TODO: Nos vamos a ir a la info del usuario cndo haga login, o a la empresa que clique, pero eso hay q mirarlo bien
        val homeIntent = Intent(this, EmpresaReservasActivity::class.java).apply {
            putExtra("userName", name)
            putExtra("email", email)
        }
        startActivity(homeIntent);
    }
}