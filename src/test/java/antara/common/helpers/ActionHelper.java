package antara.common.helpers;

import org.apache.jmeter.sampler.TestAction;
import us.abstracta.jmeter.javadsl.core.postprocessors.DslJsr223PostProcessor;
import us.abstracta.jmeter.javadsl.core.preprocessors.DslJsr223PreProcessor;
import us.abstracta.jmeter.javadsl.core.samplers.BaseSampler;
import us.abstracta.jmeter.javadsl.wrapper.wrappers.DslSamplerWrapper;

import static us.abstracta.jmeter.javadsl.JmeterDsl.jsr223PostProcessor;
import static us.abstracta.jmeter.javadsl.JmeterDsl.jsr223PreProcessor;
import static us.abstracta.jmeter.javadsl.wrapper.WrapperJmeterDsl.testElement;

public class ActionHelper {
    public static TestAction emptyAction() {
        TestAction action = new TestAction();
        action.setAction(1);
        action.setDuration("0");
        return action;
    }

    public static DslSamplerWrapper testAction(BaseSampler.SamplerChild... children) {
        return testElement(emptyAction()).children(children);
    }

    public static DslSamplerWrapper jsr223Action(DslJsr223PreProcessor.PreProcessorScript script) {
        return testElement(emptyAction()).children(
                jsr223PreProcessor(script)
        );
    }

    public static DslSamplerWrapper jsr223Action(Class<? extends DslJsr223PreProcessor.PreProcessorScript> scriptClass) {
        return testElement(emptyAction()).children(
                jsr223PreProcessor(scriptClass)
        );
    }

    public static DslSamplerWrapper jsr223Action(String name, DslJsr223PreProcessor.PreProcessorScript script) {
        return testElement(emptyAction()).children(
                jsr223PreProcessor(name, script)
        );
    }

    public static DslSamplerWrapper jsr223Action(String name, Class<? extends DslJsr223PreProcessor.PreProcessorScript> scriptClass) {
        return testElement(emptyAction()).children(
                jsr223PreProcessor(name, scriptClass)
        );
    }

    public static DslSamplerWrapper jsr223ActionPost( Class<? extends DslJsr223PostProcessor.PostProcessorScript> scriptClass) {
        return testElement(emptyAction()).children(
                jsr223PostProcessor(scriptClass)
        );
    }
}