package com.example.cadastropessoa.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.cadastropessoa.R;
import com.example.cadastropessoa.control.PessoaControl;

public class MainActivity extends AppCompatActivity {

    private PessoaControl pessoaControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pessoaControl = new PessoaControl(this);
        pessoaControl.requisicaoPessoa();
    }

    public void salvar(View v) {
        pessoaControl.salvarAction();
    }
}
