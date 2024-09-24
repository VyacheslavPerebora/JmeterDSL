package antara.pagination.processors.postprocessors;

import org.apache.log4j.Logger;
import us.abstracta.jmeter.javadsl.core.postprocessors.DslJsr223PostProcessor.PostProcessorScript;
import us.abstracta.jmeter.javadsl.core.postprocessors.DslJsr223PostProcessor.PostProcessorVars;

import java.util.Random;

public class RandomStartParameterCalculation implements PostProcessorScript {
    private static final Logger log = Logger.getLogger(RandomStartParameterCalculation.class);

    @Override
    public void runScript(PostProcessorVars s) {

        //рассчитываем параметр start для перехода на случайную страницу из списка тикетов
        Random random = new Random();
        int recordsTotal = Integer.parseInt(s.vars.get("records_Total"));
        int length = 10;
        int intPartDiv = Math.floorDiv(recordsTotal, length);

        log.warn("ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ");
        String c = s.vars.get("check_updated_list_tickets");
        log.warn(c);

        if (recordsTotal > 10) {
            //random.nextInt(b) создаст псевдослучайное число от 0 включительно, до b исключительно
            if ((recordsTotal - length * intPartDiv) > 0) {
                int r = random.nextInt(intPartDiv) + 1;
                int start_1 = r * 10;
                s.vars.put("start", String.valueOf(start_1));
            } else {
                int intPartDiv_ = intPartDiv - 1;
                int r = random.nextInt(intPartDiv_) + 1;
                int start_2 = r * 10;
                s.vars.put("start", String.valueOf(start_2));
            }
        } else log.warn("Количество записей 'records_Total' должно быть больше 10 иначе тест не выполнится корректно!");
    }
}
