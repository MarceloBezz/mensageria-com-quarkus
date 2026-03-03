package br.com.alura.controller;

import br.com.alura.domain.audit.Audit;
import br.com.alura.domain.saga.Saga;
import br.com.alura.domain.saga.SagaStatus;
import br.com.alura.repository.SagaRepository;
import br.com.alura.service.SituacaoCadastralService;
import br.com.alura.domain.Agencia;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.resteasy.reactive.RestResponse;

import java.time.LocalDateTime;
import java.util.List;

@Path("/situacao-cadastral")
public class SituacaoCadastralController {

    private final SituacaoCadastralService service;
    private final Emitter<Audit> emitter;
    private final MutinyEmitter<br.com.alura.Agencia> mutiniEmitter;
    private final SagaRepository sagaRepository;
    private final ObjectMapper objectMapper;

    SituacaoCadastralController(SituacaoCadastralService situacaoCadastralService,
                                @Channel("notificacoes") Emitter<Audit> emitter,
                                @Channel("remover-agencia-channel") MutinyEmitter<br.com.alura.Agencia> mutiniEmitter, SagaRepository sagaRepository, ObjectMapper objectMapper) {
        this.service = situacaoCadastralService;
        this.emitter = emitter;
        this.mutiniEmitter = mutiniEmitter;
        this.sagaRepository = sagaRepository;
        this.objectMapper = objectMapper;
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
    @Transactional
    public RestResponse<String> atualizar(Agencia agencia) {
        Agencia ag = service.alterar(agencia);
        if (ag != null) {
            emitter.send(new Audit(agencia.getId(), agencia.getCnpj(), agencia.getSituacaoCadastral()));
            try {
                if (agencia.getSituacaoCadastral().equals("INATIVO")) {
                    sagaRepository.persist(new Saga(
                            ag.getCnpj(),
                            objectMapper.writeValueAsString(agencia),
                            SagaStatus.OPEN,
                            LocalDateTime.now()));

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
