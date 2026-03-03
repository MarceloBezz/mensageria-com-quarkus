package br.com.alura.repository;

import br.com.alura.domain.saga.Saga;
import br.com.alura.domain.saga.SagaStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class SagaRepository implements PanacheRepository<Saga> {
    public List<Saga> listByStatusAndCreatedAt(LocalDateTime limite) {
        return list("createdAt < ?1 and status = ?2", limite, SagaStatus.OPEN);
    }
}
