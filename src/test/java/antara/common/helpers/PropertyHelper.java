package antara.common.helpers;

import us.abstracta.jmeter.javadsl.core.engines.EmbeddedJmeterEngine;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyHelper {
    public static void setProperty(String propName, String defaultValue, Properties fileProperties, Properties properties) {
        properties.setProperty(
                propName, System.getProperty(
                        propName, fileProperties.getProperty(
                                propName, defaultValue)));

    }

    static public Properties readFileProperties(String path) throws IOException {
        ClassLoader classLoader = PropertyHelper.class.getClassLoader();
        InputStream is = classLoader.getResourceAsStream(path);
        Properties properties = new Properties();
        properties.load(is);
        return properties;
    }

    static public void setPropertiesToEngine(EmbeddedJmeterEngine engine, Properties properties) {
        properties.forEach(
                (k, v) ->  engine
                        .prop(k.toString(), v)
        );
    }

    static public Properties readCommonProperties() throws IOException {
        Properties properties = new Properties();
        String commonPath = "common/common_test.properties";

        Properties fileProperties = readFileProperties(commonPath);
        setProperty("HELPDESK_HOST", "localhost", fileProperties, properties);
        setProperty("INFLUXDB_HOST", "localhost", fileProperties, properties);
        setProperty("PROTOCOL", "http", fileProperties, properties);
        setProperty("DEBUG_ENABLE", "true", fileProperties, properties);
        setProperty("ERROR_LOG_ENABLE", "true", fileProperties, properties);
        setProperty("INFLUX_DB_LOG_ENABLE", "false", fileProperties, properties);
        setProperty("RESULT_TREE_ENABLE", "true", fileProperties, properties);
        setProperty("RESULT_DASHBOARD_ENABLE", "true", fileProperties, properties);
        setProperty("DEBUG_POSTPROCESSOR_ENABLE", "false", fileProperties, properties);
        setProperty("PORT", "23232", fileProperties, properties);


        return properties;
    }

}
