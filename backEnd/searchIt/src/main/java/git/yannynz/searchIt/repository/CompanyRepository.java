package git.yannynz.searchIt.repository;

import git.yannynz.searchIt.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

    // Busca empresas pela especialidade (ignora case)
    List<Company> findByEspecialidadeIgnoreCase(String especialidade);
}

