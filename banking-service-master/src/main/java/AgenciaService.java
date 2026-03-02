import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;

public class AgenciaService {
    private final AgenciaRepository agenciaRepository;

    public AgenciaService(AgenciaRepository agenciaRepository) {
        this.agenciaRepository = agenciaRepository;
    }

    @WithSession
    public Uni<Agencia> buscarPorCnpj(String cnpj) {
        return agenciaRepository.findByCnpj(cnpj);
    }

    @WithSession
    public Uni<Void> deletar(Long id) {
        return agenciaRepository.deleteById(id).replaceWithVoid();
    }
}
