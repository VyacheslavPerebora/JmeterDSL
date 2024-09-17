package antara.common.helpers;

import us.abstracta.jmeter.javadsl.core.DslTestPlan.TestPlanChild;

import java.time.Duration;

import static us.abstracta.jmeter.javadsl.JmeterDsl.*;
import static us.abstracta.jmeter.javadsl.dashboard.DashboardVisualizer.dashboardVisualizer;
public class VisualizersHelper {
    public static TestPlanChild resultTree(boolean enable) {
        if (enable) {
            return resultsTreeVisualizer();
        } else {
            return constantTimer(Duration.ZERO);
        }
    }

    public static TestPlanChild resultTree() {
        return resultsTreeVisualizer();
    }

    public static TestPlanChild resultDashboard(boolean enable) {
        if (enable) {
            return dashboardVisualizer();
        } else {
            return constantTimer(Duration.ZERO);
        }
    }

    public static TestPlanChild resultDashboard() {
        return dashboardVisualizer();
    }

    public static TestPlanChild debugPostPro(boolean enable) {
        if (enable) {
            return debugPostProcessor();
        } else {
            return constantTimer(Duration.ZERO);
        }
    }

    public static TestPlanChild debugPostPro() {
        return debugPostProcessor();
    }
}