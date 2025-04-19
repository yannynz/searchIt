package git.yannynz.searchIt.service;

import git.yannynz.searchIt.model.OrcamentoRequest;
import git.yannynz.searchIt.repository.CompanyRepository;
import git.yannynz.searchIt.repository.OrcamentoRequestRepository;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import git.yannynz.searchIt.model.Company;

@Service
public class OrcamentoService {

    private final OrcamentoRequestRepository orcamentoRequestRepo;
    private final CompanyRepository companyRepo;
    private final ScraperService scraperService;
    private final EmailSenderService emailSenderService;
    private final WebSearchService webSearchService;
    private final GeocodingService geocodingService;

    public OrcamentoService(OrcamentoRequestRepository orcamentoRequestRepo,
            CompanyRepository companyRepo,
            ScraperService scraperService,
            EmailSenderService emailSenderService,
            WebSearchService webSearchService,
            GeocodingService geocodingService) {
        this.orcamentoRequestRepo = orcamentoRequestRepo;
        this.companyRepo = companyRepo;
        this.scraperService = scraperService;
        this.emailSenderService = emailSenderService;
        this.webSearchService = webSearchService;
        this.geocodingService = geocodingService;
    }

    @Transactional
    public OrcamentoRequest criarOrcamento(OrcamentoRequest req) {
        validarRequisicao(req);

        // 1. Salva solicitação
        OrcamentoRequest savedReq = orcamentoRequestRepo.save(req);

        // 2. Geocodifica o CEP
        LatLng userLocation = geocodingService.geocodeByCep(req.getCep());

        // 3. Busca empresas no raio
        List<Company> empresas = webSearchService
                .searchCompaniesOnInternet(
                        req.getProduto(),
                        req.getRaioKM(),
                        userLocation.getLatitude(),
                        userLocation.getLongitude());

        // 4. Persiste histórico
        for (Company c : empresas) {
            c.setOrcamentoRequest(savedReq); 
            companyRepo.save(c);
        }

        // 5. Scraping e envio de e-mail
        for (Company c : empresas) {
            Set<String> foundEmails = scraperService.scrapeEmails(c.getWebsite());
            if (foundEmails.isEmpty() && c.getEmail() != null) {
                foundEmails.add(c.getEmail());
            }
            for (String emailDestino : foundEmails) {
                enviarSolicitacao(emailDestino, savedReq, c);
            }
            c.setEmailEnviado(true);
            companyRepo.save(c);
        }

        return savedReq;
    }

    private void validarRequisicao(OrcamentoRequest req) {
        if (req.getProduto() == null || req.getProduto().isBlank())
            throw new IllegalArgumentException("Produto é obrigatório");
        if (req.getNome() == null || req.getNome().isBlank())
            throw new IllegalArgumentException("Nome do cliente é obrigatório");
        if (req.getEmail() == null || req.getEmail().isBlank())
            throw new IllegalArgumentException("E-mail é obrigatório");
        if (req.getCpfCnpj() == null || req.getCpfCnpj().isBlank())
            throw new IllegalArgumentException("CPF/CNPJ é obrigatório");
        if (req.getCep() == null || req.getCep().isBlank())
            throw new IllegalArgumentException("CEP é obrigatório");
        if (req.getRaioKM() == null || req.getRaioKM() <= 0)
            throw new IllegalArgumentException("Raio em KM deve ser > 0");
    }

    private void enviarSolicitacao(String emailDestino,
            OrcamentoRequest req,
            Company c) {
        String subject = "Orçamento - " + req.getProduto();
        String body = String.format(
                "Olá, meu nome é %s (CPF/CNPJ: %s).\n" +
                        "Solicito orçamento para '%s' (quantidade %d).\n" +
                        "Por favor, responda para: %s.\n\nAtt,\n%s",
                req.getNome(), req.getCpfCnpj(),
                req.getProduto(), req.getQuantidade(),
                req.getEmail(), req.getNome());
        emailSenderService.sendEmail(emailDestino, subject, body);
    }

    public List<OrcamentoRequest> listarOrcamentos() {
        return orcamentoRequestRepo.findAll();
    }

    public OrcamentoRequest buscarOrcamentoPorId(Integer id) {
        return orcamentoRequestRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado"));
    }
}
