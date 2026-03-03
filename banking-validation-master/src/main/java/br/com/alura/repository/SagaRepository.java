package br.com.alura.repository;

import br.com.alura.domain.saga.Saga;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SagaRepository implements PanacheRepository<Saga> {
}
