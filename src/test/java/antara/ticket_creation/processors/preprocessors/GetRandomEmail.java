package antara.ticket_creation.processors.preprocessors;

import antara.ticket_creation.processors.postprocessors.GetOwnerUserNumberParameter;
import org.apache.log4j.Logger;
import us.abstracta.jmeter.javadsl.core.preprocessors.DslJsr223PreProcessor.PreProcessorScript;
import us.abstracta.jmeter.javadsl.core.preprocessors.DslJsr223PreProcessor.PreProcessorVars;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class GetRandomEmail implements PreProcessorScript {
    private static final Logger log = Logger.getLogger(GetRandomEmail.class);

    @Override
    public void runScript(PreProcessorVars s) {

        String random_email =  generateRandomString(10) + "@" + generateRandomString(8) + ".ru";
        s.vars.put("random_email", random_email);
        log.info(random_email);

    }
    public static String generateRandomString(int length) {

        char[] pool = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        Random random = new Random();
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(pool[random.nextInt(pool.length)]);
        }
        return builder.toString();
    }
}