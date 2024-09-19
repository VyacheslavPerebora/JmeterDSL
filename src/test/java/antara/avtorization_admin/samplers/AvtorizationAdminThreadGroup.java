package antara.avtorization_admin.samplers;

import us.abstracta.jmeter.javadsl.core.threadgroups.BaseThreadGroup;

import java.time.Duration;

import static us.abstracta.jmeter.javadsl.JmeterDsl.httpSampler;
import static us.abstracta.jmeter.javadsl.JmeterDsl.threadGroup;

public class AvtorizationAdminThreadGroup {
    public static BaseThreadGroup<?> getThreadGroup(String name, boolean debug) {
        if (debug) {
            return threadGroup(name, 1, 1);
        } else {
            return threadGroup(name)
                    .rampToAndHold(1, Duration.ofSeconds(0), Duration.ofSeconds(30));
        }
    }

}