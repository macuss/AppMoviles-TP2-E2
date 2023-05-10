package com.ncabanes.sqlite01

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.ncabanes.sqlite01.databinding.ActivityBccpBinding

class BorrarConsultarCN : AppCompatActivity() {

    private lateinit var binding: ActivityBccpBinding
    private lateinit var capitalesDBHelper: MySQLiteHelper

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBccpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        capitalesDBHelper = MySQLiteHelper(this)

        binding.btcConsultar1.setOnClickListener {
            binding.tvConsulta1.text = ""
            val ciudad = binding.etCiudad.text.toString()
            val cursor = consultarCiudades(ciudad)

            cursor?.let {
                if (it.moveToFirst()) {
                    do {
                        binding.tvConsulta1.append("País: " + it.getString(0) + ", ")
                        binding.tvConsulta1.append("Ciudad: " + it.getString(1) + ", ")
                        binding.tvConsulta1.append("Población: " + it.getString(2) + "\n")
                    } while (it.moveToNext())
                } else {
                    binding.tvConsulta1.text = "Ciudad no encontrada"
                }
                it.close()
            }
            val animatorSet =
                AnimatorInflater.loadAnimator(this, R.animator.scale_button) as AnimatorSet
            animatorSet.setTarget(binding.btcConsultar1)
            animatorSet.start()
        }

        binding.btcBorrar1.setOnClickListener {
            Log.d("Borrar", "Botón de borrar presionado")
            val ciudad = binding.etCiudad.text.toString()
            Log.d("Borrar", "Ciudad a borrar: $ciudad")
            val db = capitalesDBHelper.writableDatabase
            val whereClause = "nombreCiudad = ?"
            val whereArgs = arrayOf(ciudad)
            val deletedRows = db.delete("ciudades", whereClause, whereArgs)
            Log.d("Borrar", "Filas eliminadas: $deletedRows")
            if (deletedRows > 0) {
                binding.tvConsulta1.text = "Ciudad eliminada correctamente"
            } else {
                binding.tvConsulta1.text = "No se encontró la ciudad a eliminar"
            }
            // Animación de escala sobre el botón
            val animatorSet =
                AnimatorInflater.loadAnimator(this, R.animator.scale_button) as AnimatorSet
            animatorSet.setTarget(binding.btcBorrar1)
            animatorSet.start()
        }
    }

    private fun consultarCiudades(ciudad: String): Cursor? {
        val db = capitalesDBHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT paises.nombrePais, ciudades.nombreCiudad, ciudades.poblacion " +
                    "FROM ciudades JOIN paises ON ciudades.idpais=paises._id " +
                    "WHERE ciudades.nombreCiudad=?",
            arrayOf(ciudad)
        )

        // Comprueba si la consulta devolvió algún resultado
        if (cursor.count == 0) {
            cursor.close()
            return null
        }

        // Asegúrate de que el cursor apunte a la primera fila
        cursor.moveToFirst()

        return cursor
    }
}
