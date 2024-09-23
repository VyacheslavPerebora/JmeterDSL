package antara.ticket_creation.processors.preprocessors;

import org.apache.log4j.Logger;
import us.abstracta.jmeter.javadsl.core.preprocessors.DslJsr223PreProcessor.PreProcessorScript;
import us.abstracta.jmeter.javadsl.core.preprocessors.DslJsr223PreProcessor.PreProcessorVars;

import java.util.Random;

public class StartParameterCalculation implements PreProcessorScript {
    private static final Logger log = Logger.getLogger(StartParameterCalculation.class);

    @Override
    public void runScript(PreProcessorVars s) {

        int recordsTotal = Integer.parseInt(s.vars.get("records_Total"));
        int length = 100;
        int intPartDiv = Math.floorDiv(recordsTotal, length);
        int start_1 = intPartDiv * 100;

        //default value of "start" parameter is 0
        if ((recordsTotal - start_1) > 0) {
            s.vars.put("start", String.valueOf(start_1));}
        else {int start_2 = start_1 - 100; s.vars.put("start", String.valueOf(start_2));}
    }
}
