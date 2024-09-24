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
import org.apache.http.entity.ContentType;
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
                httpSampler("<_/tickets/submit/", "/tickets/submit/")
                        .method(HTTPConstants.GET)
                        .children(
                                regexExtractor("csrfmiddlewaretoken", "csrfmiddlewaretoken.+?value=\"(.+?)\">")
                                        .defaultValue("ERR_csrfmiddlewaretoken"),
                                jsr223PostProcessor(GetOwnerUserNumberParameter.class)
                        ),
                httpSampler(">_/tickets/submit/", "/tickets/submit/")
                        .method(HTTPConstants.POST)
                        .rawParam("csrfmiddlewaretoken", "${__urlencode(${csrfmiddlewaretoken})}")
                        .rawParam("queue", "${__urlencode(${__Random(1,2,)})}")
                        .rawParam("title", "${__urlencode(${__RandomString(11,qwertyuiopasdfghjklzxcvbnmQWERTASDFGZXCVB,)})}")
                        .rawParam("body", "${__urlencode(${__RandomString(11,qwertyuiopasdfghjklzxcvbnmQWERTASDFGZXCVB,)})}")
                        .rawParam("priority", "${__urlencode(${__Random(1,5,)})}")
                        .rawParam("due_date", "${__urlencode(${__RandomDate(,2020-08-15,2024-05-12,,)})}")
                        .rawParam("submitter_email", "${__urlencode(${random_email})}")
                        .rawParam("assigned_to", "${__urlencode(${owner_user_number})}")
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
                        .rawParam("length", "${__urlencode(100)}")
                        .children(
                                regexExtractor("records_Total", "recordsTotal.+?(\\d+),")
                                        .defaultValue("ERR_records_Total"),
                                jsr223PostProcessor(AssertionFirstResponseIfNumTicketsLess100.class)
                        ),
                ifController("${__jexl3(${records_Total} > 100)}",
                        httpSampler("<_/datatables_ticket_list/{some_token}", "/datatables_ticket_list/${query_encoded}")
                                .method(HTTPConstants.GET)
                                .rawParam("start", "${__urlencode(${start})}")
                                .rawParam("length", "${__urlencode(100)}")
                                .children(
                                        jsr223PreProcessor(StartParameterCalculation.class),
                                        responseAssertion().containsRegexes("\\w+?-${ticket_id}")
                                )
                )
        );
    }
}