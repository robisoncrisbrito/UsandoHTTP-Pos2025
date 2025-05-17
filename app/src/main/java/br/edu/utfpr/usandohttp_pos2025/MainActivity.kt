package br.edu.utfpr.usandohttp_pos2025

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView
    private lateinit var tvResposta: TextView

    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvLatitude = findViewById(R.id.tvLatitude)
        tvLongitude = findViewById(R.id.tvLongitude)
        tvResposta = findViewById(R.id.tvResposta)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, this)

    }

    fun btVerEnderecoOnClick(view: View) {

        val applicationContext = applicationContext
        val apiKey = applicationContext.packageManager.getApplicationInfo(
            applicationContext.packageName,
            PackageManager.GET_META_DATA
        ).metaData.getString("com.google.android.geo.API_KEY")

        Thread {
            val endereco =
                "https://maps.googleapis.com/maps/api/geocode/xml?latlng=${tvLatitude.text},${tvLongitude.text}&key=AIzaSyCMzWccWPPD5Q8mKmyk0AVx3e-_SgTakpA"

            val url = URL(endereco)
            val conn = url.openConnection()
            //val dados = conn.getInputStream().bufferedReader().readText()

            val inputStream = conn.getInputStream()
            val entrada = BufferedReader(InputStreamReader( inputStream ) )

            val resposta = StringBuilder()

            var linha = entrada.readLine()

            while ( linha != null ) {
                resposta.append(linha)
                linha = entrada.readLine()
            }

            runOnUiThread {

                val formattedAddress = resposta.substring(
                    resposta.indexOf("<formatted_address>") + 19,
                    resposta.indexOf("</formatted_address>")
                )

                tvResposta.text = formattedAddress.toString()
            }

        }.start()

    }

    override fun onLocationChanged(location: Location) {
        tvLatitude.text = location.latitude.toString()
        tvLongitude.text = location.longitude.toString()
    }

}