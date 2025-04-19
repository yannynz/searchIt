package git.yannynz.searchIt.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.*;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "orcamento_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrcamentoRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Dados do solicitante
    private String nome;
    private String cpfCnpj;
    private String email;

    // Produto e quantidade desejada
    private String produto;
    private Integer quantidade;

    // Raio de alcance em km
    private Integer raioKM;

    // Localização do usuário
    private String cep;

    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        dataCriacao = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "orcamentoRequest", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Company> empresas;

}
