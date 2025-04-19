package git.yannynz.searchIt.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GooglePlaceResponse {
    private String status;
    private List<GooglePlaceResult> results;
}

