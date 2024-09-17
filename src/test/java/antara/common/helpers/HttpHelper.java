package antara.common.helpers;

import us.abstracta.jmeter.javadsl.http.DslHttpDefaults;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;

import static us.abstracta.jmeter.javadsl.JmeterDsl.httpDefaults;



public class HttpHelper {

    public static int getPort() throws IOException {

        Properties props = PropertyHelper.readCommonProperties();
        return Integer.parseInt(props.getProperty("PORT"));
    }

    public static DslHttpDefaults getHttpDefaults() throws IOException {

        return httpDefaults()
                .protocol("${__P(PROTOCOL)}")
                .host("${__P(HELPDESK_HOST)}")
                .port(getPort())
                .encoding(StandardCharsets.UTF_8)
                .connectionTimeout(Duration.ofSeconds(3))
                .responseTimeout(Duration.ofSeconds(10));
    }

}