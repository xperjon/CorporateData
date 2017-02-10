package se.blinfo.corporate.data.core;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import se.blinfo.corporate.data.core.utils.CinUtils;
import se.xperjon.webscraper.Scraper;

/**
 *
 * @author jel
 */
public class AllaBolag {

    private static final Scraper scraper = new Scraper();
    private static final String URL = "http://www.allabolag.se/";

    public String companyName(String cin) {
        Document doc = scraper.get(URL + CinUtils.formatCIN(cin));
        Elements select = doc.select("h1.p-name");
        //DOTO felkoll
        return select.get(0).text();
    }

    public String zipCode(String cin) {
        Document doc = scraper.get(URL + CinUtils.formatCIN(cin));
        Elements select = doc.select("span.p-postal-code");
        //DOTO felkoll
        return select.get(0).text();
    }

    public String city(String cin) {
        Document doc = scraper.get(URL + CinUtils.formatCIN(cin));
        Elements select = doc.select("span.p-locality");
        //DOTO felkoll
        return select.get(0).text();
    }

    public String region(String cin) {
        Document doc = scraper.get(URL + CinUtils.formatCIN(cin));
        Elements select = doc.select("span.p-region");
        //DOTO felkoll
        return select.get(0).text();
    }

    public String phone(String cin) {
        Document doc = scraper.get(URL + CinUtils.formatCIN(cin));
        Elements select = doc.select("a.p-tel");
        //DOTO felkoll
        return select.get(0).text();
    }

}
