package antara.ticket_creation.fragments;

import antara.avtorization_admin.postprocessors.LoginCheck;
import antara.common.interfaces.SimpleController;
import antara.ticket_creation.processors.postprocessors.AssertionFirstResponseIfNumTicketsLess100;
import antara.ticket_creation.processors.postprocessors.GetOwnerUserNumberParameter;
import antara.ticket_creation.processors.preprocessors.GetRandomEmail;
import antara.ticket_creation.processors.preprocessors.GetUserCredsFromCSV;
import antara.ticket_creation.processors.preprocessors.StartParameterCalculation;
import antara.user_creation.processors.preprocessors.CreateUser;
import antara.user_creation.samplers.SaveUserCredsInCSV;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import org.apache.log4j.Logger;
import us.abstracta.jmeter.javadsl.core.assertions.DslResponseAssertion;
import us.abstracta.jmeter.javadsl.core.controllers.DslSimpleController;
import us.abstracta.jmeter.javadsl.core.samplers.BaseSampler;

import static us.abstracta.jmeter.javadsl.JmeterDsl.*;


public class TicketCreationFragment implements SimpleController {
    private static final Logger log = Logger.getLogger(TicketCreationFragment.class);

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
                        ),
                httpSampler("<_/tickets/submit/", "/tickets/submit/")
                        .method(HTTPConstants.GET)
                        .children(
                                regexExtractor("csrfmiddlewaretoken", "csrfmiddlewaretoken.+?value=\"(.+?)\">")
                                        .defaultValue("ERR_csrfmiddlewaretoken"),
                                jsr223PostProcessor(GetOwnerUserNumberParameter.class)
                        ),
                httpSampler(">_/tickets/submit/", "/tickets/submit/")
                        .method(HTTPConstants.POST)
                        .rawParam("csrfmiddlewaretoken", "${csrfmiddlewaretoken}")
                        .rawParam("queue", "${__Random(1,2,)}")
                        .rawParam("title", "${__RandomString(11,qwertyuiopasdfghjklzxcvbnmQWERTASDFGZXCVB,)}")
                        .rawParam("body", "${__RandomString(11,qwertyuiopasdfghjklzxcvbnmQWERTASDFGZXCVB,)}")
                        .rawParam("priority", "${__Random(1,5,)}")
                        .rawParam("due_date", "${__RandomDate(,2020-08-15,2024-05-12,,)}")
                        .rawParam("submitter_email", "${random_email}")
                        .rawParam("assigned_to", "${owner_user_number}")
                        .children(
                                jsr223PreProcessor(GetRandomEmail.class),
                                regexExtractor("ticket_id", "tickets/(\\d+?)/")
                                        .defaultValue("ERR_ticket_id"),
                                responseAssertion().containsRegexes("Assigned.*To.+\\W+.+>${username}\\W*<")
                        ),
                httpSampler("<_/tickets/{ticket_id}/", "/tickets/${ticket_id}/")
                        .method(HTTPConstants.GET)
                        .children(
                                responseAssertion().equalsToStrings("200").fieldToTest(DslResponseAssertion.TargetField.RESPONSE_CODE)
                        ),
                httpSampler("<_/tickets/", "/tickets/")
                        .method(HTTPConstants.GET)
                        .children(
                                regexExtractor("query_encoded", "name='query_encoded.+?value=\\'(.+?)\\'/>")
                                        .defaultValue("ERR_query_encoded")
                                ),
                httpSampler("<_/datatables_ticket_list/{some_token}", "/datatables_ticket_list/${query_encoded}")
                        .method(HTTPConstants.GET)
                        .rawParam("length", "100")
                        .children(
                                regexExtractor("records_Total", "recordsTotal.+?(\\d+),")
                                        .defaultValue("ERR_records_Total"),
                                jsr223PostProcessor(AssertionFirstResponseIfNumTicketsLess100.class)
                        ),
                ifController("${__jexl3(${records_Total} > 100)}",
                        httpSampler("<_/datatables_ticket_list/{some_token}", "/datatables_ticket_list/${query_encoded}")
                                .method(HTTPConstants.GET)
                                .rawParam("start", "${start}")
                                .rawParam("length", "100")
                                .children(
                                        jsr223PreProcessor(StartParameterCalculation.class),
                                        responseAssertion().containsRegexes("\\w+?-${ticket_id}")
                                )
                )

        );


    }

}


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