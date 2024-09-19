package antara.ticket_creation.fragments;

import antara.avtorization_admin.postprocessors.LoginCheck;
import antara.common.interfaces.SimpleController;
import antara.ticket_creation.processors.preprocessors.GetUserCredsFromCSV;
import antara.user_creation.processors.preprocessors.CreateUser;
import antara.user_creation.samplers.SaveUserCredsInCSV;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import us.abstracta.jmeter.javadsl.core.controllers.DslSimpleController;

import static us.abstracta.jmeter.javadsl.JmeterDsl.*;


public class TicketCreationFragment implements SimpleController {

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
                        .rawParam("username", "${username}")
                        .rawParam("password", "${password}")
                        .rawParam("next", "/")
                        .rawParam("csrfmiddlewaretoken", "${csrfmiddlewaretoken}")
                        .children(
                                jsr223PreProcessor(GetUserCredsFromCSV.class),
                                regexExtractor("login_check", "(Logout)")
                                        .defaultValue("ERR_login_check"),
                                jsr223PostProcessor(LoginCheck.class),
                                responseAssertion().containsRegexes("${username}")
                        )
        );





    }
}


//
//httpSampler(">_/admin/auth/user/add/", "/admin/auth/user/add/")
//                        .method(HTTPConstants.POST)
//                        .rawParam("csrfmiddlewaretoken", "${csrfmiddlewaretoken}")
//                        .rawParam("username", "${username}")
//                        .rawParam("password1", "${password}")
//                        .rawParam("password2", "${password}")
//                        .rawParam("_save", "Save")
//                        .children(
//        jsr223PreProcessor(CreateUser.class),
//regexExtractor("user_id", "/admin/auth/user/(\\d+)/")
//                                        .defaultValue("ERR_user_id"),
//responseAssertion().containsRegexes("The user.+?${username}.+?was added")
//                        ),
//httpSampler(">_/admin/auth/user/{user_id}/change/", "/admin/auth/user/${user_id}/change/")
//                        .method(HTTPConstants.POST)
//                        .rawParam("csrfmiddlewaretoken", "${csrfmiddlewaretoken}")
//                        .rawParam("username", "${username}")
//                        .rawParam("is_active", "on")
//                        .rawParam("is_staff", "on")
//                        .rawParam("date_joined_0", "2024-09-11")
//                        .rawParam("date_joined_1", "19:02:17")
//                        .rawParam("_save", "Save")
//                        .children(
//        responseAssertion().containsRegexes("The user.+?>${username}<.+?was changed successfully")
//                        ),
//httpSampler("<_/admin/auth/user/", "/admin/auth/user/")
//                        .method(HTTPConstants.GET)
//                        .rawParam("q", "${username}")
//                        .children(
//        responseAssertion().containsRegexes("${username}.+?field-is_staff.+?True")
//                        ),
//jsr223Sampler(SaveUserCredsInCSV.class)
//        );