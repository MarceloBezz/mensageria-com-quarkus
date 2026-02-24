package br.com.alura.domain;

public class Audit {
    private Long id;
    private String cnpj;
    private String situacaoCadastral;


    public Audit(Long id, String cnpj, String situacaoCadastral) {
        this.id = id;
        this.cnpj = cnpj;
        this.situacaoCadastral = situacaoCadastral;
    }

    public Long getId() {
        return id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getSituacaoCadastral() {
        return situacaoCadastral;
    }
}
