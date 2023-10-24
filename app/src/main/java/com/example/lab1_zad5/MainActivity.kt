package com.example.lab1_zad5

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager

class MainActivity : AppCompatActivity() {

    private val REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, "com.example.lab1.permission.READ_USER_DATA")
            != PackageManager.PERMISSION_GRANTED) {
            // Jeśli uprawnienie nie jest przyznane, proś o nie
            ActivityCompat.requestPermissions(this, arrayOf("com.example.lab1.permission.READ_USER_DATA"), REQUEST_CODE)
        } else {
            // Jeśli uprawnienie jest już przyznane, kontynuuj z odpytywaniem ContentProvider
            fetchDataFromProvider()
        }
    }

    @SuppressLint("Range")
    private fun fetchDataFromProvider() {
        try {
            val CONTENT_URI = Uri.parse("content://com.example.lab1.provider/user")
            val cursor: Cursor? = contentResolver.query(CONTENT_URI, null, null, null, null)

            cursor?.let {
                if (it.getColumnIndex("id") != -1 && it.getColumnIndex("username") != -1 && it.getColumnIndex("stopperValue") != -1) {
                    while (it.moveToNext()) {
                        val id = it.getInt(it.getColumnIndex("id"))
                        val username = it.getString(it.getColumnIndex("username"))
                        val stopperValue = it.getInt(it.getColumnIndex("stopperValue"))
                        Log.d("DrugaAplikacja", "Użytkownik: $id, $username, $stopperValue")
                    }
                } else {
                    Log.e("DrugaAplikacja", "Oczekiwane kolumny nie zostały znalezione w kursorze.")
                }
            }
            cursor?.close()
        } catch (e: SecurityException) {
            Log.e("DrugaAplikacja", "Brak uprawnienia lub ContentProvider nie został znaleziony", e)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // uprawnienie zostało przyznane, kontynuuj odpytywanie ContentProvider
                    fetchDataFromProvider()
                } else {
                    // uprawnienie zostało odrzucone, możesz dodać odpowiednią reakcję
                    Log.e("DrugaAplikacja", "Uprawnienie odczytu danych zostało odrzucone.")
                }
                return
            }
            else -> {
                // Inne przypadki, jeśli istnieją
            }
        }
    }
}
