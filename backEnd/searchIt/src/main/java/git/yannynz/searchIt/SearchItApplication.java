package git.yannynz.searchIt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SearchItApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchItApplication.class, args);
	}

}
