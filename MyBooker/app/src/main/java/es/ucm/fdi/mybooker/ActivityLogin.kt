package es.ucm.fdi.mybooker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception
import java.lang.IllegalArgumentException

class ActivityLogin : AppCompatActivity()
{

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
                                    val tipo = document["tipoUsuario"].toString()
                                    val name = document["name"].toString()
                                    val email = document["email"].toString()
                                    val image = document["profileImg"].toString()
                                    when (tipo) {
                                        "usuario" -> showUserInfo(user_uid,name,email, image)
                                        "empresa" -> showEmpresaInfo(user_uid)
                                        else -> FirebaseCrashlytics.getInstance().recordException(Exception("login_getType, No such type $tipo"))
                                    }
                                } else {
                                    FirebaseCrashlytics.getInstance().recordException(Exception("login_getDocument, No such document $document"))
                                }
                            }
                            .addOnFailureListener { exception ->
                                enableLogin()
                                FirebaseCrashlytics.getInstance().recordException(Exception("login_getDocument, get failed with ${exception.message.toString()}"))
                            }
                    } else {
                        showAlert(it.exception?.localizedMessage)
                    }
                }
            } else {
                showAlert("Usuario y contraseñas no rellenados")
            }
        }
    }

    private fun enableLogin()
    {
        login.isEnabled = true
        loadingLogin.visibility = View.GONE
    }

    private fun showAlert(message: String?)
    {

        enableLogin()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage(message.toString())
        builder.setNegativeButton("Reintentar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showUserInfo(userId : String, name: String, email: String, image: String)
    {

        val homeIntent = Intent(this, MainActivity::class.java)
        homeIntent.putExtra("userName", name)
        homeIntent.putExtra("email", email)
        homeIntent.putExtra("profileImg", image)

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

        val homeIntent = Intent(this, EnterpriseMainActivity::class.java).apply {
            putExtra("userId", userId)
            putExtra("userName", name)
            putExtra("email", email)
        }
        startActivity(homeIntent);
    }
}