package antara.common.helpers;

import org.apache.http.entity.ContentType;
import us.abstracta.jmeter.javadsl.http.HttpHeaders;

import static us.abstracta.jmeter.javadsl.JmeterDsl.httpHeaders;

public class HeadersHelper {
    public static HttpHeaders getHeaders() {
        return httpHeaders()
                .header("Connection", "keep-alive")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:120.0) Gecko/20100101 Firefox/120.0");
    }

}