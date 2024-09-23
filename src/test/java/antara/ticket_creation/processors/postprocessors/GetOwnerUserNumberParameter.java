package antara.ticket_creation.processors.postprocessors;

import org.apache.log4j.Logger;
import us.abstracta.jmeter.javadsl.core.postprocessors.DslJsr223PostProcessor.PostProcessorScript;
import us.abstracta.jmeter.javadsl.core.postprocessors.DslJsr223PostProcessor.PostProcessorVars;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class GetOwnerUserNumberParameter implements PostProcessorScript {
    private static final Logger log = Logger.getLogger(GetOwnerUserNumberParameter.class);

    @Override
    public void runScript(PostProcessorVars s) {

        String username = s.vars.get("username");
        Pattern pattern = Pattern.compile("[\'\"](\\d+)[\'\"].?." + username);
        String responseBody = s.prev.getResponseDataAsString();
        Matcher matcher = pattern.matcher(responseBody);

        String owner_user_number;
        if (matcher.find()) {
            owner_user_number = matcher.group(1);
            s.vars.put("owner_user_number", owner_user_number);
        }
        else log.error("Проверьте регулярное выражение в GetOwnerUserNumberParameter классе");
    }
}