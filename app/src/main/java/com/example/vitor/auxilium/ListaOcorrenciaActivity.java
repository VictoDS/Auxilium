package com.example.vitor.auxilium;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ListaOcorrenciaActivity extends AppCompatActivity {

    private String texto = "";
    private String flagRet = "N";

    private String enderecoGoogle(double lat, double lng){//-28.681607,-49.37345997
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://maps.googleapis.com/maps/api/geocode/json?latlng="+lat+","+lng).build();

        final String[] endereco = {""};

        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String conteudo = response.body().string();
                try {
                    JSONObject json = new JSONObject(conteudo);
                    endereco[0] = json.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        client.newCall(request).enqueue(callback);

        int i = 0;
        while (endereco[0].equals("")){
            if (i==0){
                Log.i("PROC","Carregando endereço...");
            }
            i++;
        }

        Log.i("PROC","Endereço carregado...");

        return endereco[0];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_ocorrencia);

        final TextView conteudo = (TextView) findViewById(R.id.textConteudo);
        conteudo.setText("");

        OkHttpClient client = new OkHttpClient();

        MediaType media = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();

        try {
            json.put("id_usuario", AuxiliumHome.usuario.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String req = json.toString();
        RequestBody body = RequestBody.create(media,req);

        Request request = new Request.Builder().url("https://seilaeu.herokuapp.com/lista-ocorrencia").post(body).build();

        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String conteudo = response.body().string();

                if (conteudo.equals("[]")){
                    texto = "O usuário não possui registro de ocorrências pelo Auxilium.";
                    flagRet = "S";
                }else{
                    try {
                        JSONArray jsonArray = new JSONArray(conteudo);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String status = "";
                            switch (jsonArray.getJSONObject(i).getString("status")){
                                case "P": status = "Pendente";
                                    break;
                                case "R": status = "Reprovado";
                                    break;
                                case "E": status = "Encerrado";
                                    break;
                            }
                            texto += "Momento: "+jsonArray.getJSONObject(i).getString("momento")+"\n" +
                                    "Local: "+enderecoGoogle(jsonArray.getJSONObject(i).getDouble("lat"),
                                    jsonArray.getJSONObject(i).getDouble("lng"))+"\n"+
                                    "Tipo: "+jsonArray.getJSONObject(i).getString("tip_ocorrencia")+"\n"+
                                    "Descrição: "+jsonArray.getJSONObject(i).getString("descricao")+"\n"+
                                    "Status: "+status+"\n\n\n";
                        }
                        flagRet = "S";
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        client.newCall(request).enqueue(callback);

        int i = 0;
        while (flagRet.equals("N")){
            if (i==0){
                Log.i("PROC","Carregando...");
            }
            i++;
        }

        conteudo.setText(texto);
    }
}
