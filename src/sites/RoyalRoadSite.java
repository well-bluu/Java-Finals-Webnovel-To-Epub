package sites;

import interfaces.NovelSite;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



// Handles scraping of chapter titles, content, and URLs.


public class RoyalRoadSite implements NovelSite {

    @Override
    public String getSiteKey() {
        return "royalroad";
    }

    // Returns the base referrer URL required for requests
    // Because some sites blocks requests withhout a valid referrer
    @Override
    public String getReferrer() {
        return "https://www.royalroad.com";
    }

    @Override
    public String extractSlug(String novelUrl) {
        String[] parts = novelUrl.split("/");
        String last = parts[parts.length - 1];
        return last.split("\\?")[0];
    }

    @Override
    public String parseTitle(Document doc, int chapterNumber) {
        Element el = doc.selectFirst("h1, .chapter-title");
        if (el != null && !el.text().isEmpty()) return el.text().trim();
        return "Chapter " + chapterNumber;
    }

    // Cleans chapters
    // Removes hidden elements, ads, and scripts.
    
    @Override
    public String parseContent(Document doc, String chapterUrl) {
        StringBuilder content = new StringBuilder();
        Element container = doc.selectFirst("div.chapter-content");

        if (container != null) {
            // Remove hidden anti-scraper messages
            container.select("[style*='display: none'], [style*='display:none']").remove();
            container.select("script, ins, .ads").remove();

            for (Element p : container.select("p")) {
                String text = p.text().trim();
                if (!text.isEmpty()) content.append(text).append("\n\n");
            }
        }

        return content.length() > 0 ? content.toString().trim() : "Empty content.";
    }

    @Override
    public List<String> getChapterUrls(String novelUrl) throws IOException {
        List<String> urls = new ArrayList<>();

        Document doc = Jsoup.connect(novelUrl)
            .userAgent("Mozilla/5.0")
            .timeout(15_000)
            .get();

        // Extract all links
        for (Element link : doc.select("a[href]")) {
            String href = link.attr("abs:href");
            // Only keep chapter links and avoid duplicates
            if (href.contains("/chapter/") && !urls.contains(href)) {
                urls.add(href);
            }
        }

        System.out.println("[RoyalRoad] Found " + urls.size() + " chapters.");
        return urls;
    }
}