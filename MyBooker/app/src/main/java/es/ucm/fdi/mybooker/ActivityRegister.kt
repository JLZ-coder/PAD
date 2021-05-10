package es.ucm.fdi.mybooker

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

open class ActivityRegister : AppCompatActivity() {

    // TODO: HAbrá q estructurar esto bien. Era puna prueba para el registro de usuarios

    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()

    private lateinit var userMail: EditText
    private lateinit var userPass: EditText
    private lateinit var userConfirmPass: EditText
    private lateinit var userName: EditText
    private lateinit var labelEmpresas: TextView
    private lateinit var btnRegister: Button
    //Empresa
    private lateinit var location: EditText
    private lateinit var cp: EditText
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        userMail = findViewById<EditText>(R.id.editTextUserMail)
        userPass = findViewById<EditText>(R.id.editTextPassword)
        userConfirmPass = findViewById<EditText>(R.id.editTextConfirmPassword)
        userName = findViewById<EditText>(R.id.editTextUserName)
        labelEmpresas = findViewById<TextView>(R.id.label_empresas)
        btnRegister = findViewById<Button>(R.id.btnRegisterUser)

        //Empresa
        location = findViewById<EditText>(R.id.editTextLocation)
        cp = findViewById<EditText>(R.id.editTextCodePostal)
        spinner = findViewById<Spinner>(R.id.categoria)
        val lista = resources.getStringArray(R.array.categorias)
        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, lista)
        spinner.adapter = adaptador


        setUp()
    }

    private fun setUp()
    {
        title = "Register"

        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("mail")
        userMail.setText(email ?: userMail.text.toString())
        val tipoUsuario = bundle?.getString("tipoUsuario")
        if  (tipoUsuario == "usuario") {
            setUpUsuario()
        }
        else if (tipoUsuario == "empresa") {
            setUpEmpresa()
        }
    }

    private fun setUpUsuario()
    {

        btnRegister.setOnClickListener {
            if (userName.text.isNotEmpty() && userPass.text.isNotEmpty() && userConfirmPass.text.isNotEmpty() && userMail.text.isNotEmpty() && userConfirmPass.text.toString() == userPass.text.toString()) {

                mAuth.createUserWithEmailAndPassword(
                    userMail.text.toString(),
                    userPass.text.toString()
                ).addOnCompleteListener{
                    if (it.isSuccessful) {
                        val user_uid = mAuth.currentUser.uid
                        db.collection("users").document(user_uid).set(
                            mapOf(
                                "name" to userName.text.toString(),
                                "email" to userMail.text.toString(),
                                "tipoUsuario" to "usuario"
                            )
                        )
                        showUserInfo(it.result?.user?.email ?: "", ProviderType.MAIL, userName.text.toString())
                    } else {
                        showAlert(it.exception!!);
                    }
                }
            }else{
                showErrorsCommon()
            }
        }
    }

    private fun showErrorsCommon(){
        if(TextUtils.isEmpty(userName.text.toString())) {
            userName.error = "Rellene este campo"
        }
        if(TextUtils.isEmpty(userPass.text.toString())){
            userPass.error = "Rellene este campo"
        }
        if(TextUtils.isEmpty(userMail.text.toString())){
            userMail.error = "Rellene este campo"
        }
        if(userConfirmPass.text.toString() != userPass.text.toString()){
            userConfirmPass.error = "Contraseña no coincide"
        }
    }

    private fun setUpEmpresa() {
        labelEmpresas.visibility = View.VISIBLE
        location.visibility = View.VISIBLE
        cp.visibility = View.VISIBLE
        spinner.visibility = View.VISIBLE
        btnRegister.setOnClickListener {
            if (userName.text.isNotEmpty() && userPass.text.isNotEmpty() && userConfirmPass.text.isNotEmpty()
                && userMail.text.isNotEmpty() && userConfirmPass == userPass) {

                mAuth.createUserWithEmailAndPassword(
                    userMail.text.toString(),
                    userPass.text.toString()
                ).addOnCompleteListener{
                    if (it.isSuccessful) {
                        val user_uid = mAuth.currentUser.uid
                        db.collection("users").document(user_uid).set(
                            mapOf(
                                "name" to userName.text.toString(),
                                "email" to userMail.text.toString(),
                                "tipoUsuario" to "empresa"
                            )
                        )
                        db.collection("enterprises").document(user_uid).set(
                            mapOf(
                                "name" to userName.text.toString(),
                                "email" to userMail.text.toString(),
                                "category" to spinner.getSelectedItem().toString() ,
                                "location" to location.text.toString(),
                                "cp" to cp.text.toString()
                            )
                        )
                        showEmpresaInfo(it.result?.user?.email ?: "", ProviderType.MAIL, userName.text.toString())
                    } else {
                        showAlert(it.exception!!);
                    }
                }
            }else{
                showErrorsCommon()
            }
        }
    }

    private fun showAlert(e: Exception)
    {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage(e.message.toString())
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showUserInfo(email: String, provider: ProviderType, userName: String)
    {
        // TODO: Nos vamos a ir a la info del usuario cndo haga login, o a la empresa que clique, pero eso hay q mirarlo bien
        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("name", userName)
        }

        startActivity(homeIntent);
    }

    private fun showEmpresaInfo(email: String, provider: ProviderType, userName: String)
    {
        // TODO: Nos vamos a ir a la info del usuario cndo haga login, o a la empresa que clique, pero eso hay q mirarlo bien
        val homeIntent = Intent(this, EmpresaReservasActivity::class.java).apply {
            putExtra("email", email)
            putExtra("name", userName)
        }

        startActivity(homeIntent);
    }
}