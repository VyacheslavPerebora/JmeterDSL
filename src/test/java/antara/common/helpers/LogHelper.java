package antara.common.helpers;

import us.abstracta.jmeter.javadsl.core.DslTestPlan;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;

import java.time.Duration;

import static us.abstracta.jmeter.javadsl.JmeterDsl.constantTimer;
import static us.abstracta.jmeter.javadsl.JmeterDsl.influxDbListener;


public class LogHelper {

    public static String getTestResultString(TestPlanStats status) {
        return String.format("""


                        ===============================================
                        Test results
                        Errors: %s
                        Duration: %02d:%02d:%02d
                        ===============================================""",
                status.overall().errorsCount(),
                status.duration().toHoursPart(), status.duration().toMinutesPart(), status.duration().toSecondsPart());
    }

    public static DslTestPlan.TestPlanChild influxDbLog(boolean enable) {
        if (enable) {
            return influxDbListener("http://${__P(INFLUXDB_HOST)}:8086/write?db=jmeter")
                    .title("Test")
                    .samplersRegex(".*")
                    .tag("nodeName", "${__machineName()}")
                    .tag("runId", "Reinsured");
        } else {
            return constantTimer(Duration.ZERO);
        }
    }

}