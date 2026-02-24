package br.com.alura.service;

import br.com.alura.domain.Audit;
import br.com.alura.repository.BankingAuditRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class BankingAuditService {

    private final BankingAuditRepository repository;

    public BankingAuditService(BankingAuditRepository repository) {
        this.repository = repository;
    }

    @Incoming("notificacoes")
    @WithTransaction
    public Uni<Void> consumirMensagem(JsonObject audit) {
        var auditConvertida = new Audit(audit.getString("cnpj"), audit.getString("situacaoCadastral"));
        return repository.persist(auditConvertida).replaceWithVoid();
    }
}
