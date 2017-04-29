package sample;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class CrawlWebsite {
    // We'll use a fake USER_AGENT so the web server thinks the robot is a normal web browser.
    private static final String USER_AGENT = "Chrome/56.0.2924.87";
    private List<String> links = new LinkedList<>();
    private Document htmlDocument;

    public boolean crawl(String url) {
        try {
            Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
            Document htmlDocument =connection.get();
            this.htmlDocument = htmlDocument;

            if (connection.response().statusCode() == 200) {
                System.out.println("\n**Visiting** Received web page at " + url);
            }

            if (!connection.response().contentType().contains("text/html")) {
                System.out.println("**Failure** Retrieved something other than HTML");
                return false;
            }

            Elements linksOnPage = htmlDocument.select("a[href]");
            System.out.println("Found (" + linksOnPage.size() + ") links");

            for (Element link : linksOnPage) {
                this.links.add(link.absUrl("href"));
                //System.out.println(link.absUrl("href"));
            }

            return true;
        }
        catch (IOException ioe) {
            return false;
        }
    }

    public boolean searchForWord(String searchWord) {
        if (htmlDocument == null) {
            System.out.println("Error! Empty page.");
            return false;
        }

        System.out.println("Searching for the word '" + searchWord + "'..");
        String bodyText = htmlDocument.body().text();

        return bodyText.toLowerCase().contains(searchWord.toLowerCase());
    }

    public List<String> getLinks() {
        return links;
    }

    public String getTitle() {
        return htmlDocument.title();
    }
}
