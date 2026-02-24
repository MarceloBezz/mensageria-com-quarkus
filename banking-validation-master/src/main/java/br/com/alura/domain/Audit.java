package br.com.alura.domain;

public class Audit {
    private Integer id;
    private String cnpj;
    private String situacaoCadastral;


    public Audit(Integer id, String cnpj, String situacaoCadastral) {
        this.id = id;
        this.cnpj = cnpj;
        this.situacaoCadastral = situacaoCadastral;
    }

    public Integer getId() {
        return id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getSituacaoCadastral() {
        return situacaoCadastral;
    }
}
