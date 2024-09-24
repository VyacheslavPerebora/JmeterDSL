package antara.transfer_task_by_statuses;

import antara.avtorization_admin.helpers.AdminLoginPropertyHelper;
import antara.common.helpers.PropertyHelper;
import antara.pagination.fragments.PaginationFragment;
import antara.ticket_creation.samplers.TicketCreationThreadGroup;
import antara.transfer_task_by_statuses.fragments.TransferTaskByStatusesFragment;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;
import us.abstracta.jmeter.javadsl.core.engines.EmbeddedJmeterEngine;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import static antara.common.helpers.ActionHelper.testAction;
import static antara.common.helpers.CacheHelper.getCacheManager;
import static antara.common.helpers.CookiesHelper.getCookiesClean;
import static antara.common.helpers.HeadersHelper.getHeaders;
import static antara.common.helpers.HttpHelper.getHttpDefaults;
import static antara.common.helpers.LogHelper.getTestResultString;
import static antara.common.helpers.LogHelper.influxDbLog;
import static antara.common.helpers.VisualizersHelper.*;
import static us.abstracta.jmeter.javadsl.JmeterDsl.*;


public class TransferTaskByStatusesTest {
    boolean debugEnable;
    boolean errorLogEnable;
    boolean influxDbLogEnable;
    boolean resultTreeEnable;
    boolean resultDashboardEnable;
    boolean debugPostProcessorEnable;
    double throughputPerMinute;

    static final Logger logger = Logger.getLogger(TransferTaskByStatusesTest.class);
    EmbeddedJmeterEngine embeddedJmeterEngine = new EmbeddedJmeterEngine();
    Properties properties = new Properties();

    @BeforeTest
    private void init() throws IOException {
        properties = AdminLoginPropertyHelper.readAdminLoginProperties();
        PropertyHelper.setPropertiesToEngine(embeddedJmeterEngine, properties);

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
        TransferTaskByStatusesFragment transferTaskByStatusesFragment = new TransferTaskByStatusesFragment();

        TestPlanStats run = testPlan(
                getCookiesClean(),
                getCacheManager(),
                getHeaders(),
                getHttpDefaults(),
                TicketCreationThreadGroup.getThreadGroup("TG_TRANSFER_TASK_BY_STATUSES", debugEnable)
                        .children(
                                ifController(s -> !debugEnable,
                                        testAction(throughputTimer(throughputPerMinute).perThread())
                                ),
                                transaction("UC_TRANSFER_TASK_BY_STATUSES",
                                        transferTaskByStatusesFragment.get()
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