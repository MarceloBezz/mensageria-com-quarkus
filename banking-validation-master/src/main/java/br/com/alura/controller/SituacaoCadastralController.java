package br.com.alura.controller;

import br.com.alura.service.SituacaoCadastralService;
import br.com.alura.domain.Agencia;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Path("/situacao-cadastral")
public class SituacaoCadastralController {

    private final SituacaoCadastralService service;

    SituacaoCadastralController(SituacaoCadastralService situacaoCadastralService) {
        this.service = situacaoCadastralService;
    }

    @POST
    @Transactional
    public RestResponse<Agencia> cadastrar(Agencia agencia) {
        service.cadastrar(agencia);
        return RestResponse.ok(agencia);
    }

    @GET
    public RestResponse<List<Agencia>> buscarTodos() {
        var agencias = service.buscaTodos();
        return  RestResponse.ok(agencias);
    }

    @GET
    @Path("{cnpj}")
    public RestResponse<Object> buscarPorCnpj(String cnpj) {
        Agencia agencia = service.buscarPorCnpj(cnpj);
        if (agencia != null) {
            return RestResponse.ok(agencia);
        }
        return RestResponse.noContent();
    }

    @PUT
    public RestResponse<String> atualizar(Agencia agencia) {
        service.alterar(agencia);
        return RestResponse.ok("Agência alterada com sucesso!");
    }
}
