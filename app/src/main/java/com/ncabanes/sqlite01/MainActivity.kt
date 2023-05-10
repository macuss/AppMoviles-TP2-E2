package com.ncabanes.sqlite01

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ncabanes.sqlite01.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var capitalesDBHelper: MySQLiteHelper
    private var mToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
        initListeners()
    }

    private fun initComponents() {
        capitalesDBHelper = MySQLiteHelper(this)
    }

    @SuppressLint("SetTextI18n")
    private fun initListeners() {
        binding.btGuardar.setOnClickListener {
            if (binding.etNombrePais.text.isNotBlank() &&
                binding.etNombreCiudad.text.isNotBlank() && binding.etPoblacion.text.isNotBlank()
            ) {
                capitalesDBHelper.anadirDatos(
                    binding.etNombrePais.text.toString(),
                    binding.etNombreCiudad.text.toString(),
                    binding.etPoblacion.text.toString().toInt()
                )
                limpiarCampos()
                notificar("Guardado")
            } else {
                notificar("No se ha podido guardar ,ingrese todos los valores")
            }
        }

        binding.btConsultar.setOnClickListener {
            binding.tvConsulta.text = ""
            val cursor = consultarCiudades()

            if (cursor.moveToFirst()) {
                do {
                    binding.tvConsulta.append("IDciu: " + cursor.getInt(0).toString() + ", ")
                    binding.tvConsulta.append("País: " + cursor.getString(1).toString() + ", ")
                    binding.tvConsulta.append("Ciudad: " + cursor.getString(2).toString() + ", ")
                    binding.tvConsulta.append("Población: " + cursor.getString(3).toString() + "\n")
                } while (cursor.moveToNext())
            } else {
                binding.tvConsulta.text = "No se encontraron ciudades"
            }
        }

        binding.btcBorrarCP.setOnClickListener {
            navegarBorrarCP()
        }

        binding.btModificar.setOnClickListener {
            navegarModificar()
        }

        binding.btcBorrarCN.setOnClickListener {
            navegarBorrarCN()
        }
    }

    private fun limpiarCampos() {
        binding.etNombrePais.text.clear()
        binding.etNombreCiudad.text.clear()
        binding.etPoblacion.text.clear()
    }

    fun notificar(text: String) {
        mToast?.cancel()
        mToast = Toast.makeText(
            applicationContext,
            text,
            Toast.LENGTH_SHORT
        )
        mToast?.show()
    }

    private fun consultarCiudades(): Cursor {
        val db = capitalesDBHelper.readableDatabase
        db.beginTransaction()
        try {
            return db.rawQuery(
                "SELECT ciudades._id, paises.nombrePais, ciudades.nombreCiudad, ciudades.poblacion FROM ciudades JOIN paises ON ciudades.idpais=paises._id",
                null
            )
        } finally {
            db.setTransactionSuccessful()
            db.endTransaction()
        }
    }

    fun navegarModificar() {
        val intent = Intent(this, ModActivity::class.java)
        startActivity(intent)
    }

    fun navegarBorrarCP() {
        val intent = Intent(this, BorrarCP::class.java)
        startActivity(intent)
    }

    fun navegarBorrarCN() {
        val intent = Intent(this, BorrarConsultarCN::class.java)
        startActivity(intent)
    }

}
