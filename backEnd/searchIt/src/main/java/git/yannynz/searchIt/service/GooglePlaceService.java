package git.yannynz.searchIt.service;

import git.yannynz.searchIt.model.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GooglePlaceService implements WebSearchService {

    @Value("${google.places.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    @Override
    public List<Company> searchCompaniesOnInternet(
            String produto,
            Integer raioKM,
            double latitude,
            double longitude) {

        List<Company> companies = new ArrayList<>();

        int radiusMeters = (raioKM != null ? raioKM : 20) * 1000;

        String url = UriComponentsBuilder
                .fromHttpUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
                .queryParam("location", latitude + "," + longitude)
                .queryParam("radius", radiusMeters)
                .queryParam("keyword", produto)
                .queryParam("key", apiKey)
                .toUriString();

        // Corrigir nome da classe DTO abaixo, se necessário
        GooglePlaceResponse resp = restTemplate.getForObject(url, GooglePlaceResponse.class);

        if (resp != null && "OK".equalsIgnoreCase(resp.getStatus())) {
            resp.getResults().forEach(r -> {
                companies.add(Company.builder()
                        .nome(r.getName())
                        .especialidade(produto)
                        .website("") // você pode buscar via Place Details se quiser
                        .emailEnviado(false)
                        .build());
            });
        }

        return companies;
    }
}

