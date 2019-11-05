package com.example.cadastropessoa.dao;

import android.content.Context;

import com.example.cadastropessoa.helper.DaoHelper;
import com.example.cadastropessoa.model.Pessoa;

public class PessoaDao extends DaoHelper<Pessoa> {
    public PessoaDao(Context c) {
        super(c, Pessoa.class);
    }
}
