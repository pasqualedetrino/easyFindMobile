package com.example.parsaniahardik.spinner_json_volley;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

import static android.R.layout.simple_spinner_item;

public class MainActivity extends AppCompatActivity {

    private String URLcategorie = "http://ec2-3-83-214-110.compute-1.amazonaws.com:5000/api/categorie";
    private static ProgressDialog mProgressDialog;
    private ArrayList<infoCategoria> infoCategoriaArrayList;
    private ArrayList<String> names = new ArrayList<String>();
    private Spinner spinnerCategoria;

    // button per avviare nuove activity
    private Button bt1;
    private Button bt2;
    private Button bt3;


    // spinner prodotto
    private Spinner spinnerProdotto;

    private ArrayList<Prodotto> ProdottoArrayList;
    private ArrayList<String> namesProdotto = new ArrayList<String>();

    private String prodottoSelezionato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerCategoria = findViewById(R.id.spCompany);    // spinner per selezionare la categoria

        spinnerProdotto = findViewById(R.id.spProdotto);    // spinner per selezionare il prodotto

        bt1 = findViewById(R.id.bt1);
        bt2 = findViewById(R.id.bt2);

        selectCategoria();

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                intent.putExtra("prodottoSelez",prodottoSelezionato);
                startActivity(intent);
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Main3Activity.class);
                intent.putExtra("prodottoSelez",prodottoSelezionato);
                startActivity(intent);
            }
        });


    }

    private void selectCategoria() {

        showSimpleProgressDialog(this, "Loading...","Fetching Json",false);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLcategorie, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("strrrrr", ">>" + response);

                try {

                    JSONObject obj = new JSONObject(response);

                        infoCategoriaArrayList = new ArrayList<>();
                        JSONArray dataArray  = obj.getJSONArray("item");

                        for (int i = 0; i < dataArray.length(); i++) {

                            infoCategoria playerModel = new infoCategoria();
                            JSONObject dataobj = dataArray.getJSONObject(i);

                            playerModel.setCategoria(dataobj.getString("categoria"));

                            infoCategoriaArrayList.add(playerModel);

                        }

                        for (int i = 0; i < infoCategoriaArrayList.size(); i++){
                            names.add(infoCategoriaArrayList.get(i).getCaregoria());

                        }

                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(MainActivity.this, simple_spinner_item, names);
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCategoria.setAdapter(spinnerArrayAdapter);

                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // Set Adapter in the spinner
                        spinnerCategoria.setAdapter(spinnerArrayAdapter);
                        spinnerCategoria.setOnItemSelectedListener(
                        new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                String state = parentView.getItemAtPosition(position).toString();
                                selectArticolo(state);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parentView) {}
                        });

                        removeSimpleProgressDialog();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(stringRequest);

    }

    public static void removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();

        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void showSimpleProgressDialog(Context context, String title, String msg, boolean isCancelable) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg);
                mProgressDialog.setCancelable(isCancelable);
            }

            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }

        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // funzione che cercherà i prodotti in base alla categoria selezionata
    private void selectArticolo(String ris)
    {
        // clear dello spinner
        spinnerProdotto.setAdapter(null);
        if(namesProdotto != null)
            namesProdotto.clear();


        Log.d("VALORE ", ">>" + ris);

        showSimpleProgressDialog(this, "Loading...","Fetching Json",false);

        String URLProdotto = "http://ec2-3-83-214-110.compute-1.amazonaws.com:5000/api/prodotticategoria/" + ris;
        Log.d("VORREI ", ">>" + URLProdotto);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLProdotto, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject obj = new JSONObject(response);

                    ProdottoArrayList = new ArrayList<>();
                    JSONArray dataArray  = obj.getJSONArray("item");

                    for (int i = 0; i < dataArray.length(); i++) {

                        Prodotto prod = new Prodotto();
                        JSONObject dataobj = dataArray.getJSONObject(i);

                        prod.setnomeProdotto(dataobj.getString("nome_prodotto"));

                        ProdottoArrayList.add(prod);

                    }

                    for (int i = 0; i < ProdottoArrayList.size(); i++){
                        namesProdotto.add(ProdottoArrayList.get(i).getnomeProdotto());

                    }

                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(MainActivity.this, simple_spinner_item, namesProdotto);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerProdotto.setAdapter(spinnerArrayAdapter);

                    spinnerArrayAdapter.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item);
                    // Set Adapter in the spinner
                    spinnerProdotto.setAdapter(spinnerArrayAdapter);
                    spinnerProdotto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                           String state = parentView.getItemAtPosition(position).toString();
                           prodottoSelezionato = state;
                       }
                       @Override
                       public void onNothingSelected(AdapterView<?> parentView) {}
                    });
                    removeSimpleProgressDialog();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        requestQueue.add(stringRequest);

    }
}
