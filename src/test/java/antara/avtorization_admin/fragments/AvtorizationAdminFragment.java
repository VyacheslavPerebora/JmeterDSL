package antara.avtorization_admin.fragments;

import org.apache.http.entity.ContentType;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import antara.avtorization_admin.postprocessors.LoginCheck;
import antara.common.interfaces.SimpleController;
import us.abstracta.jmeter.javadsl.core.assertions.DslResponseAssertion;
import us.abstracta.jmeter.javadsl.core.controllers.DslSimpleController;

import static antara.common.helpers.ActionHelper.jsr223Action;
import static us.abstracta.jmeter.javadsl.JmeterDsl.*;
import static us.abstracta.jmeter.javadsl.JmeterDsl.httpSampler;


public class AvtorizationAdminFragment implements SimpleController {

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
                        .rawParam("username", "${__urlencode(${__P(ADMIN_LOGIN)})}")
                        .rawParam("password", "${__urlencode(${__P(ADMIN_PASS)})}")
                        .rawParam("next", "/")
                        .rawParam("csrfmiddlewaretoken", "${__urlencode(${csrfmiddlewaretoken})}")
                        .children(
                                regexExtractor("login_check", "(Logout)")
                                        .defaultValue("ERR_login_check"),
                                jsr223PostProcessor(LoginCheck.class)
                        )
        );
    }
}