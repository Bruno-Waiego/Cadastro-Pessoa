package com.example.cadastropessoa.control;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.cadastropessoa.R;
import com.example.cadastropessoa.dao.PessoaDao;
import com.example.cadastropessoa.dto.PessoaDTO;
import com.example.cadastropessoa.model.Pessoa;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PessoaControl {

    private Activity activity;
    private EditText editNome;
    private EditText editTelefone;
    private EditText editEmail;

    private Pessoa pessoa;
    private PessoaDao pessoaDao;

    private ListView lvPessoa;
    private List<Pessoa> listPessoa;
    private ArrayAdapter<Pessoa> adapterPessoa;

    public PessoaControl(Activity activity) {
        this.activity = activity;
        pessoaDao = new PessoaDao(activity);
        initComponents();
    }

    private void initComponents() {
        lvPessoa = activity.findViewById(R.id.lvPessoa);
        editEmail = activity.findViewById(R.id.editEmail);
        editNome = activity.findViewById(R.id.editNome);
        editTelefone = activity.findViewById(R.id.editTelefone);
        configListView();
        requisicaoPessoa();
    }

    public void configListView() {
        try {
            listPessoa = pessoaDao.getDao().queryForAll();
        } catch (SQLException e) {
            listPessoa = new ArrayList<>();
        }
        adapterPessoa = new ArrayAdapter<>(
                activity,
                android.R.layout.simple_list_item_1,
                listPessoa
        );
        lvPessoa.setAdapter(adapterPessoa);
    }

    private void limparCampos() {
        editEmail.setText("");
        editTelefone.setText("");
        editNome.setText("");
    }

    public void requisicaoPostPessoa() {
        pessoa = new Pessoa();
        pessoa.setNome(editNome.getText().toString());
        pessoa.setEmail(editEmail.getText().toString());
        pessoa.setTelefone(editTelefone.getText().toString());

        Gson g = new Gson();

        RequestParams params = new RequestParams("params", g.toJson(pessoa));

        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://192.168.56.1:8080/CadastroPessoa/api/pessoa", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                Toast.makeText(activity, "Iniciando requisição", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onRetry(int retryNo) {
                super.onRetry(retryNo);
            }

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    pessoaDao.getDao().create(pessoa);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(activity, "Sucesso na requisição", Toast.LENGTH_LONG).show();
                limparCampos();
                requisicaoPessoa();
                atualizarListView();
                pessoa = new Pessoa();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(activity, "Erro na requisição", Toast.LENGTH_LONG).show();
            }
        });


    }

    public void requisicaoPessoa() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://192.168.56.1:8080/CadastroPessoa/api/pessoa", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                Toast.makeText(activity, "Iniciando requisição", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                listPessoa.clear();
                String pessoaJson = new String(bytes);

                Gson g = new Gson();

                Type type = new TypeToken<List<PessoaDTO>>() {
                }.getType();

                List<PessoaDTO> yourList = g.fromJson(pessoaJson, type);

                for (PessoaDTO p : yourList) {
                    listPessoa.add(p.getPessoa());
                    try {
                        pessoaDao.getDao().createIfNotExists(p.getPessoa());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                atualizarListView();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(activity, "Erro na requisição", Toast.LENGTH_SHORT).show();
                requisicaoPessoa();
            }

            @Override
            public void onRetry(int retryNo) {
                super.onRetry(retryNo);
            }
        });
    }

    private void atualizarListView() {
        adapterPessoa.notifyDataSetChanged();
    }

    public void salvarAction() {
        requisicaoPostPessoa();

    }

}
