package br.com.alura.controller;

import br.com.alura.domain.Audit;
import br.com.alura.service.SituacaoCadastralService;
import br.com.alura.domain.Agencia;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@Path("/situacao-cadastral")
public class SituacaoCadastralController {

    private final SituacaoCadastralService service;
    private final Emitter<Audit> emitter;
    private final MutinyEmitter<br.com.alura.Agencia> mutiniEmitter;

    SituacaoCadastralController(SituacaoCadastralService situacaoCadastralService,
                                @Channel("notificacoes") Emitter<Audit> emitter,
                                @Channel("remover-agencia-channel") MutinyEmitter<br.com.alura.Agencia> mutiniEmitter) {
        this.service = situacaoCadastralService;
        this.emitter = emitter;
        this.mutiniEmitter = mutiniEmitter;
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
        return RestResponse.ok(agencias);
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
        Agencia ag = service.alterar(agencia);
        if (ag != null) {
            emitter.send(new Audit(agencia.getId(), agencia.getCnpj(), agencia.getSituacaoCadastral()));
            try {
                if (agencia.getSituacaoCadastral().equals("INATIVO")) {
                    mutiniEmitter
                            .send(new br.com.alura.Agencia(ag.getNome(), ag.getRazaoSocial(), ag.getCnpj(), ag.getSituacaoCadastral()))
                            .subscribe().with(
                            success -> System.out.println("Enviado"),
                            failure -> System.out.println("Erro: " + failure)
                    );
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
                return RestResponse.status(500);
            }
            return RestResponse.ok("Agência alterada com sucesso!");
        }

        return RestResponse.status(500);
    }
}
