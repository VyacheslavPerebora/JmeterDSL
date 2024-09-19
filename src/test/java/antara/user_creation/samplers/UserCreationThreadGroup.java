package antara.user_creation.samplers;

import us.abstracta.jmeter.javadsl.core.threadgroups.BaseThreadGroup;
import us.abstracta.jmeter.javadsl.core.threadgroups.DslSetupThreadGroup;
import us.abstracta.jmeter.javadsl.core.threadgroups.DslSimpleThreadGroup;

import java.time.Duration;

import static us.abstracta.jmeter.javadsl.JmeterDsl.setupThreadGroup;
import static us.abstracta.jmeter.javadsl.JmeterDsl.threadGroup;

public class UserCreationThreadGroup {

    public static BaseThreadGroup<?> getThreadGroup(String name, boolean debug) {
        if (debug) {
            return  threadGroup(name, 1, 1);
        } else {
            return threadGroup(name)
                    .rampToAndHold(1, Duration.ofSeconds(0), Duration.ofSeconds(30));
        }
    }

}