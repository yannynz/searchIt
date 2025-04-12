package git.yannynz.searchIt.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ScraperService {

    // Regex simples para e-mail (pode refinar depois)
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

    public Set<String> scrapeEmails(String url) {
        Set<String> emails = new HashSet<>();
        if (url == null || url.isBlank()) {
            return emails; // sem URL, sem scraping
        }
        try {
            // Faz GET na página
            Document doc = Jsoup.connect(url)
                                .userAgent("Mozilla/5.0")
                                .timeout(5000)
                                .get();
            // Extrai todo texto
            String text = doc.text();
            // Procura por emails via regex
            Matcher matcher = EMAIL_PATTERN.matcher(text);
            while (matcher.find()) {
                emails.add(matcher.group());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return emails;
    }
}

