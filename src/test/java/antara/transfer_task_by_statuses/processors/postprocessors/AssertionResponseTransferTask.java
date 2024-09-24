package antara.transfer_task_by_statuses.processors.postprocessors;

import org.apache.log4j.Logger;
import us.abstracta.jmeter.javadsl.core.postprocessors.DslJsr223PostProcessor.PostProcessorScript;
import us.abstracta.jmeter.javadsl.core.postprocessors.DslJsr223PostProcessor.PostProcessorVars;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssertionResponseTransferTask implements PostProcessorScript {
    private static final Logger log = Logger.getLogger(AssertionResponseTransferTask.class);

    @Override
    public void runScript(PostProcessorVars s) {

        String newStatus = s.vars.get("new_status");
        Pattern pattern = Pattern.compile("radio.inline.+value=[\"\'](\\d+)[\"\'].+?checked");
        String responseBody = s.prev.getResponseDataAsString();
        Matcher matcher = pattern.matcher(responseBody);

            if (matcher.find()) {
                String result = matcher.group(1);
                assert newStatus.equals(result);
            }
            else {
                s.prev.setSuccessful(false);
                s.prev.setSampleLabel(">_/tickets/{random_ticket_id}/update/_FAILED");
                log.error("Совпадений с регулярным выражением не найдено");}
    }
}
