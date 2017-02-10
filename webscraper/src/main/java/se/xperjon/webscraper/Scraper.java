package se.xperjon.webscraper;

import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.xperjon.webscraper.utils.UrlUtils;

/**
 *
 * @author jel
 */
public class Scraper {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private String charset = null;
    private static final CloseableHttpClient client = HttpClients.createDefault();

    public Document get(String url) {
        String raw = getRaw(url);
        logger.info("Parsing result");
        return Jsoup.parse(raw);
    }

    public String getRaw(String url) {
        String charset = null;

        RequestBuilder get = RequestBuilder.get();
        get.setUri(url);
        get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:51.0) Gecko/20100101 Firefox/51.0");

        HttpResponse httpResponse = null;
        try {
            logger.info("Fetching from allabolag.se");
            long startTime = System.nanoTime();
            httpResponse = client.execute(get.build());
            long endTime = System.nanoTime();
            long durationMS = (endTime - startTime) / 1000000;
            logger.info("done in " + durationMS);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return getContent(charset, httpResponse);
            } else {
                logger.warn("get page {} error, status code {} ", url, statusCode);
                return null;
            }
        } catch (IOException e) {
            logger.warn("download page {} error", url, e);
            return null;
        } finally {
            try {
                if (httpResponse != null) {
                    //ensure the connection is released back to pool
                    EntityUtils.consume(httpResponse.getEntity());
                }
            } catch (IOException e) {
                logger.warn("close response fail", e);
            }
        }
    }

    protected String getContent(String charset, HttpResponse httpResponse) throws IOException {
        if (charset == null) {
            byte[] contentBytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
            String htmlCharset = getHtmlCharset(httpResponse, contentBytes);
            if (htmlCharset != null) {
                return new String(contentBytes, htmlCharset);
            } else {
                logger.warn("Charset autodetect failed, use {} as charset. Please specify charset in Site.setCharset()", Charset.defaultCharset());
                return new String(contentBytes);
            }
        } else {
            return IOUtils.toString(httpResponse.getEntity().getContent(), charset);
        }
    }

    protected String getHtmlCharset(HttpResponse httpResponse, byte[] contentBytes) throws IOException {
        String charset;
        // charset
        // 1、encoding in http header Content-Type
        String value = httpResponse.getEntity().getContentType().getValue();
        charset = UrlUtils.getCharset(value);
        if (StringUtils.isNotBlank(charset)) {
            logger.debug("Auto get charset: {}", charset);
            return charset;
        }
        // use default charset to decode first time
        Charset defaultCharset = Charset.defaultCharset();
        String content = new String(contentBytes, defaultCharset.name());
        // 2、charset in meta
        if (StringUtils.isNotEmpty(content)) {
            Document document = Jsoup.parse(content);
            Elements links = document.select("meta");
            for (Element link : links) {
                // 2.1、html4.01 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                String metaContent = link.attr("content");
                String metaCharset = link.attr("charset");
                if (metaContent.indexOf("charset") != -1) {
                    metaContent = metaContent.substring(metaContent.indexOf("charset"), metaContent.length());
                    charset = metaContent.split("=")[1];
                    break;
                } // 2.2、html5 <meta charset="UTF-8" />
                else if (StringUtils.isNotEmpty(metaCharset)) {
                    charset = metaCharset;
                    break;
                }
            }
        }
        logger.debug("Auto get charset: {}", charset);
        // 3、todo use tools as cpdetector for content decode
        return charset;
    }
}
