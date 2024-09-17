package antara.admin_login.fragments;

import org.apache.jmeter.protocol.http.util.HTTPConstants;
import antara.admin_login.postprocessors.LoginCheck;
import antara.common.interfaces.SimpleController;
import us.abstracta.jmeter.javadsl.core.controllers.DslSimpleController;

import java.util.Objects;

import static antara.common.helpers.ActionHelper.jsr223Action;
import static us.abstracta.jmeter.javadsl.JmeterDsl.*;
import static us.abstracta.jmeter.javadsl.JmeterDsl.httpSampler;


public class AdminLoginFragment implements SimpleController {

    public DslSimpleController get() {
        return simpleController(
                httpSampler("<_/", "/")
                        .method(HTTPConstants.GET)
                        .children(
                                regexExtractor("csrf_token", "csrfmiddlewaretoken\".*value=\"(.*)\">")
                                        .defaultValue("csrf_ERR")
                        ),
                httpSampler(">_/login/", "/login/")
                        .method(HTTPConstants.POST)
                        .rawParam("username", "${__P(ADMIN_LOGIN)}")
                        .rawParam("password", "${__P(ADMIN_PASS)}")
                        .rawParam("csrfmiddlewaretoken", "${csrf_token}")
                        .rawParam("next", "/")
                        .children(
                                regexExtractor("login_check", "(Logout)")
                                        .defaultValue("login_check_error"),
                                jsr223PostProcessor(LoginCheck.class)

                        )
        );
    }
}