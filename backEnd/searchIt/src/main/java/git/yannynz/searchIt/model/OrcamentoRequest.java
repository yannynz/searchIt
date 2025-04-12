package git.yannynz.searchIt.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    // Raio de alcance (não implementamos a lógica geoespacial aqui,
    // mas poderíamos usar lat/long e filtrar depois)
    private Integer raioKM;

    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        dataCriacao = LocalDateTime.now();
    }
}

