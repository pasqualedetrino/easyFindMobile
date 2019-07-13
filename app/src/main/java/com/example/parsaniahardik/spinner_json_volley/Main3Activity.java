package com.example.parsaniahardik.spinner_json_volley;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Main3Activity extends AppCompatActivity implements LocationListener {

    private CoordinatorLayout mCLayout;

    private String message, latitude, longitude;
    private double latitudeDouble, longitudeDouble;

    LocationManager locationManager;

    private ArrayList<ProdottoCitta> ProdottoCittaArrayList;

    private TextView mTextView;
    private TextView tView;

    SeekBar SeekBarRaggio;

    private int raggio;
    private String raggioStringa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        Intent intent = getIntent();
        message = intent.getStringExtra("prodottoSelez");

        mTextView = (TextView) findViewById(R.id.tv);


        SeekBarRaggio=(SeekBar)findViewById(R.id.simpleSeekBar);

        tView = (TextView) findViewById(R.id.textview1);
        tView.setText(SeekBarRaggio.getProgress() + "/" + SeekBarRaggio.getMax());

        mTextView.setText("");

        final Location location = new Location(locationManager.GPS_PROVIDER);

        SeekBarRaggio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;

                raggio = progress;

                raggioStringa = String.valueOf(raggio);

                cercaCoordinate();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(Main3Activity.this, "Seek bar progress is :" + progressChangedValue, Toast.LENGTH_SHORT);
                tView.setText(progressChangedValue + "/" + seekBar.getMax());
            }
        });

        getLocation();

        onLocationChanged(location);

        // se non dovesse trovare la posizione setta questa
        if(latitudeDouble == 0.0 || longitudeDouble == 0.0)
            latitude = Double.toString(40.88304660000001);
            longitude = Double.toString(14.3090712);

    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = Double.toString(location.getLatitude());
        longitude = Double.toString(location.getLongitude());

        latitudeDouble = location.getLatitude();
        longitudeDouble = location.getLongitude();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(Main3Activity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}


    private void cercaCoordinate() {

        mTextView.setText("");

        String URLProdotto = "http://ec2-3-83-214-110.compute-1.amazonaws.com:5000/api/prodottoCoordinatePrezzo/" + message + "/" +  latitude + "/" + longitude + "/" + raggioStringa;

        Log.d("latitude", latitude);
        Log.d("longitude", longitude);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLProdotto, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject obj = new JSONObject(response);

                            ProdottoCittaArrayList = new ArrayList<>();
                            JSONArray dataArray = obj.getJSONArray("item");

                            for (int i = 0; i < dataArray.length(); i++) {

                                ProdottoCitta prodCitta = new ProdottoCitta();
                                JSONObject dataobj = dataArray.getJSONObject(i);

                                prodCitta.setNome_v(dataobj.getString("nome_v"));
                                prodCitta.setPrezzo(dataobj.getString("prezzo"));
                                prodCitta.setIndirizzo(dataobj.getString("indirizzo"));
                                prodCitta.setQuantita(dataobj.getString("quantita"));

                                ProdottoCittaArrayList.add(prodCitta);

                            }

                            for (int i = 0; i < ProdottoCittaArrayList.size(); i++) {
                                mTextView.append("NOME VENDITORE: " + ProdottoCittaArrayList.get(i).getNome_v() + "\n");
                                mTextView.append("INDIRIZZO: " + ProdottoCittaArrayList.get(i).getIndirizzo() + "\n");
                                mTextView.append("PREZZO: " + ProdottoCittaArrayList.get(i).getPrezzo() + "\n");
                                mTextView.append("QUANTITA': " + ProdottoCittaArrayList.get(i).getQuantita() + "\n");
                                mTextView.append("\n\n");

                            }


                            // Display the formatted json data in text view

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Do something when error occurred
                        Snackbar.make(
                                mCLayout,
                                "Error...",
                                Snackbar.LENGTH_LONG
                        ).show();
                    }
                }
        );


        // Add JsonArrayRequest to the RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}

