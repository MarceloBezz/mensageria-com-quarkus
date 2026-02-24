package br.com.alura.service;

import br.com.alura.repository.SituacaoCadastralRepository;
import br.com.alura.domain.Agencia;
import br.com.alura.domain.Audit;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class SituacaoCadastralService {

    private final SituacaoCadastralRepository repository;
    private final Emitter<Audit> emitter;

    public SituacaoCadastralService(SituacaoCadastralRepository repository,
                                    @Channel("notificacoes") Emitter<Audit> emitter) {
        this.repository = repository;
        this.emitter = emitter;
    }

    public void alterar(Agencia agencia) {
        repository.update("situacaoCadastral = ?1 where cnpj = ?2",
                        agencia.getSituacaoCadastral(), agencia.getCnpj());
    }
}
