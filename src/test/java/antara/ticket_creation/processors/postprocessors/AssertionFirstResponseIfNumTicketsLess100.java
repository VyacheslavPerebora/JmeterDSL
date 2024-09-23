package antara.ticket_creation.processors.postprocessors;

import org.apache.jmeter.assertions.AssertionResult;
import org.apache.log4j.Logger;
import us.abstracta.jmeter.javadsl.core.postprocessors.DslJsr223PostProcessor.PostProcessorScript;
import us.abstracta.jmeter.javadsl.core.postprocessors.DslJsr223PostProcessor.PostProcessorVars;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssertionFirstResponseIfNumTicketsLess100 implements PostProcessorScript {
    private static final Logger log = Logger.getLogger(AssertionFirstResponseIfNumTicketsLess100.class);

    @Override
    public void runScript(PostProcessorVars s) {

        int recordsTotal = Integer.parseInt(s.vars.get("records_Total"));
        String ticket_id = s.vars.get("ticket_id");

        if (recordsTotal <= 100 && recordsTotal > 0) {
            Pattern pattern = Pattern.compile("\\w+?-" + ticket_id);
            String responseBody = s.prev.getResponseDataAsString();
            Matcher matcher = pattern.matcher(responseBody);

            if (matcher.find()) {
                String result = matcher.group(0);
                int index = result.indexOf("-") + 1;
                String subStr = result.substring(index);
                log.info("тикет с id = " + ticket_id + " найден, полный идентификатор тикета -- " + result);
                assert subStr.equals(ticket_id);
            }
            else log.error("Ошибка, совпадение не найдено, проверьте корректность предыдущих операций");
        }
        else log.warn("Совпадение в ответе на первый запрос не найдено. смотреть ответ в следующем блоке IF");
    }
}
