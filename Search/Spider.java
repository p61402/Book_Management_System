package sample;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Spider extends Thread {
    private static final int MAX_PAGES_TO_SEARCH = 50;
    private Set<String> pagesVisited = new HashSet<>();
    private List<String> pagesToVisit = new LinkedList<>();
    private List<String> pagesStored = new LinkedList<>();
    private List<String> titlesStored = new LinkedList<>();
    private String url;
    private String searchWord;
    private int mode;

    public Spider(String url, String searchWord, int mode) {
        this.url = url;
        this.searchWord = searchWord;
        this.mode = mode;
    }

    public void run() {
        if (mode == 1)
            search(url, searchWord);
        else if (mode == 2)
            picture(url, searchWord);
        else
            System.out.println("**WARNING**: Wrong Mode!");
    }

    private void search(String url, String searchWord) {
        while (pagesVisited.size() < MAX_PAGES_TO_SEARCH) {
            String currentUrl;
            CrawlWebsite leg = new CrawlWebsite();
            if (this.pagesToVisit.isEmpty()) {
                currentUrl = url;
                pagesVisited.add(url);
            } else {
                currentUrl = nextUrl();
            }

            leg.crawl(currentUrl);
            // Lots of stuff happening here. Look at the crawl method in sample.CrawlWebsite

            boolean success = leg.searchForWord(searchWord);
            if (success) {
                //System.out.println(String.format("*Success*  Word %s found at %s", searchWord, currentUrl));
                pagesStored.add(currentUrl);
                titlesStored.add(leg.getTitle());
                //break;
            }
            pagesToVisit.addAll(leg.getLinks());
        }

        System.out.println("\n*Done* Visited " + pagesVisited.size() + " web page(s)");
        System.out.println("\nTotal " + titlesStored.size() + " pages found.");
        //printTitles();
        //printUrls();
    }

    private void picture(String url, String searchWord) {
        CrawlPicture leg = new CrawlPicture();

        leg.crawl(url, searchWord);

        pagesStored = leg.getLinks();
        titlesStored = leg.getTitles();
    }

    private String nextUrl() {
        String nextUrl;
        do {
            nextUrl = pagesToVisit.remove(0);
        } while (pagesVisited.contains(nextUrl));
        pagesVisited.add(nextUrl);
        return nextUrl;
    }

    private void printUrls() {
        for (String page : pagesStored)
            System.out.println(page);
    }

    private void printTitles() {
        for (String title : titlesStored)
            System.out.println(title);
    }

    public List<String> getTitlesStored() {
        return titlesStored;
    }

    public List<String> getPagesStored() { return pagesStored; }

    public double get_progress() {
            return (double)pagesVisited.size() / MAX_PAGES_TO_SEARCH;
    }

    public int getMode() {
        return this.mode;
    }
}
