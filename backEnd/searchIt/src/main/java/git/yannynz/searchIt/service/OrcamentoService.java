package git.yannynz.searchIt.service;

import git.yannynz.searchIt.model.Company;
import git.yannynz.searchIt.model.OrcamentoRequest;
import git.yannynz.searchIt.repository.CompanyRepository;
import git.yannynz.searchIt.repository.OrcamentoRequestRepository;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import git.yannynz.searchIt.model.OrcamentoRequest;


/**
 * Orquestra o fluxo de criação de solicitação de orçamento:
 * 1. Salva no banco (OrcamentoRequest)
 * 2. Busca empresas que tenham a especialidade (produto)
 * 3. Para cada empresa, faz scraping do site (ou usa e-mail existente)
 * 4. Envia e-mail de solicitação
 */
@Service
public class OrcamentoService {

    private final OrcamentoRequestRepository orcamentoRequestRepo;
    private final CompanyRepository companyRepo;
    private final ScraperService scraperService;
    private final EmailService emailService;

    public OrcamentoService(
            OrcamentoRequestRepository orcamentoRequestRepo,
            CompanyRepository companyRepo,
            ScraperService scraperService,
            EmailService emailService
    ) {
        this.orcamentoRequestRepo = orcamentoRequestRepo;
        this.companyRepo = companyRepo;
        this.scraperService = scraperService;
        this.emailService = emailService;
    }

    /**
     * Cria a solicitação de orçamento, busca empresas e dispara os e-mails.
     *
     * @param req dados do usuário e do produto que ele deseja
     * @return a solicitação salva no banco
     * @throws IllegalArgumentException se faltam dados obrigatórios
     */
    public OrcamentoRequest criarOrcamento(OrcamentoRequest req) {
        // Validações simples
        if (req.getProduto() == null || req.getProduto().isBlank()) {
            throw new IllegalArgumentException("Produto é obrigatório");
        }
        if (req.getNome() == null || req.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório");
        }

        // Salva a solicitação no BD
        OrcamentoRequest savedReq = orcamentoRequestRepo.save(req);

        // Busca empresas que tenham "especialidade" == produto (ignora case)
        List<Company> empresas = companyRepo.findByEspecialidadeIgnoreCase(req.getProduto());

        // Se quiser filtrar por raioKM, faça a lógica aqui (ex.: comparar lat/long com a do user)

        // Para cada empresa, obtém e-mails via scraping e envia a solicitação
        for (Company c : empresas) {
            // Tenta extrair e-mails do website
            Set<String> foundEmails = scraperService.scrapeEmails(c.getWebsite());

            // Se nenhum e-mail for encontrado no site, podemos usar o e-mail cadastrado da empresa
            if (foundEmails.isEmpty() && c.getEmail() != null) {
                foundEmails.add(c.getEmail());
            }

            // Enviar e-mail para cada endereço encontrado
            for (String emailEncontrado : foundEmails) {
                enviarSolicitacao(emailEncontrado, savedReq, c);
            }

            // Marca que o e-mail foi enviado
            c.setEmailEnviado(true);
            companyRepo.save(c);
        }

        return savedReq;
    }

    /**
     * Monta o texto do e-mail e dispara via EmailService.
     */
    private void enviarSolicitacao(String emailDestino, OrcamentoRequest req, Company c) {
        String subject = "Orçamento - " + req.getProduto();
        String body = String.format(
            "Olá, meu nome é %s (CPF/CNPJ: %s).\n" +
            "Preciso de orçamento para o produto '%s' (quantidade: %d).\n" +
            "Por favor, responda para o meu e-mail: %s.\n\n" +
            "Obrigado,\n%s\n",
            req.getNome(),
            req.getCpfCnpj(),
            req.getProduto(),
            req.getQuantidade(),
            req.getEmail(),
            req.getNome()
        );

        emailService.sendEmail(emailDestino, subject, body);
    }

    /**
     * Método opcional para listar as Requests existentes
     */
    public List<OrcamentoRequest> listarOrcamentos() {
        return orcamentoRequestRepo.findAll();
    }
}

