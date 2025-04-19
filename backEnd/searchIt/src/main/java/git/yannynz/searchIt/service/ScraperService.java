package git.yannynz.searchIt.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ScraperService {

    // Caminhos comuns onde e-mails são encontrados
    private static final List<String> commonPaths = List.of(
        "/", "/contato", "/fale-conosco", "/sobre", "/empresa", "/quem-somos"
    );

    // Regex para capturar e-mails válidos
    private static final Pattern EMAIL_REGEX = Pattern.compile(
        "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

    public Set<String> scrapeEmails(String baseUrl) {
        Set<String> emails = new HashSet<>();

        if (baseUrl == null || baseUrl.isBlank()) return emails;

        for (String path : commonPaths) {
            String fullUrl = buildFullUrl(baseUrl, path);

            try {
                Document doc = Jsoup.connect(fullUrl)
                        .userAgent("Mozilla/5.0")
                        .timeout(5000)
                        .get();

                // 1. Emails por texto
                Matcher matcher = EMAIL_REGEX.matcher(doc.text());
                while (matcher.find()) {
                    emails.add(matcher.group());
                }

                // 2. Emails por atributos href="mailto:..."
                for (Element el : doc.select("a[href^=mailto]")) {
                    String href = el.attr("href").replace("mailto:", "").trim();
                    if (!href.isEmpty()) emails.add(href);
                }

                // Se encontrou ao menos um email, já retorna
                if (!emails.isEmpty()) break;

            } catch (IOException e) {
                // Ignora páginas inacessíveis
                System.err.println("Falha ao acessar: " + fullUrl);
            }
        }

        return emails;
    }

    private String buildFullUrl(String base, String path) {
        if (!base.startsWith("http")) base = "https://" + base;
        if (base.endsWith("/")) base = base.substring(0, base.length() - 1);
        return base + path;
    }
}

