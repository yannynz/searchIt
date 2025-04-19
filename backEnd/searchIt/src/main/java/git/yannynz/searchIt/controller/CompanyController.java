package git.yannynz.searchIt.controller;

import git.yannynz.searchIt.model.Company;
import git.yannynz.searchIt.model.OrcamentoRequest;
import git.yannynz.searchIt.repository.CompanyRepository;
import git.yannynz.searchIt.repository.OrcamentoRequestRepository;
import git.yannynz.searchIt.service.WebSearchService;
import git.yannynz.searchIt.service.LatLng;
import git.yannynz.searchIt.service.GeocodingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empresas")
public class CompanyController {

    private final WebSearchService webSearchService;
    private final GeocodingService geocodingService;
    private final CompanyRepository companyRepo;
    private final OrcamentoRequestRepository orcamentoRequestRepo;

    public CompanyController(
            WebSearchService webSearchService,
            GeocodingService geocodingService,
            CompanyRepository companyRepo,
            OrcamentoRequestRepository orcamentoRequestRepo
    ) {
        this.webSearchService = webSearchService;
        this.geocodingService = geocodingService;
        this.companyRepo = companyRepo;
        this.orcamentoRequestRepo = orcamentoRequestRepo;
    }

    /**
     * GET /empresas?produto=Cimento&raioKM=20&cep=01001-000
     */
    @GetMapping
    public ResponseEntity<List<Company>> searchCompanies(
            @RequestParam("produto") String produto,
            @RequestParam("raioKM") Integer raioKM,
            @RequestParam("cep") String cep) {

        LatLng latLng = geocodingService.geocodeByCep(cep);
        List<Company> companies = webSearchService.searchCompaniesOnInternet(
                produto, raioKM, latLng.getLatitude(), latLng.getLongitude());

        return ResponseEntity.ok(companies);
    }

    /**
     * GET /empresas/{id}/empresas
     * Retorna empresas associadas ao orçamento de ID especificado
     */
    @GetMapping("/{id}/empresas")
    public ResponseEntity<List<Company>> listarEmpresasAssociadas(@PathVariable Integer id) {
        OrcamentoRequest req = orcamentoRequestRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado"));

        List<Company> empresas = companyRepo.findByOrcamentoRequest(req);
        return ResponseEntity.ok(empresas);
    }
}

