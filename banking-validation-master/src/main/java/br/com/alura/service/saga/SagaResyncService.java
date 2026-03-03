package br.com.alura.service.saga;

import br.com.alura.Agencia;
import br.com.alura.domain.saga.Saga;
import br.com.alura.repository.SagaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.vertx.mutiny.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;

import java.time.LocalDateTime;

@ApplicationScoped
public class SagaResyncService {
    private final MutinyEmitter<br.com.alura.Agencia> mutinyEmitter;
    private final SagaRepository sagaRepository;
    private final ObjectMapper objectMapper;
    private final Vertx vertx;

    public SagaResyncService(@Channel("remover-agencia-channel") MutinyEmitter<Agencia> mutinyEmitter,
                             SagaRepository sagaRepository,
                             Vertx vertx) {
        this.mutinyEmitter = mutinyEmitter;
        this.sagaRepository = sagaRepository;
        this.objectMapper = new ObjectMapper();
        this.vertx = vertx;
    }

    @Scheduled(every = "10s")
    public void resync() {
//        vertx.runOnContext(() -> {
            LocalDateTime limite = LocalDateTime.now().minusMinutes(2);
            var sagas = sagaRepository.listByStatusAndCreatedAt(limite);
            for (Saga saga : sagas) {
                try {
                    br.com.alura.domain.Agencia agenciaConvertida = objectMapper.readValue(saga.getEntidade(), br.com.alura.domain.Agencia.class);
                    mutinyEmitter.sendAndForget(new br.com.alura.Agencia(agenciaConvertida.getNome(), agenciaConvertida.getRazaoSocial(),
                            agenciaConvertida.getCnpj(), agenciaConvertida.getSituacaoCadastral()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
//        });
    }
}
