package antara.admin_login.postprocessors;

import us.abstracta.jmeter.javadsl.core.postprocessors.DslJsr223PostProcessor.PostProcessorScript;
import us.abstracta.jmeter.javadsl.core.postprocessors.DslJsr223PostProcessor.PostProcessorVars;

import java.util.Objects;

public class LoginCheck implements PostProcessorScript {
    @Override
    public void runScript(PostProcessorVars s) {
        String loginCheck = s.vars.get("login_check");
        if (!Objects.equals(loginCheck, "Logout") || Objects.equals(loginCheck, "login_check_error")) {
            s.prev.setSuccessful(false);
            s.prev.setSampleLabel(">_/login/_FAILED");
        }
    }
}