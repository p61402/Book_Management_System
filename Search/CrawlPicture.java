package sample;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class CrawlPicture {
    private static final String USER_AGENT = "Chrome/56.0.2924.87";
    private List<String> links = new LinkedList<>();
    private List<String> titles = new LinkedList<>();
    private Document htmlDocument;

    public boolean crawl(String url, String keyword) {
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

            Elements linksOnPage = htmlDocument.select("img");
            System.out.println("Found (" + linksOnPage.size() + ") links");

            for (Element link : linksOnPage) {
                this.links.add(link.attr("src"));
                this.titles.add(link.attr("src"));
                //System.out.println(link.absUrl("href"));
            }

            return true;
        }
        catch (IOException ioe) {
            return false;
        }
    }

    public List<String> getTitles() { return titles; }

    public List<String> getLinks() {
        return links;
    }
}
