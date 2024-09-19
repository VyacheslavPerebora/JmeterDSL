package antara.user_creation;

import antara.avtorization_admin.fragments.AvtorizationAdminFragment;
import antara.avtorization_admin.helpers.AdminLoginPropertyHelper;
import antara.user_creation.fragments.UserCreationFragment;
import antara.user_creation.samplers.UserCreationThreadGroup;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;
import us.abstracta.jmeter.javadsl.core.engines.EmbeddedJmeterEngine;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import static antara.common.helpers.ActionHelper.jsr223Action;
import static antara.common.helpers.ActionHelper.testAction;
import static antara.common.helpers.CacheHelper.getCacheManager;
import static antara.common.helpers.CookiesHelper.getCookiesClean;
import static antara.common.helpers.HeadersHelper.getHeaders;
import static antara.common.helpers.HttpHelper.getHttpDefaults;
import static antara.common.helpers.LogHelper.getTestResultString;
import static antara.common.helpers.LogHelper.influxDbLog;
import static antara.common.helpers.VisualizersHelper.*;
import static us.abstracta.jmeter.javadsl.JmeterDsl.*;


public class UserCreationTest {
    boolean debugEnable;
    boolean errorLogEnable;
    boolean influxDbLogEnable;
    boolean resultTreeEnable;
    boolean resultDashboardEnable;
    boolean debugPostProcessorEnable;
    double throughputPerMinute;

    static final Logger logger = LogManager.getLogger(UserCreationTest.class);
    EmbeddedJmeterEngine embeddedJmeterEngine = new EmbeddedJmeterEngine();
    Properties properties = new Properties();

    @BeforeTest
    private void init() throws IOException {
        properties = AdminLoginPropertyHelper.readAdminLoginProperties();
        AdminLoginPropertyHelper.setPropertiesToEngine(embeddedJmeterEngine, properties);


        debugEnable = Boolean.parseBoolean(properties.getProperty("DEBUG_ENABLE"));
        errorLogEnable = Boolean.parseBoolean(properties.getProperty("ERROR_LOG_ENABLE"));
        influxDbLogEnable = Boolean.parseBoolean(properties.getProperty("INFLUX_DB_LOG_ENABLE"));
        resultTreeEnable = Boolean.parseBoolean(properties.getProperty("RESULT_TREE_ENABLE"));
        resultDashboardEnable = Boolean.parseBoolean(properties.getProperty("RESULT_DASHBOARD_ENABLE"));
        debugPostProcessorEnable = Boolean.parseBoolean(properties.getProperty("DEBUG_POSTPROCESSOR_ENABLE"));
        throughputPerMinute = Double.parseDouble(properties.getProperty("THROUGHPUT"));
    }

    @SuppressWarnings("unused")
    @Test
    private void test() throws IOException, InterruptedException, TimeoutException {
        AvtorizationAdminFragment adminLoginFragment = new AvtorizationAdminFragment();
        UserCreationFragment userCreationFragment = new UserCreationFragment();


        TestPlanStats run = testPlan(
                getCookiesClean(),
                getCacheManager(),
                getHeaders(),
                getHttpDefaults(),
                UserCreationThreadGroup.getThreadGroup("TG_USER_CREATION", debugEnable)
                        .children(
                                ifController(s -> !debugEnable,
                                        testAction(throughputTimer(throughputPerMinute).perThread())
                                ),
                                transaction("UC_AVTORIZATION_ADMIN",
                                        adminLoginFragment.get()
                                ),
                                transaction("UC_USER_CREATION",
                                        userCreationFragment.get()
                                )
                        ),
                influxDbLog(influxDbLogEnable),
                resultTree(resultTreeEnable),
                resultDashboard(resultDashboardEnable),
                debugPostPro(debugPostProcessorEnable)

        ).runIn(embeddedJmeterEngine);

        logger.info(getTestResultString(run));

    }
}