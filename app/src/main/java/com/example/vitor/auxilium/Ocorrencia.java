package com.example.vitor.auxilium;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vitor on 15/06/2017.
 */

public class Ocorrencia {
    private String  tpOcorrencia;
    private String  dsOcorrencia;
    private Usuario usuario;
    private Double  lat;
    private Double  lng;

    public Ocorrencia(double lat, double lng,Usuario usuario, Map<String,String> mapOcorrencia) {
        setLat(lat);
        setLng(lng);
        setUsuario(usuario);
        setTpOcorrencia(mapOcorrencia.get("tpOcorrencia"));
        setDsOcorrencia(mapOcorrencia);
    }

    public void setTpOcorrencia(String tpOcorrencia) {
        this.tpOcorrencia = tpOcorrencia;
    }

    public void setDsOcorrencia(Map<String,String> mapOcorrencia) {
        this.dsOcorrencia = " ";
        switch (mapOcorrencia.get("tpOcorrencia")){
            case "ACIDENTE TRANSITO":
                if (mapOcorrencia.get("veiculos")!=""){
                    this.dsOcorrencia = "Relatado o envolvimento de:"+mapOcorrencia.get("veiculos").substring(1)+".";
                }
                this.dsOcorrencia += " "+mapOcorrencia.get("quimicos");
                if (mapOcorrencia.get("vitimas")!=""){
                    this.dsOcorrencia += " Número de vítimas: "+mapOcorrencia.get("vitimas")+".";
                }
                break;
            case "ATENDIMENTO MEDICO":
                if (mapOcorrencia.get("tpEmergencia")!=""){
                    this.dsOcorrencia = "Relatada ocorrência de"+mapOcorrencia.get("tpEmergencia").substring(1)+".";
                };
                if (mapOcorrencia.get("problemaSaude").equals("S")){
                    this.dsOcorrencia = "Chamada emergencial pelo aplicativo. Usuário possui: "+mapOcorrencia.get("dsProblemaSaude");
                };
                break;
            case "INCENDIO":
                if (mapOcorrencia.get("tpLocal")!=""){
                    this.dsOcorrencia = "Ocorrência atinge"+mapOcorrencia.get("tpLocal").substring(1)+".";
                }
                this.dsOcorrencia += " "+mapOcorrencia.get("quimicos");
                break;
            default:
                this.dsOcorrencia = " ";
                break;
        }
    }

    public void setUsuario(Usuario usuario){
        this.usuario = usuario;
    }

    public void setLat(double lat){
        this.lat = lat;
    }

    public void setLng(double lng){
        this.lng = lng;
    }

    public String getTpOcorrencia() {
        return tpOcorrencia;
    }

    public String getDsOcorrencia() {
        return dsOcorrencia;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    @Override
    public String toString() {
        return "Tipo Ocorrência: "+this.getTpOcorrencia()+"\nDescrição: "+this.getDsOcorrencia();
    }
}
