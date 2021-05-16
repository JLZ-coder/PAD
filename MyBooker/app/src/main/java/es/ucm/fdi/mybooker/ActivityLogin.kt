package es.ucm.fdi.mybooker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ActivityLogin : AppCompatActivity()
{

    // TODO: HAbrá q estructurar esto bien. Era puna prueba para el login de usuarios
    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()
    private lateinit var login: Button
    private lateinit var regUser: TextView
    private lateinit var regEnter: TextView
    private lateinit var loadingLogin: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        regUser = findViewById<TextView>(R.id.btnRegisterUser)
        regEnter = findViewById<TextView>(R.id.btnRegisterEnt)

        loadingLogin = findViewById<ProgressBar>(R.id.loadingLogin)

        regUser.setOnClickListener {
            goRegisterUser()
        }

        regEnter.setOnClickListener {
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

        login = findViewById<Button>(R.id.btnLogIn)

        login.setOnClickListener {
            login.isEnabled = false
            loadingLogin.visibility = View.VISIBLE

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
                                    val name = document["name"].toString()
                                    val email = document["email"].toString()
                                    when (tipo) {
                                        "usuario" -> showUserInfo(user_uid,name,email)
                                        "empresa" -> showEmpresaInfo(user_uid)
                                        else -> Log.d("login_getDocument", "No such document")
                                    }
                                } else {
                                    Log.d("login_getDocument", "No such document")
                                }
                            }
                            .addOnFailureListener { exception ->
                                login.isEnabled = true
                                loadingLogin.visibility = View.GONE
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

        login.isEnabled = true
        loadingLogin.visibility = View.GONE

        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage("El usuario no existe")
        //builder.setPositiveButton("Registrar usuario"){ _, _ -> goRegisterUser() }
        //builder.setPositiveButton("Resgistrar empresa"){ _, _ -> goRegisterEnt() }
        builder.setNegativeButton("Reintentar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showUserInfo(userId : String, name:String, email:String)
    {

        // TODO: Nos vamos a ir a la info del usuario cndo haga login, o a la empresa que clique, pero eso hay q mirarlo bien
        val homeIntent = Intent(this, MainActivity::class.java)
        homeIntent.putExtra("userName", name)
        homeIntent.putExtra("email", email)

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
        val homeIntent = Intent(this, EnterpriseMainActivity::class.java).apply {
            putExtra("userId", userId)
            putExtra("userName", name)
            putExtra("email", email)
        }
        startActivity(homeIntent);
    }

}