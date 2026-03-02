import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class RemoverAgenciaService {
    private final AgenciaService agenciaService;
    private final ObjectMapper objectMapper;

    public RemoverAgenciaService(AgenciaService agenciaService, ObjectMapper objectMapper) {
        this.agenciaService = agenciaService;
        this.objectMapper = objectMapper;
    }

    @WithTransaction
    @Incoming("remover-agencia-channel")
    public Uni<Void> consumirMensagem(String mensagem) {
        try {
            var agenciaMensagem = objectMapper.readValue(mensagem, AgenciaMensagem.class);
            return agenciaService.buscarPorCnpj(agenciaMensagem.getCnpj())
                    .onItem().ifNotNull().transformToUni(agencia -> agenciaService.deletar(agencia.getId()))
                    .replaceWithVoid();
        } catch (Exception e) {
            return Uni.createFrom().failure(e);
        }
    }
}
