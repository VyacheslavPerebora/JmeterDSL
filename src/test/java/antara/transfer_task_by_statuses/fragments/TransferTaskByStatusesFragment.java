package antara.transfer_task_by_statuses.fragments;

import antara.avtorization_admin.postprocessors.LoginCheck;
import antara.common.interfaces.SimpleController;
import antara.pagination.processors.postprocessors.RandomStartParameterCalculation;
import antara.ticket_creation.processors.preprocessors.GetUserCredsFromCSV;
import antara.transfer_task_by_statuses.processors.postprocessors.AssertionResponseTransferTask;
import org.apache.http.entity.ContentType;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import org.apache.log4j.Logger;
import us.abstracta.jmeter.javadsl.core.assertions.DslResponseAssertion.TargetField;
import us.abstracta.jmeter.javadsl.core.controllers.DslSimpleController;

import static us.abstracta.jmeter.javadsl.JmeterDsl.*;


public class TransferTaskByStatusesFragment implements SimpleController {
    private static final Logger log = Logger.getLogger(TransferTaskByStatusesFragment.class);

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
                httpSampler("<_/tickets/", "/tickets/")
                        .method(HTTPConstants.GET)
                        .children(
                                regexExtractor("query_encoded", "name='query_encoded.+?value=\\'(.+?)\\'/>")
                                        .defaultValue("ERR_query_encoded")
                                ),
                httpSampler("<_/datatables_ticket_list/{some_token}", "/datatables_ticket_list/${query_encoded}")
                        .method(HTTPConstants.GET)
                        .rawParam("length", "${__urlencode(10)}")
                        .children(
                                regexExtractor("random_ticket_id", "ticket.+?(\\d+).*?\\[")
                                        .defaultValue("ERR_random_ticket_id")
                        ),
                httpSampler("<_/tickets/{random_ticket_id}/", "/tickets/${random_ticket_id}/")
                        .method(HTTPConstants.GET)
                        .children(
                                regexExtractor("csrfmiddlewaretoken", "csrfmiddlewaretoken.+?value=\"(.+?)\">")
                                        .defaultValue("ERR_csrfmiddlewaretoken"),
                                regexExtractor("new_status", "radio.inline.+value=[\\\"\\'](\\d+)[\\\"\\'].....st...?.?.?.?.?.?.?.?.?>")
                                        .defaultValue("ERR_new_status"),
                                responseAssertion().containsSubstrings("200").fieldToTest(TargetField.RESPONSE_CODE)
                        ),
                httpSampler(">_/tickets/{random_ticket_id}/update/", "/tickets/${random_ticket_id}/update/")
                        .method(HTTPConstants.POST)
                        .rawParam("new_status", "${__urlencode(${new_status})}")
                        .rawParam("csrfmiddlewaretoken", "${__urlencode(${csrfmiddlewaretoken})}")
                        .children(
                                jsr223PostProcessor(AssertionResponseTransferTask.class)
                        )
        );
    }
}
