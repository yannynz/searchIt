package git.yannynz.searchIt.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Nome da empresa
    private String nome;

    // Produto que essa empresa "especializa" (ex.: Cimento, Tijolo etc.)
    private String especialidade;

    // Para filtrar por localização, poderíamos ter latitude/longitude
    private String endereco; // ou lat/long

    // Website para extrair email
    private String website;

    // Email "oficial" (pode ser sobrescrito via scraping)
    private String email;

    private Boolean emailEnviado;
}

