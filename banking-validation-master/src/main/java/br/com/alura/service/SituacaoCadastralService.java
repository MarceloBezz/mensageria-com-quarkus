package br.com.alura.service;

import br.com.alura.repository.SituacaoCadastralRepository;
import br.com.alura.domain.Agencia;
import br.com.alura.domain.Audit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.List;

@ApplicationScoped
public class SituacaoCadastralService {

    private final SituacaoCadastralRepository repository;


    public SituacaoCadastralService(SituacaoCadastralRepository repository,
                                    @Channel("notificacoes") Emitter<Audit> emitter) {
        this.repository = repository;
//        this.emitter = emitter;
    }

    @Transactional
    public Agencia alterar(Agencia agencia) {
        if (this.buscarPorCnpj(agencia.getCnpj()) != null) {
            repository.update("situacaoCadastral = ?1 where cnpj = ?2",
                    agencia.getSituacaoCadastral(), agencia.getCnpj());
            return agencia;
        }
        return null;
    }

    @Transactional
    public void cadastrar(Agencia agencia) {
        repository.persist(agencia);
    }

    public List<Agencia> buscaTodos() {
        return repository
                .findAll()
                .stream()
                .toList();
    }

    public Agencia buscarPorCnpj(String cnpj) {
        return repository.findByCnpj(cnpj);
    }
}
