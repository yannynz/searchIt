package git.yannynz.searchIt.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class GooglePlaceResult {
    private String name;
    private String place_id;
}

