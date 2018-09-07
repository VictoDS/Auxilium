package com.example.vitor.auxilium;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

public class UsuarioActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    private final Usuario usuario = AuxiliumHome.usuario;
    private Date    dtNascimento  = null;
    private final String[] ufs    = {"AC","AL","AP","AM","BA","CE","DF","ES",
            "GO","MA","MS","MT","MG","PA","PB","PR","PE","PI","RJ","RN","RS","RO","RR","SC","SP","SE","TO"};
    private String gravado = "N";

    private void gravaUsuario(){
        OkHttpClient client = new OkHttpClient();

        MediaType media = MediaType.parse("application/json; charset=utf-8");
        final JSONObject json = new JSONObject();
        String url = "";

        try {
            if (usuario.getId()==0) {
                json.put("nome", usuario.getNome());
                json.put("cpf", usuario.getCpf());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                json.put("data_nascimento", sdf.format(usuario.getDtNascimento()));
                json.put("telefone", usuario.getFone());
                json.put("endereco", usuario.getEndereco());
                json.put("bairro", usuario.getBairro());
                json.put("cidade", usuario.getCidade());
                json.put("uf", usuario.getUf());
                json.put("token", usuario.getToken());
                json.put("doenca", usuario.getProblemaSaude());
                json.put("ds_doenca", usuario.getDsProblemaSaude());
                Log.i("JSON",json.toString());
                url = "https://seilaeu.herokuapp.com/novo-usuario";
            }else{
                json.put("telefone", usuario.getFone());
                json.put("endereco", usuario.getEndereco());
                json.put("bairro", usuario.getBairro());
                json.put("cidade", usuario.getCidade());
                json.put("uf", usuario.getUf());
                json.put("token", usuario.getToken());
                json.put("doenca", usuario.getProblemaSaude());
                json.put("ds_doenca", usuario.getDsProblemaSaude());
                json.put("id",usuario.getId());
                Log.i("JSON",json.toString());
                url = "https://seilaeu.herokuapp.com/atualiza-usuario";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String req = json.toString();
        RequestBody body = RequestBody.create(media,req);

        Request request = new Request.Builder().url(url).post(body).build();

        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String conteudo = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(conteudo);
                    Log.e("JSON",conteudo);
                    if (jsonObject.getString("status").equals("ERRO")){
                        msgErro("Falha ao gravar usuario!");
                        gravado = "E";
                    }else{
                        gravado = "S";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        client.newCall(request).enqueue(callback);
    }

    private void msgErro (String msg){
        AlertDialog.Builder erro = new AlertDialog.Builder(UsuarioActivity.this);
        erro.setTitle("Erro!");
        erro.setMessage(msg);
        erro.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("ERR","Erro programado");
            }
        });
        erro.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        final String androidId = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        final EditText editNome     = (EditText) findViewById(R.id.editNome);
        final EditText editCpf      = (EditText) findViewById(R.id.editCpf);
        final EditText editTelefone = (EditText) findViewById(R.id.editTelefone);
        final EditText editEndereco = (EditText) findViewById(R.id.editEndereco);
        final EditText editBairro   = (EditText) findViewById(R.id.editBairro);
        final EditText editCidade   = (EditText) findViewById(R.id.editCidade);
        final Spinner  spUf         = (Spinner)  findViewById(R.id.spUf);
        final EditText editDsDoenca = (EditText) findViewById(R.id.editDsDoenca);
        final DatePickerDialog calendario = new DatePickerDialog(this,UsuarioActivity.this,1999,01,01);

        final TextView txtData   = (TextView) findViewById(R.id.txtData);
        final Button btData      = (Button) findViewById(R.id.btDatePicker);
        final Button btConfirmar = (Button) findViewById(R.id.btSalvar);

        final CheckBox ckDoenca  = (CheckBox) findViewById(R.id.ckDoenca);

        final ArrayAdapter adp = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,ufs);
        adp.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spUf.setAdapter(adp);

        Log.i("PROC","Usuário Carregado!");

        if (!usuario.getStatus().equals("E")){
            editNome.setText(usuario.getNome());
            editNome.setEnabled(false);
            editCpf.setText(usuario.getCpf());
            editCpf.setEnabled(false);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            txtData.setText("Nascido em "+sdf.format(usuario.getDtNascimento()));
            btData.setVisibility(View.INVISIBLE);
            editTelefone.setText(usuario.getFone());
            editEndereco.setText(usuario.getEndereco());
            editBairro.setText(usuario.getBairro());
            editCidade.setText(usuario.getCidade());

            for (int i=0; i<ufs.length;i++) {
                if (ufs[i].equals(usuario.getUf())){
                    spUf.setSelection(i);
                }
            }
            if (usuario.getProblemaSaude().equals("S")){
                ckDoenca.setChecked(true);
                editDsDoenca.setText(usuario.getDsProblemaSaude());
            }else{
                ckDoenca.setChecked(false);
            }
        }

        btData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendario.show();
            }
        });

        btConfirmar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (editCpf.getText().length()==0){
                    msgErro("Preencha o CPF");
                    editCpf.requestFocus();
                    return;
                }

                if (editNome.getText().length()==0){
                    msgErro("Preencha o Nome");
                    editNome.requestFocus();
                    return;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                long hoje = System.currentTimeMillis();
                String menor = "O usuário precisa ter no mínimo 18 anos.";
                if ((calendario.getDatePicker().getYear()+19)>Integer.parseInt(sdf.format(hoje))) {
                    if ((calendario.getDatePicker().getYear() + 18) == Integer.parseInt(sdf.format(hoje))) {
                        SimpleDateFormat sdfD = new SimpleDateFormat("dd");
                        SimpleDateFormat sdfM = new SimpleDateFormat("MM");

                        int diaHoje = Integer.parseInt(sdfD.format(hoje));
                        int mesHoje = Integer.parseInt(sdfM.format(hoje));

                        if (mesHoje < calendario.getDatePicker().getMonth()) {
                            msgErro(menor);
                            return;
                        } else if (mesHoje == calendario.getDatePicker().getMonth()) {
                            if (diaHoje < calendario.getDatePicker().getDayOfMonth()) {
                                msgErro(menor);
                                return;
                            }
                        }
                    }
                }
                if (editTelefone.getText().length()==0){
                    msgErro("Preencha o Telefone");
                    editTelefone.requestFocus();
                    return;
                }

                if (editEndereco.getText().length()==0){
                    msgErro("Preenche o Endereço");
                    editEndereco.requestFocus();
                    return;
                }

                if (editBairro.getText().length()==0){
                    msgErro("Preencha o Bairro");
                    editBairro.requestFocus();
                    return;
                }

                if (editCidade.getText().length()==0){
                    msgErro("Preencha a Cidade");
                    editCidade.requestFocus();
                    return;
                }

                usuario.setNome(editNome.getText()+"");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    dtNascimento = sdf2.parse(calendario.getDatePicker().getYear()+"-"+
                            (calendario.getDatePicker().getMonth()+1==13?1:calendario.getDatePicker().getMonth()+1)+"-"+
                            calendario.getDatePicker().getDayOfMonth());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                usuario.setDtNascimento(dtNascimento);
                usuario.setCpf(editCpf.getText()+"");
                usuario.setFone(editTelefone.getText()+"");
                usuario.setEndereco(editEndereco.getText()+"");
                usuario.setBairro(editBairro.getText()+"");
                usuario.setCidade(editCidade.getText()+"");
                usuario.setUf(spUf.getSelectedItem().toString());
                usuario.setToken(androidId);
                usuario.setProblemaSaude(ckDoenca.isChecked()?"S":"N");
                usuario.setDsProblemaSaude(ckDoenca.isChecked()?editDsDoenca.getText()+"":"-");

                gravaUsuario();

                //while(gravado=="N"){}
                Toast.makeText(UsuarioActivity.this,"Usuário salvo!",Toast.LENGTH_LONG).show();

                finish();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
