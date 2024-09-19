package antara.ticket_creation.processors.preprocessors;

import us.abstracta.jmeter.javadsl.core.preprocessors.DslJsr223PreProcessor.PreProcessorScript;
import us.abstracta.jmeter.javadsl.core.preprocessors.DslJsr223PreProcessor.PreProcessorVars;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class GetUserCredsFromCSV implements PreProcessorScript {
    @Override
    public void runScript(PreProcessorVars s) {

        try (BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/users.csv"))) {
            String line;

            if ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                String username_value = values[0].trim();
                String password_value = values[1].trim();

                s.vars.put("username", username_value);
                s.vars.put("password", password_value);
            }
            else System.out.println("Файл пуст.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}