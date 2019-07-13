package com.example.parsaniahardik.spinner_json_volley;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class Main2Activity extends AppCompatActivity {


    private CoordinatorLayout mCLayout;
    private TextView mTextView;


    private ArrayList<ProdottoCitta> ProdottoCittaArrayList;

    private Button bt1;

    private EditText insertCitta;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        setTitle("SELEZIONA UNA CITTA' E TROVERO' IL PRODOTTO");

        Intent intent = getIntent();
        message = intent.getStringExtra("prodottoSelez");

        mTextView = (TextView) findViewById(R.id.tv);

        mTextView.setText("");

        bt1 = (Button) findViewById(R.id.buttonID);

        insertCitta = (EditText) findViewById(R.id.Textcitta);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cittaCercare = insertCitta.getText().toString().toUpperCase();

                Log.d("voglio cercare ", cittaCercare);

                if(cittaCercare.isEmpty())
                    Toast.makeText(Main2Activity.this, "Inserisci una città", Toast.LENGTH_SHORT).show();
                else
                    cercaCitta(cittaCercare);

            }
        });
    }


    private void cercaCitta (String cercaCitta){
        // clear della textview
        mTextView.setText("");

        String URLProdotto = "http://ec2-3-83-214-110.compute-1.amazonaws.com:5000/api/prodottoCitta/" + cercaCitta + "/" +  message;
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}

