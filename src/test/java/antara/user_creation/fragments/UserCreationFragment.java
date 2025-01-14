package antara.user_creation.fragments;

import antara.common.interfaces.SimpleController;
import antara.user_creation.samplers.SaveUserCredsInCSV;
import antara.user_creation.processors.preprocessors.CreateUser;
import org.apache.http.entity.ContentType;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import us.abstracta.jmeter.javadsl.core.controllers.DslSimpleController;

import static antara.common.helpers.ActionHelper.jsr223Action;
import static us.abstracta.jmeter.javadsl.JmeterDsl.*;


public class UserCreationFragment implements SimpleController {

    public DslSimpleController get() {
        return simpleController(
                httpSampler("<_/admin/auth/user/add/", "/admin/auth/user/add/")
                        .method(HTTPConstants.GET)
                        .children(
                                regexExtractor("csrfmiddlewaretoken", "csrfmiddlewaretoken.+?value=\"(.+?)\">")
                                        .defaultValue("ERR_csrfmiddlewaretoken")
                        ),
                httpSampler(">_/admin/auth/user/add/", "/admin/auth/user/add/")
                        .method(HTTPConstants.POST)
                        .rawParam("csrfmiddlewaretoken", "${__urlencode(${csrfmiddlewaretoken})}")
                        .rawParam("username", "${__urlencode(${username})}")
                        .rawParam("password1", "${__urlencode(${password})}")
                        .rawParam("password2", "${__urlencode(${password})}")
                        .rawParam("_save", "${__urlencode(Save)}")
                        .children(
                                jsr223PreProcessor(CreateUser.class),
                                regexExtractor("user_id", "/admin/auth/user/(\\d+)/")
                                        .defaultValue("ERR_user_id"),
                                responseAssertion().containsRegexes("The user.+?${username}.+?was added")
                        ),
                httpSampler(">_/admin/auth/user/{user_id}/change/", "/admin/auth/user/${user_id}/change/")
                        .method(HTTPConstants.POST)
                        .rawParam("csrfmiddlewaretoken", "${__urlencode(${csrfmiddlewaretoken})}")
                        .rawParam("username", "${__urlencode(${username})}")
                        .rawParam("is_active", "${__urlencode(on)}")
                        .rawParam("is_staff", "${__urlencode(on)}")
                        .rawParam("date_joined_0", "${__urlencode(2024-09-11)}")
                        .rawParam("date_joined_1", "${__urlencode(19:02:17)}")
                        .rawParam("_save", "${__urlencode(Save)}")
                        .children(
                                responseAssertion().containsRegexes("The user.+?>${username}<.+?was changed successfully")
                        ),
                httpSampler("<_/admin/auth/user/", "/admin/auth/user/")
                        .method(HTTPConstants.GET)
                        .rawParam("q", "${__urlencode(${username})}")
                        .children(
                                responseAssertion().containsRegexes("${username}.+?field-is_staff.+?True")
                        ),
                jsr223Sampler(SaveUserCredsInCSV.class)
        );
    }
}