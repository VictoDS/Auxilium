package com.example.vitor.auxilium;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuxiliumHome extends AppCompatActivity {

    public static final Usuario usuario = new Usuario();

    private double lat;
    private double lng;

    private void carregaUsuario(String token){
        OkHttpClient client = new OkHttpClient();

        MediaType media = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();

        try {
            json.put("token",token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String req = json.toString();
        RequestBody body = RequestBody.create(media,req);

        Request request = new Request.Builder().url("https://seilaeu.herokuapp.com/usuario").post(body).build();

        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String conteudo = response.body().string();
                Log.e("JSON",conteudo);
                if (conteudo.equals("[]")) {
                    usuario.setStatus("E");
                }else{
                    JSONArray jsonA = null;
                    try {
                        jsonA = new JSONArray(conteudo);
                        int id = jsonA.getJSONObject(0).getInt("id");
                        String nome = jsonA.getJSONObject(0).getString("nome");
                        String cpf = jsonA.getJSONObject(0).getString("cpf");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date dtNascimento = sdf.parse(jsonA.getJSONObject(0).getString("dt_nascimento"));
                        String fone = jsonA.getJSONObject(0).getString("fone");
                        String endereco = jsonA.getJSONObject(0).getString("endereco");
                        String bairro = jsonA.getJSONObject(0).getString("bairro");
                        String cidade = jsonA.getJSONObject(0).getString("cidade");
                        String uf = jsonA.getJSONObject(0).getString("uf");
                        String status = jsonA.getJSONObject(0).getString("status");
                        String token = jsonA.getJSONObject(0).getString("token");
                        String dsProblema = jsonA.getJSONObject(0).getString("ds_problema_saude");
                        String problema = jsonA.getJSONObject(0).getString("problema_saude");

                        usuario.setId(id);
                        usuario.setNome(nome);
                        usuario.setCpf(cpf);
                        usuario.setDtNascimento(dtNascimento);
                        usuario.setFone(fone);
                        usuario.setEndereco(endereco);
                        usuario.setBairro(bairro);
                        usuario.setCidade(cidade);
                        usuario.setUf(uf);
                        usuario.setStatus(status);
                        usuario.setToken(token);
                        usuario.setProblemaSaude(problema);
                        usuario.setDsProblemaSaude(dsProblema);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        client.newCall(request).enqueue(callback);
    }

    private void msgErro (String msg){
        AlertDialog.Builder erro = new AlertDialog.Builder(AuxiliumHome.this);
        erro.setTitle("Erro!");
        erro.setMessage(msg);
        erro.setNeutralButton("OK",null);
        erro.show();
    }

    private void realizarChamada(String numero, Ocorrencia ocorrencia){
        OkHttpClient client = new OkHttpClient();

        MediaType media = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();

        try {
            json.put("id_usuario",ocorrencia.getUsuario().getId());
            json.put("latitude",ocorrencia.getLat());
            json.put("longitude",ocorrencia.getLng());
            json.put("tipo",ocorrencia.getTpOcorrencia());
            json.put("descricao",ocorrencia.getDsOcorrencia());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String req = json.toString();
        RequestBody body = RequestBody.create(media,req);

        Request request = new Request.Builder().url("https://seilaeu.herokuapp.com/nova-ocorrencia").post(body).build();

        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String conteudo = response.body().string();
                JSONObject jsonO = null;
                try {
                    jsonO = new JSONObject(conteudo);
                    String descricao = jsonO.getString("descricao");
                    if (jsonO.getString("status").equals("ERRO")){
                        msgErro("Falha ao gravar ocorrência.\n"+descricao);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        client.newCall(request).enqueue(callback);

        Intent chamada = new Intent(Intent.ACTION_CALL);
        chamada.setData(Uri.parse("tel:"+numero));
        if (ActivityCompat.checkSelfPermission(AuxiliumHome.this, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED) {
            msgErro("Permissão de chamada não liberada!");
            return;
        }
        startActivity(chamada);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.meusDados:
                Intent callFormUsuario = new Intent(AuxiliumHome.this,UsuarioActivity.class);
                startActivity(callFormUsuario);
                break;
            case R.id.meusChamados:
                Intent callListaOcorrencia = new Intent(AuxiliumHome.this,ListaOcorrenciaActivity.class);
                startActivity(callListaOcorrencia);
                break;
            case R.id.ajuda:
                Intent callHelp = new Intent(AuxiliumHome.this,ActivityHelp.class);
                startActivity(callHelp);
                break;
            default: msgErro("Opção não configurada.");
        }

        return true;
    }

    public void atualizar(Location location){
        lat = location.getLatitude();
        lng = location.getLongitude();

        Log.i("LOC",lat+" , "+lng);
    }

    public void configurarServico(){
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    if (lat==0||lng==0) {
                        atualizar(location);
                    }else{
                        return;
                    }
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                public void onProviderEnabled(String provider) {

                }

                public void onProviderDisabled(String provider) {

                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }catch(SecurityException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    configurarServico();
                } else {
                    AlertDialog.Builder erro = new AlertDialog.Builder(AuxiliumHome.this);
                    erro.setTitle("Erro!");
                    erro.setMessage("Permissão de acesso á localização necessária!\nSaindo do aplicativo...");
                    erro.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });
                    erro.show();
                }
                return;
            }
            case 2: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder erro = new AlertDialog.Builder(AuxiliumHome.this);
                    erro.setTitle("Erro!");
                    erro.setMessage("Permissão para realizar chamadas necessária!\nSaindo do aplicativo...");
                    erro.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });
                    erro.show();
                }
                return;
            }
            case 3: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder erro = new AlertDialog.Builder(AuxiliumHome.this);
                    erro.setTitle("Erro!");
                    erro.setMessage("Permissão de acesso á internet necessária!\nSaindo do aplicativo...");
                    erro.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    });
                    erro.show();
                }
                return;
            }
        }
    }

    private void pedirPermissoes() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        if (ActivityCompat.checkSelfPermission(AuxiliumHome.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AuxiliumHome.this, new String[]{Manifest.permission.CALL_PHONE},2);
        }
        if (ActivityCompat.checkSelfPermission(AuxiliumHome.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AuxiliumHome.this, new String[]{Manifest.permission.INTERNET},3);
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean GPSAtivado = false;
        GPSAtivado = locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER);

        if (GPSAtivado) {
            configurarServico();
        }else {
            AlertDialog.Builder erro = new AlertDialog.Builder(AuxiliumHome.this);
            erro.setTitle("Erro!");
            erro.setMessage("GPS desativado! Favor habilitar o GPS e entrar novamente.\nSaindo do aplicativo...");
            erro.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
            erro.setCancelable(false);
            erro.show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auxilium_home);

        pedirPermissoes();

        final String androidId = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        carregaUsuario(androidId);

        final Button btNovaOcorrencia = (Button) findViewById(R.id.btNovaOcorrencia);
        final Button btPanico         = (Button) findViewById(R.id.btPanico);

        final Button btCancelar  = (Button) findViewById(R.id.btCancelar);
        final Button btConfirmar = (Button) findViewById(R.id.btConfirmar);

        final Button btAcidenteTransito  = (Button) findViewById(R.id.btAcidenteTransito);
        final Button btAtendimentoMedico = (Button) findViewById(R.id.btAtendimentoMedico);
        final Button btIncendio          = (Button) findViewById(R.id.btIncendio);
        final Button btSalvamento        = (Button) findViewById(R.id.btSalvamento);
        final Button btOutros            = (Button) findViewById(R.id.btOutros);

        final TextView lbDinamica = (TextView) findViewById(R.id.lbDinamico);

        final Switch swMotos    = (Switch) findViewById(R.id.swMotos);
        final Switch swCarros   = (Switch) findViewById(R.id.swCarros);
        final Switch swCaminhao = (Switch) findViewById(R.id.swCaminhao);
        final Switch swCarretas = (Switch) findViewById(R.id.swCarretas);
        final Switch swOnibus   = (Switch) findViewById(R.id.swOnibusVans);

        final Switch swQuimico  = (Switch) findViewById(R.id.swQuimico);

        final RadioButton rb1 = (RadioButton) findViewById(R.id.rb1);
        final RadioButton rb2 = (RadioButton) findViewById(R.id.rb2);
        final RadioButton rb3mais = (RadioButton) findViewById(R.id.rb3Mais);

        final TextView   lbVitimas     = (TextView) findViewById(R.id.lbVitimas);
        final RadioGroup rgroupVitimas = (RadioGroup) findViewById(R.id.rgroupVitimas);

        final Switch swFraturaExposta = (Switch) findViewById(R.id.swFraturaExposta);
        final Switch swQueimadura = (Switch) findViewById(R.id.swQueimadura);
        final Switch swMalSubito = (Switch) findViewById(R.id.swMalSubito);
        final Switch swDesmaio = (Switch) findViewById(R.id.swDesmaio);
        final Switch swMalEstar = (Switch) findViewById(R.id.swMalEstar);
        final Switch swEnvenenamento = (Switch) findViewById(R.id.swEnvenenamento);

        final Switch swCasa     = (Switch) findViewById(R.id.swCasa);
        final Switch swPredio   = (Switch) findViewById(R.id.swPredio);
        final Switch swComercio = (Switch) findViewById(R.id.swComercio);
        final Switch swFloresta = (Switch) findViewById(R.id.swFloresta);

        final ToggleButton tbAfogamento = (ToggleButton) findViewById(R.id.tbAfogamento);
        final ToggleButton tbPerdido    = (ToggleButton) findViewById(R.id.tbPerdido);
        final ToggleButton tbEscombros  = (ToggleButton) findViewById(R.id.tbEscombros);

        final ToggleButton tbInseto       = (ToggleButton) findViewById(R.id.tbInseto);
        final ToggleButton tbArvore       = (ToggleButton) findViewById(R.id.tbArvore);
        final ToggleButton tbDesobstrucao = (ToggleButton) findViewById(R.id.tbDesobstrucao);

        int i = 0;
        while (usuario.getStatus().equals("N")){
            if (i==0){
                Log.i("PROC","Carregando Usuário...");
            }
            i++;
        }
        Log.i("PROC","Usuário Carregado!");

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean GPSAtivado = false;
        GPSAtivado = locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER);

        if (GPSAtivado) {
            if (usuario.getStatus().equals("E")){
                AlertDialog.Builder erro = new AlertDialog.Builder(AuxiliumHome.this);
                erro.setTitle("Ops!");
                erro.setMessage("Este aparelho não possui cadastro no sistema do Corpo de Bombeiros.\nVocê deve cadastrar seus dados primeiro.");
                erro.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent callFormUsuario = new Intent(AuxiliumHome.this,UsuarioActivity.class);
                        startActivity(callFormUsuario);
                    }
                });
                erro.setCancelable(false);
                erro.show();
            }
        }

        if (usuario.getProblemaSaude().equals("S")){
            btPanico.setVisibility(View.VISIBLE);
            btPanico.setText("Chamada Pré-definida:\n"+usuario.getDsProblemaSaude());
        }

        if (usuario.getStatus().equals("B")){
            btNovaOcorrencia.setVisibility(View.INVISIBLE);
            btPanico.setVisibility(View.INVISIBLE);
            lbDinamica.setText("Seu usuário foi bloqueado por estar associado a uma ocorrência de trote!\n" +
                    "Ligue para o atendimento do Corpo de Bombeiros para saber mais informações.");
            lbDinamica.setVisibility(View.VISIBLE);
            lbDinamica.setTextSize(28);
        }else if (usuario.getStatus().equals("I")||usuario.getStatus().equals("E")){
            btNovaOcorrencia.setVisibility(View.INVISIBLE);
            btPanico.setVisibility(View.INVISIBLE);
            lbDinamica.setText("Seu usuário ainda não foi liberado pelo Corpo de Bombeiros!");
            lbDinamica.setTextSize(28);
            lbDinamica.setVisibility(View.VISIBLE);
        } else {
            lbDinamica.setTextSize(15);
        }

        final String cbm   = "998240158";
        final Map<String, String> dsOcorrencia = new HashMap<String, String>();
        dsOcorrencia.put("tpOcorrencia","");

        btConfirmar.setVisibility(View.INVISIBLE);
        btCancelar.setVisibility(View.INVISIBLE);

        btNovaOcorrencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btAcidenteTransito.setVisibility(View.VISIBLE);
                btAtendimentoMedico.setVisibility(View.VISIBLE);
                btIncendio.setVisibility(View.VISIBLE);
                btSalvamento.setVisibility(View.VISIBLE);
                btOutros.setVisibility(View.VISIBLE);
                btCancelar.setVisibility(View.VISIBLE);
                btNovaOcorrencia.setVisibility(View.INVISIBLE);
                btPanico.setVisibility(View.INVISIBLE);
            }
        });

        btPanico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dsOcorrencia.put("tpOcorrencia","ATENDIMENTO MEDICO");
                dsOcorrencia.put("problemaSaude","S");
                dsOcorrencia.put("dsProblemaSaude",usuario.getDsProblemaSaude());
                dsOcorrencia.put("tpEmergencia","");
                Ocorrencia ocorrencia = new Ocorrencia((lat==0.0?-28.681607:lat),(lng==0.0?-49.373459:lng),usuario,dsOcorrencia);
                realizarChamada(cbm,ocorrencia);
            }
        });

        btConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Descrição Acidente Trânsito
                String tpVeiculos = swMotos.isChecked()?", motos":"";
                tpVeiculos += swCarros.isChecked()?", carros":"";
                tpVeiculos += swCaminhao.isChecked()?", caminhões":"";
                tpVeiculos += swCarretas.isChecked()?", carretas":"";
                tpVeiculos += swOnibus.isChecked()?", ônibus/vans":"";
                dsOcorrencia.put("veiculos",tpVeiculos);
                dsOcorrencia.put("quimicos",swQuimico.isChecked()?"Informada a presença de produtos químicos.":"");
                dsOcorrencia.put("vitimas",rb1.isChecked()?"1":rb2.isChecked()?"2":rb3mais.isChecked()?"3 ou mais":"");
                //Descrição atendimento médico
                String tpEmergencia = swFraturaExposta.isChecked()?", fratura exposta":"";
                tpEmergencia += swQueimadura.isChecked()?", queimadura":"";
                tpEmergencia += swMalSubito.isChecked()?", mal súbito":"";
                tpEmergencia += swDesmaio.isChecked()?", desmaio":"";
                tpEmergencia += swMalEstar.isChecked()?", mal estar":"";
                tpEmergencia += swEnvenenamento.isChecked()?", envenenamento":"";
                dsOcorrencia.put("tpEmergencia",tpEmergencia);
                //Descrição incêndio
                String tpLocal = swCasa.isChecked()?", casas":"";
                tpLocal += swPredio.isChecked()?", prédios":"";
                tpLocal += swComercio.isChecked()?", comércios":"";
                tpLocal += swFloresta.isChecked()?", florestas":"";
                dsOcorrencia.put("tpLocal",tpLocal);
                if (dsOcorrencia.get("tpOcorrencia").equals("AFOGAMENTO") && !tbAfogamento.isChecked()){
                    dsOcorrencia.put("tpOcorrencia","SALVAMENTO");
                }
                if (dsOcorrencia.get("tpOcorrencia").equals("PERDIDO") && !tbPerdido.isChecked()){
                    dsOcorrencia.put("tpOcorrencia","SALVAMENTO");
                }
                if (dsOcorrencia.get("tpOcorrencia").equals("ESCOMBROS") && !tbEscombros.isChecked()){
                    dsOcorrencia.put("tpOcorrencia","SALVAMENTO");
                }
                if (dsOcorrencia.get("tpOcorrencia").equals("INSETO") && !tbInseto.isChecked()){
                    dsOcorrencia.put("tpOcorrencia","OUTROS");
                }
                if (dsOcorrencia.get("tpOcorrencia").equals("CORTE ARVORE") && !tbArvore.isChecked()){
                    dsOcorrencia.put("tpOcorrencia","OUTROS");
                }
                if (dsOcorrencia.get("tpOcorrencia").equals("DESOBSTRUCAO PASSAGEM") && !tbDesobstrucao.isChecked()){
                    dsOcorrencia.put("tpOcorrencia","OUTROS");
                }
                dsOcorrencia.put("problemaSaude","");
                Ocorrencia ocorrencia = new Ocorrencia((lat==0.0?-28.681607:lat),(lng==0.0?-49.373459:lng),usuario,dsOcorrencia);
                realizarChamada(cbm,ocorrencia);
            }
        });

        btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dsOcorrencia.get("tpOcorrencia").equals("")){
                    btAcidenteTransito.setVisibility(View.INVISIBLE);
                    btAtendimentoMedico.setVisibility(View.INVISIBLE);
                    btIncendio.setVisibility(View.INVISIBLE);
                    btSalvamento.setVisibility(View.INVISIBLE);
                    btOutros.setVisibility(View.INVISIBLE);
                    btCancelar.setVisibility(View.INVISIBLE);
                    btNovaOcorrencia.setVisibility(View.VISIBLE);
                    if (usuario.getProblemaSaude().equals("S")){
                        btPanico.setVisibility(View.VISIBLE);
                    }
                }else {
                    btConfirmar.setVisibility(View.INVISIBLE);
                    btAcidenteTransito.setVisibility(View.VISIBLE);
                    btAtendimentoMedico.setVisibility(View.VISIBLE);
                    btIncendio.setVisibility(View.VISIBLE);
                    btSalvamento.setVisibility(View.VISIBLE);
                    btOutros.setVisibility(View.VISIBLE);
                    lbDinamica.setVisibility(View.INVISIBLE);
                    swMotos.setVisibility(View.INVISIBLE);
                    swCarros.setVisibility(View.INVISIBLE);
                    swCaminhao.setVisibility(View.INVISIBLE);
                    swCarretas.setVisibility(View.INVISIBLE);
                    swOnibus.setVisibility(View.INVISIBLE);
                    swQuimico.setVisibility(View.INVISIBLE);
                    lbVitimas.setVisibility(View.INVISIBLE);
                    rgroupVitimas.setVisibility(View.INVISIBLE);
                    swFraturaExposta.setVisibility(View.INVISIBLE);
                    swQueimadura.setVisibility(View.INVISIBLE);
                    swMalSubito.setVisibility(View.INVISIBLE);
                    swDesmaio.setVisibility(View.INVISIBLE);
                    swMalEstar.setVisibility(View.INVISIBLE);
                    swEnvenenamento.setVisibility(View.INVISIBLE);
                    swCasa.setVisibility(View.INVISIBLE);
                    swPredio.setVisibility(View.INVISIBLE);
                    swComercio.setVisibility(View.INVISIBLE);
                    swFloresta.setVisibility(View.INVISIBLE);
                    swQuimico.setVisibility(View.INVISIBLE);
                    tbAfogamento.setVisibility(View.INVISIBLE);
                    tbPerdido.setVisibility(View.INVISIBLE);
                    tbEscombros.setVisibility(View.INVISIBLE);
                    tbAfogamento.setChecked(false);
                    tbPerdido.setChecked(false);
                    tbEscombros.setChecked(false);
                    tbInseto.setVisibility(View.INVISIBLE);
                    tbArvore.setVisibility(View.INVISIBLE);
                    tbDesobstrucao.setVisibility(View.INVISIBLE);
                    tbInseto.setChecked(false);
                    tbArvore.setChecked(false);
                    tbDesobstrucao.setChecked(false);
                    dsOcorrencia.put("tpOcorrencia", "");
                }
            }
        });

        btAcidenteTransito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btConfirmar.setVisibility(View.VISIBLE);
                btCancelar.setVisibility(View.VISIBLE);
                btAcidenteTransito.setVisibility(View.INVISIBLE);
                btAtendimentoMedico.setVisibility(View.INVISIBLE);
                btIncendio.setVisibility(View.INVISIBLE);
                btSalvamento.setVisibility(View.INVISIBLE);
                btOutros.setVisibility(View.INVISIBLE);
                lbDinamica.setText("Tipos de Veículos Envolvidos");
                lbDinamica.setVisibility(View.VISIBLE);
                swMotos.setVisibility(View.VISIBLE);
                swCarros.setVisibility(View.VISIBLE);
                swCaminhao.setVisibility(View.VISIBLE);
                swCarretas.setVisibility(View.VISIBLE);
                swOnibus.setVisibility(View.VISIBLE);
                swQuimico.setVisibility(View.VISIBLE);
                lbVitimas.setVisibility(View.VISIBLE);
                rgroupVitimas.setVisibility(View.VISIBLE);
                dsOcorrencia.put("tpOcorrencia","ACIDENTE TRANSITO");
            }
        });

        btAtendimentoMedico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btConfirmar.setVisibility(View.VISIBLE);
                btCancelar.setVisibility(View.VISIBLE);
                btAcidenteTransito.setVisibility(View.INVISIBLE);
                btAtendimentoMedico.setVisibility(View.INVISIBLE);
                btIncendio.setVisibility(View.INVISIBLE);
                btSalvamento.setVisibility(View.INVISIBLE);
                btOutros.setVisibility(View.INVISIBLE);
                lbDinamica.setText("Tipo de Emergência Médica");
                lbDinamica.setVisibility(View.VISIBLE);
                swFraturaExposta.setVisibility(View.VISIBLE);
                swQueimadura.setVisibility(View.VISIBLE);
                swMalSubito.setVisibility(View.VISIBLE);
                swDesmaio.setVisibility(View.VISIBLE);
                swMalEstar.setVisibility(View.VISIBLE);
                swEnvenenamento.setVisibility(View.VISIBLE);
                dsOcorrencia.put("tpOcorrencia","ATENDIMENTO MEDICO");
            }
        });

        btIncendio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btConfirmar.setVisibility(View.VISIBLE);
                btCancelar.setVisibility(View.VISIBLE);
                btAcidenteTransito.setVisibility(View.INVISIBLE);
                btAtendimentoMedico.setVisibility(View.INVISIBLE);
                btIncendio.setVisibility(View.INVISIBLE);
                btSalvamento.setVisibility(View.INVISIBLE);
                btOutros.setVisibility(View.INVISIBLE);
                lbDinamica.setText("Tipo de Local em Chamas");
                lbDinamica.setVisibility(View.VISIBLE);
                swCasa.setVisibility(View.VISIBLE);
                swPredio.setVisibility(View.VISIBLE);
                swComercio.setVisibility(View.VISIBLE);
                swFloresta.setVisibility(View.VISIBLE);
                swQuimico.setVisibility(View.VISIBLE);
                dsOcorrencia.put("tpOcorrencia","INCENDIO");
            }
        });

        btSalvamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btConfirmar.setVisibility(View.VISIBLE);
                btCancelar.setVisibility(View.VISIBLE);
                btAcidenteTransito.setVisibility(View.INVISIBLE);
                btAtendimentoMedico.setVisibility(View.INVISIBLE);
                btIncendio.setVisibility(View.INVISIBLE);
                btSalvamento.setVisibility(View.INVISIBLE);
                btOutros.setVisibility(View.INVISIBLE);
                tbAfogamento.setVisibility(View.VISIBLE);
                tbPerdido.setVisibility(View.VISIBLE);
                tbEscombros.setVisibility(View.VISIBLE);
                dsOcorrencia.put("tpOcorrencia","SALVAMENTO");
            }
        });

        btOutros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btConfirmar.setVisibility(View.VISIBLE);
                btCancelar.setVisibility(View.VISIBLE);
                btAcidenteTransito.setVisibility(View.INVISIBLE);
                btAtendimentoMedico.setVisibility(View.INVISIBLE);
                btIncendio.setVisibility(View.INVISIBLE);
                btSalvamento.setVisibility(View.INVISIBLE);
                btOutros.setVisibility(View.INVISIBLE);
                tbInseto.setVisibility(View.VISIBLE);
                tbArvore.setVisibility(View.VISIBLE);
                tbDesobstrucao.setVisibility(View.VISIBLE);
                dsOcorrencia.put("tpOcorrencia","OUTROS");
            }
        });

        tbAfogamento.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                tbPerdido.setChecked(false);
                tbEscombros.setChecked(false);
                dsOcorrencia.put("tpOcorrencia","AFOGAMENTO");
            }
        });

        tbPerdido.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                tbAfogamento.setChecked(false);
                tbEscombros.setChecked(false);
                dsOcorrencia.put("tpOcorrencia","PERDIDO");
            }
        });

        tbEscombros.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                tbPerdido.setChecked(false);
                tbAfogamento.setChecked(false);
                dsOcorrencia.put("tpOcorrencia","ESCOMBROS");
            }
        });

        tbInseto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                tbArvore.setChecked(false);
                tbDesobstrucao.setChecked(false);
                dsOcorrencia.put("tpOcorrencia","INSETO");
            }
        });

        tbArvore.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                tbInseto.setChecked(false);
                tbDesobstrucao.setChecked(false);
                dsOcorrencia.put("tpOcorrencia","CORTE ARVORE");
            }
        });

        tbDesobstrucao.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                tbInseto.setChecked(false);
                tbArvore.setChecked(false);
                dsOcorrencia.put("tpOcorrencia","DESOBSTRUCAO PASSAGEM");
            }
        });
    }
}