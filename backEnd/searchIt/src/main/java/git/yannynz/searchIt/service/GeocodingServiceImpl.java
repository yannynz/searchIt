package git.yannynz.searchIt.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GeocodingServiceImpl implements GeocodingService {

    @Value("${google.geocoding.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public GeocodingServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public LatLng geocodeByCep(String cep) {
        String url = UriComponentsBuilder
            .fromHttpUrl("https://maps.googleapis.com/maps/api/geocode/json")
            .queryParam("address", cep)
            .queryParam("key", apiKey)
            .toUriString();

        GeocodingResponse resp = restTemplate
            .getForObject(url, GeocodingResponse.class);

        if (resp != null 
         && "OK".equalsIgnoreCase(resp.getStatus())
         && !resp.getResults().isEmpty()) {
            Location loc = resp.getResults()
                               .get(0)
                               .getGeometry()
                               .getLocation();
            return new LatLng(loc.getLat(), loc.getLng());
        }
        throw new IllegalArgumentException("Não foi possível geocodificar o CEP: " + cep);
    }
}

