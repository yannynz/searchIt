package git.yannynz.searchIt.controller;

import git.yannynz.searchIt.model.OrcamentoRequest;
import git.yannynz.searchIt.service.OrcamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orcamentos")
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    public OrcamentoController(OrcamentoService orcamentoService) {
        this.orcamentoService = orcamentoService;
    }

    // POST /orcamentos
    // Recebe nome, cpfCnpj, email, produto, quantidade, raioKM (JSON)
    @PostMapping
    public ResponseEntity<OrcamentoRequest> criar(@RequestBody OrcamentoRequest req) {
        OrcamentoRequest salvo = orcamentoService.criarOrcamento(req);
        return ResponseEntity.ok(salvo);
    }

    // GET /orcamentos
    // Lista todas as solicitações já feitas
    @GetMapping
    public ResponseEntity<List<OrcamentoRequest>> listar() {
        List<OrcamentoRequest> lista = orcamentoService.listarOrcamentos();
        return ResponseEntity.ok(lista);
    }
}

