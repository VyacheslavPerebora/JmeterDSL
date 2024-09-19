package antara.user_creation.processors.preprocessors;

import us.abstracta.jmeter.javadsl.core.preprocessors.DslJsr223PreProcessor.PreProcessorVars;
import us.abstracta.jmeter.javadsl.core.preprocessors.DslJsr223PreProcessor.PreProcessorScript;

import java.util.Random;

public class CreateUser implements PreProcessorScript {
    @Override
    public void runScript(PreProcessorVars s) {

        String username_value = generateRandomString(12);
        String password_value = generateRandomString(12);

        s.vars.put("username", username_value);
        s.vars.put("password", password_value);
    }
    public static String generateRandomString(int length) {

        char[] pool = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        Random random = new Random();
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(pool[random.nextInt(pool.length)]);
        }
        return builder.toString();
    }
}