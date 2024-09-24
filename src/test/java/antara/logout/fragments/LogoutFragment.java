package antara.logout.fragments;

import antara.avtorization_admin.postprocessors.LoginCheck;
import antara.common.interfaces.SimpleController;
import antara.ticket_creation.processors.preprocessors.GetUserCredsFromCSV;
import antara.transfer_task_by_statuses.processors.postprocessors.AssertionResponseTransferTask;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import org.apache.log4j.Logger;
import us.abstracta.jmeter.javadsl.core.assertions.DslResponseAssertion.TargetField;
import us.abstracta.jmeter.javadsl.core.controllers.DslSimpleController;

import static us.abstracta.jmeter.javadsl.JmeterDsl.*;


public class LogoutFragment implements SimpleController {
    private static final Logger log = Logger.getLogger(LogoutFragment.class);

    public DslSimpleController get() {
        return simpleController(
                httpSampler("<_/", "/")
                        .method(HTTPConstants.GET)
                        .children(
                                regexExtractor("csrfmiddlewaretoken", "csrfmiddlewaretoken.+?value=\"(.+?)\">")
                                        .defaultValue("ERR_csrfmiddlewaretoken")
                        ),
                httpSampler(">_/login/", "/login/")
                        .method(HTTPConstants.POST)
                        .rawParam("username", "${__urlencode(${username})}")
                        .rawParam("password", "${__urlencode(${password})}")
                        .rawParam("next", "/")
                        .rawParam("csrfmiddlewaretoken", "${__urlencode(${csrfmiddlewaretoken})}")
                        .children(
                                jsr223PreProcessor(GetUserCredsFromCSV.class),
                                regexExtractor("login_check", "(Logout)")
                                        .defaultValue("ERR_login_check"),
                                jsr223PostProcessor(LoginCheck.class),
                                responseAssertion().containsRegexes("${username}")
                        ),
                httpSampler("<_/logout/", "/logout/")
                        .method(HTTPConstants.GET)
                        .children(
                                responseAssertion().containsRegexes("Log.In")
                                )
        );
    }
}
