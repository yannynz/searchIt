package git.yannynz.searchIt.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodingResponse {
    private List<GeocodingResult> results;
    private String status;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class GeocodingResult {
    private Geometry geometry;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class Geometry {
    private Location location;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class Location {
    private double lat;
    private double lng;
}

