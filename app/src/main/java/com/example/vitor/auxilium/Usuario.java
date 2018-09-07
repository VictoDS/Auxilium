package com.example.vitor.auxilium;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vitor on 15/06/2017.
 */

public class Usuario {
    private int    id;
    private String nome;
    private String cpf;
    private Date   dtNascimento;
    private String fone;
    private String endereco;
    private String bairro;
    private String cidade;
    private String uf;
    private String status;
    private String token;
    private String problemaSaude;
    private String dsProblemaSaude;

    public Usuario() {
        setId(0);
        setNome("Usuário Padrão");
        setCpf("00000000000");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            setDtNascimento(sdf.parse("01/01/2018"));
        } catch (ParseException e){
            setDtNascimento(new Date());
        }
        setFone("000000000");
        setEndereco("-");
        setBairro("-");
        setCidade("-");
        setUf("SC");
        setStatus("N");
        setToken("padrao");
        setProblemaSaude("N");
        setDsProblemaSaude("-");

    }

    public Usuario(int id, String nome, String cpf, Date dtNascimento, String fone, String endereco, String bairro, String cidade,
                   String uf, String status, String token, String problemaSaude, String dsProblemaSaude) {
        setId(id);
        setNome(nome);
        setCpf(cpf);
        setDtNascimento(dtNascimento);
        setFone(fone);
        setEndereco(endereco);
        setBairro(bairro);
        setCidade(cidade);
        setUf(uf);
        setStatus(status);
        setToken(token);
        setProblemaSaude(problemaSaude);
        setDsProblemaSaude(dsProblemaSaude);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Date getDtNascimento() {
        return dtNascimento;
    }

    public void setDtNascimento(Date dtNascimento) {
        this.dtNascimento = dtNascimento;
    }

    public String getFone() {
        return fone;
    }

    public void setFone(String fone) {
        this.fone = fone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getProblemaSaude() {
        return problemaSaude;
    }

    public void setProblemaSaude(String problemaSaude) {
        this.problemaSaude = problemaSaude;
    }

    public String getDsProblemaSaude() {
        return dsProblemaSaude;
    }

    public void setDsProblemaSaude(String dsProblemaSaude) {
        this.dsProblemaSaude = dsProblemaSaude;
    }

    @Override
    public String toString() {
        SimpleDateFormat conversor = new SimpleDateFormat("dd/MM/yyyy");
        return "Usuario{" +
                "\nid=" + id +
                ", \nnome='" + nome + '\'' +
                ", \ncpf='" + cpf + '\'' +
                ", \ndtNascimento=" + conversor.format(dtNascimento) +
                ", \nfone='" + fone + '\'' +
                ", \nendereco='" + endereco + '\'' +
                ", \nbairro='" + bairro + '\'' +
                ", \ncidade='" + cidade + '\'' +
                ", \nuf='" + uf + '\'' +
                ", \nstatus='" + status + '\'' +
                ", \ntoken='" + token + '\'' +
                ", \nproblemaSaude='" + problemaSaude + '\'' +
                ", \ndsProblemaSaude='" + dsProblemaSaude + '\'' +
                "\n}";
    }
}