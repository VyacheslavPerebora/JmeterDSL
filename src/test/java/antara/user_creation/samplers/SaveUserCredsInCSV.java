package antara.user_creation.samplers;

import us.abstracta.jmeter.javadsl.java.DslJsr223Sampler;

import java.io.FileWriter;
import java.io.IOException;

public class SaveUserCredsInCSV implements DslJsr223Sampler.SamplerScript {

    @Override
    public void runScript(DslJsr223Sampler.SamplerVars vars) throws Exception {

        if (vars.prev.isSuccessful()) {
            String username = vars.vars.get("username");
            String password = vars.vars.get("password");
            //use the context manager
            try (FileWriter fWriter = new FileWriter("src/test/resources/users.csv")) {
                String line_1 = username + "," + password + "\n";
                fWriter.write(line_1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
