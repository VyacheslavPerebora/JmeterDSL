package antara.admin_login.helpers;

import antara.common.helpers.PropertyHelper;

import java.io.IOException;
import java.util.Properties;

public class AdminLoginPropertyHelper extends PropertyHelper {

    public static Properties readAdminLoginProperties() throws IOException {
        String propertyPath = "admin_login/admin_login_test.properties";
        Properties properties = new Properties();
        Properties fileProperties = readFileProperties(propertyPath);
        properties.putAll(readCommonProperties());

        setProperty("ADMIN_LOGIN", "localhost", fileProperties, properties);
        setProperty("ADMIN_PASS", "localhost", fileProperties, properties);

        setProperty("THROUGHPUT", "45", fileProperties, properties);


       return properties;

    }

}