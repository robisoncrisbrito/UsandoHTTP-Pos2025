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
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONObject
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

        val endereco =
            "https://maps.googleapis.com/maps/api/geocode/json?latlng=${tvLatitude.text},${tvLongitude.text}&key=AIzaSyCMzWccWPPD5Q8mKmyk0AVx3e-_SgTakpA"

        val requestQueue = Volley.newRequestQueue(this)

        val request = StringRequest(
            Request.Method.GET,
            endereco,
            { resposta : String ->
                val jsonObject = parseJson(resposta)

                var rua = ""

                if ( jsonObject != null ) {
                    val results = jsonObject.getAsJsonArray( "results" )
                    if ( results != null  && results.size() > 0 ) {
                        val result = results.get(0).asJsonObject
                        rua = result.get("formatted_address").asString
                    }
                }
                tvResposta.text = rua.toString()
            },
            { erro ->
                tvResposta.text = erro.toString()
            }
        )

        requestQueue.add( request )

    } // fim do btVerEnderecoOnClick

    private fun parseJson(json: String): JsonObject? {
        val jsonElement = JsonParser.parseString( json )

        if ( jsonElement.isJsonObject() ) {
            return jsonElement.asJsonObject
        } else {
            return null
        }
    }


    override fun onLocationChanged(location: Location) {
        tvLatitude.text = location.latitude.toString()
        tvLongitude.text = location.longitude.toString()
    }

}