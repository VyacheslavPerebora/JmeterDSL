package antara.pagination.fragments;

import antara.avtorization_admin.postprocessors.LoginCheck;
import antara.common.interfaces.SimpleController;
import antara.pagination.processors.postprocessors.RandomStartParameterCalculation;
import antara.ticket_creation.processors.postprocessors.AssertionFirstResponseIfNumTicketsLess100;
import antara.ticket_creation.processors.postprocessors.GetOwnerUserNumberParameter;
import antara.ticket_creation.processors.preprocessors.GetRandomEmail;
import antara.ticket_creation.processors.preprocessors.GetUserCredsFromCSV;
import antara.ticket_creation.processors.preprocessors.StartParameterCalculation;
import org.apache.http.entity.ContentType;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import org.apache.log4j.Logger;
import us.abstracta.jmeter.javadsl.core.assertions.DslResponseAssertion;
import us.abstracta.jmeter.javadsl.core.controllers.DslSimpleController;

import static us.abstracta.jmeter.javadsl.JmeterDsl.*;


public class PaginationFragment implements SimpleController {
    private static final Logger log = Logger.getLogger(PaginationFragment.class);

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
                httpSampler("<_/tickets/", "/tickets/")
                        .method(HTTPConstants.GET)
                        .children(
                                regexExtractor("query_encoded", "name='query_encoded.+?value=\\'(.+?)\\'/>")
                                        .defaultValue("ERR_query_encoded")
                                ),
                httpSampler("<_/datatables_ticket_list/{some_token}", "/datatables_ticket_list/${query_encoded}")
                        .method(HTTPConstants.GET)
                        .rawParam("length", "10")
                        .children(
                                regexExtractor("records_Total", "recordsTotal.+?(\\d+),")
                                        .defaultValue("ERR_records_Total"),
                                regexExtractor("check_updated_list_tickets", "data.*?ticket.*?(\\[.+?\\])")
                                        .defaultValue("ERR_check_updated_list_tickets"),
                                jsr223PostProcessor(RandomStartParameterCalculation.class)
                        ),
                ifController("${__jexl3(${records_Total} > 10)}",
                        httpSampler("<_/datatables_ticket_list/{some_token}", "/datatables_ticket_list/${query_encoded}")
                                .method(HTTPConstants.GET)
                                .rawParam("start", "${start}")
                                .rawParam("length", "10")
                                .children(
                                        responseAssertion().containsSubstrings("${check_updated_list_tickets}").invertCheck(true)
                                )
                )

        );


    }

}
