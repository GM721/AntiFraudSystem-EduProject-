import antifraud.AntiFraudApplication;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.hyperskill.hstest.dynamic.DynamicTest;
import org.hyperskill.hstest.dynamic.input.DynamicTesting;
import org.hyperskill.hstest.exception.outcomes.UnexpectedError;
import org.hyperskill.hstest.exception.outcomes.WrongAnswer;
import org.hyperskill.hstest.mocks.web.request.HttpRequest;
import org.hyperskill.hstest.mocks.web.response.HttpResponse;
import org.hyperskill.hstest.stage.SpringTest;
import org.hyperskill.hstest.testcase.CheckResult;

import java.util.ArrayList;
import java.util.List;

import static org.hyperskill.hstest.common.JsonUtils.getJson;
import static org.hyperskill.hstest.testing.expect.Expectation.expect;
import static org.hyperskill.hstest.testing.expect.json.JsonChecker.*;

class TestHint {
    private final String apiPath;
    private final String requestBody;
    private final String message;

    public TestHint(String apiPath, String requestBody, String message) {
        this.apiPath = apiPath;
        this.requestBody = requestBody;
        this.message = message;
    }

    @Override
    public String toString() {
        return "Test case\n" +
                "Testing api: '" + apiPath + '\'' + "\n" +
                (requestBody.length() > 0 ? "request: '" + requestBody + '\'' + "\n" : "") +
                "Expectations: '" + message + "'" + "\n" +
                "-----";
    }
}

public class AntiFraudTest extends SpringTest {

    private final String transactionApi = "/api/antifraud/transaction";
    private final String userApi = "/api/auth/user";
    private final String userListApi = "/api/auth/list";
    private final String lockApi = "/api/auth/access";
    private final String roleApi = "/api/auth/role";
    private final String ipApi = "/api/antifraud/suspicious-ip";
    private final String cardApi = "/api/antifraud/stolencard";
    private final String historyApi = "/api/antifraud/history";

    List<Integer> userIdList = new ArrayList<>();

    private final String administrator = "{\n" +
            "   \"name\": \"administrator\",\n" +
            "   \"username\": \"administrator\",\n" +
            "   \"password\": \"oMoa4VvqnLxW\"\n" +
            "}";

    private final String johndoe1 = "{\n" +
            "   \"name\": \"John Doe 1\",\n" +
            "   \"username\": \"johndoe1\",\n" +
            "   \"password\": \"oMoa3VvqnLxW\"\n" +
            "}";

    private final String johndoe2 = "{\n" +
            "   \"name\": \"John Doe 2\",\n" +
            "   \"username\": \"johndoe2\",\n" +
            "   \"password\": \"oMoa5VvqnLxW\"\n" +
            "}";

    private final String johndoe3 = "{\n" +
            "   \"name\": \"John Doe 3\",\n" +
            "   \"username\": \"johndoe3\",\n" +
            "   \"password\": \"oMoa6VvqnLxW\"\n" +
            "}";

    private final String wronguser1 = "{\n" +
            "   \"name\": \"John Doe 1\",\n" +
            "   \"password\": \"oa3VvqnLxW\"\n" +
            "}";

    private final String wronguser2 = "{\n" +
            "   \"name\": \"John Doe 2\",\n" +
            "   \"username\": \"johndoe1\"\n" +
            "}";

    private final String wronguserCred1 = "{\n" +
            "   \"name\": \"John Doe 1\",\n" +
            "   \"username\": \"johndoe1\",\n" +
            "   \"password\": \"oa3VvqnLxW\"\n" +
            "}";

    private final String wronguserCred2 = "{\n" +
            "   \"name\": \"John Do\",\n" +
            "   \"username\": \"johndo\",\n" +
            "   \"password\": \"oMoa3VvqnLxW\"\n" +
            "}";

    private final String listAnswer1 = "[ {\n" +
            "  \"id\" : 1,\n" +
            "  \"name\" : \"administrator\",\n" +
            "  \"username\" : \"administrator\",\n" +
            "  \"role\" : \"ADMINISTRATOR\"\n" +
            "}, {\n" +
            "  \"id\" : 2,\n" +
            "  \"name\" : \"John Doe 1\",\n" +
            "  \"username\" : \"johndoe1\",\n" +
            "  \"role\" : \"MERCHANT\"\n" +
            "}, {\n" +
            "  \"id\" : 4,\n" +
            "  \"name\" : \"John Doe 2\",\n" +
            "  \"username\" : \"johndoe2\",\n" +
            "  \"role\" : \"MERCHANT\"\n" +
            "} ]";

    private final String listAnswer2 = "[ {\n" +
            "  \"id\" : 1,\n" +
            "  \"name\" : \"administrator\",\n" +
            "  \"username\" : \"administrator\",\n" +
            "  \"role\" : \"ADMINISTRATOR\"\n" +
            "}, {\n" +
            "  \"id\" : 4,\n" +
            "  \"name\" : \"John Doe 2\",\n" +
            "  \"username\" : \"johndoe2\",\n" +
            "  \"role\" : \"MERCHANT\"\n" +
            "} ]";

    private final String listAnswer3 = "[ {\n" +
            "  \"id\" : 1,\n" +
            "  \"name\" : \"administrator\",\n" +
            "  \"username\" : \"administrator\",\n" +
            "  \"role\" : \"ADMINISTRATOR\"\n" +
            "}, {\n" +
            "  \"id\" : 4,\n" +
            "  \"name\" : \"John Doe 2\",\n" +
            "  \"username\" : \"johndoe2\",\n" +
            "  \"role\" : \"SUPPORT\"\n" +
            "} ]";

    private final String tr1 = "{\n" +
            "  \"amount\": 1,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T16:00:00\"\n" +
            "}";

    private final String tr2 = "{\n" +
            "  \"amount\": 199,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T16:01:00\"\n" +
            "}";

    private final String tr3 = "{\n" +
            "  \"amount\": 200,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T16:02:00\"\n" +
            "}";

    private final String tr4 = "{\n" +
            "  \"amount\": 201,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T16:03:00\"\n" +
            "}";

    private final String tr5 = "{\n" +
            "  \"amount\": 1499,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T16:04:00\"\n" +
            "}";

    private final String tr6 = "{\n" +
            "  \"amount\": 1500,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T16:05:00\"\n" +
            "}";

    private final String tr7 = "{\n" +
            "  \"amount\": 1501,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T16:06:00\"\n" +
            "}";

    private final String tr8 = "{\n" +
            "  \"amount\": 2000,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T16:07:00\"\n" +
            "}";

    private final String tr9 = "{\n" +
            "  \"amount\": 210,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-23T16:03:00\"\n" +
            "}";

    private final String tr10 = "{\n" +
            "  \"amount\": 202,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-23T16:04:00\"\n" +
            "}";

    private final String tr11 = "{\n" +
            "  \"amount\": 203,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-23T19:04:00\"\n" +
            "}";

    private final String tr12 = "{\n" +
            "  \"amount\": 1161,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-23T19:04:00\"\n" +
            "}";

    private final String tr13 = "{\n" +
            "  \"amount\": 163,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-23T19:08:00\"\n" +
            "}";

    private final String tr14 = "{\n" +
            "  \"amount\": 131,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-23T19:18:00\"\n" +
            "}";

    private final String tr15 = "{\n" +
            "  \"amount\": 929,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-23T19:28:00\"\n" +
            "}";

    private final String tr16 = "{\n" +
            "  \"amount\": 289,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-23T20:28:00\"\n" +
            "}";

    private final String tr17 = "{\n" +
            "  \"amount\": 930,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-23T20:28:00\"\n" +
            "}";

    private final String tr18 = "{\n" +
            "  \"amount\": 931,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-23T20:28:00\"\n" +
            "}";

    private final String trW1 = "{\n" +
            "  \"amount\": -1,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T16:00:00\"\n" +
            "}";

    private final String trW2 = "{\n" +
            "  \"amount\": 0,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T16:00:00\"\n" +
            "}";

    private final String trW3 = "{\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T16:00:00\"\n" +
            "}";

    private final String trW4 = "{\n" +
            "  \"amount\": \" \",\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T16:00:00\"\n" +
            "}";

    private final String trW5 = "{\n" +
            "  \"amount\": \"\",\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T16:00:00\"\n" +
            "}";

    private final String trW6 = "{ }";

    private final String trP1 = "{\n" +
            "  \"amount\": 1000,\n" +
            "  \"ip\": \"192.168.1.67\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T16:00:00\"\n" +
            "}";

    private final String trP2 = "{\n" +
            "  \"amount\": 1000,\n" +
            "  \"ip\": \"192.168.1.1\",\n" +
            "  \"number\": \"4000003305160034\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T16:00:00\"\n" +
            "}";

    private final String trP3 = "{\n" +
            "  \"amount\": 1000,\n" +
            "  \"ip\": \"192.168.1.67\",\n" +
            "  \"number\": \"4000003305160034\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T16:00:00\"\n" +
            "}";

    private final String trP4 = "{\n" +
            "  \"amount\": 2000,\n" +
            "  \"ip\": \"192.168.1.67\",\n" +
            "  \"number\": \"4000003305160034\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T16:00:00\"\n" +
            "}";

    // correlation rules
    private final String corr1IP = "{\n" +
            "  \"amount\": 1,\n" +
            "  \"ip\": \"192.168.1.2\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T17:10:00\"\n" +
            "}";

    private final String corr2IP = "{\n" +
            "  \"amount\": 1,\n" +
            "  \"ip\": \"192.168.1.3\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T17:11:00\"\n" +
            "}";

    private final String corr22IP = "{\n" +
            "  \"amount\": 1,\n" +
            "  \"ip\": \"192.168.1.3\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T17:12:00\"\n" +
            "}";

    private final String corr3IP = "{\n" +
            "  \"amount\": 1,\n" +
            "  \"ip\": \"192.168.1.4\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T17:12:00\"\n" +
            "}";

    private final String corr4IP = "{\n" +
            "  \"amount\": 1,\n" +
            "  \"ip\": \"192.168.1.5\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T17:13:00\"\n" +
            "}";

    private final String corr5IP = "{\n" +
            "  \"amount\": 2000,\n" +
            "  \"ip\": \"192.168.1.5\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-22T17:14:00\"\n" +
            "}";

    private final String corr1Reg = "{\n" +
            "  \"amount\": 1,\n" +
            "  \"ip\": \"192.168.1.2\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"EAP\",\n" +
            "  \"date\": \"2022-01-21T17:10:00\"\n" +
            "}";

    private final String corr2Reg = "{\n" +
            "  \"amount\": 1,\n" +
            "  \"ip\": \"192.168.1.2\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"ECA\",\n" +
            "  \"date\": \"2022-01-21T17:11:00\"\n" +
            "}";

    private final String corr22Reg = "{\n" +
            "  \"amount\": 1,\n" +
            "  \"ip\": \"192.168.1.2\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"ECA\",\n" +
            "  \"date\": \"2022-01-21T17:12:00\"\n" +
            "}";

    private final String corr3Reg = "{\n" +
            "  \"amount\": 1,\n" +
            "  \"ip\": \"192.168.1.2\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"HIC\",\n" +
            "  \"date\": \"2022-01-21T17:13:00\"\n" +
            "}";

    private final String corr4Reg = "{\n" +
            "  \"amount\": 1,\n" +
            "  \"ip\": \"192.168.1.2\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"SSA\",\n" +
            "  \"date\": \"2022-01-21T17:14:00\"\n" +
            "}";

    private final String corr5Reg = "{\n" +
            "  \"amount\": 2000,\n" +
            "  \"ip\": \"192.168.1.2\",\n" +
            "  \"number\": \"4000008449433403\",\n" +
            "  \"region\": \"SA\",\n" +
            "  \"date\": \"2022-01-21T17:15:00\"\n" +
            "}";





    private final String ipAnswer = "[ {\n" +
            "  \"id\" : 1,\n" +
            "  \"ip\" : \"192.168.1.66\"\n" +
            "}, {\n" +
            "  \"id\" : 2,\n" +
            "  \"ip\" : \"192.168.1.67\"\n" +
            "} ]";

    private final String ipAnswer2 = "[ {\n" +
            "  \"id\" : 2,\n" +
            "  \"ip\" : \"192.168.1.67\"\n" +
            "} ]";

    private final String ipAnswerEmpty = "[]";

    private final String cardAnswerEmpty = "[]";

    private final String cardAnswer = "[ {\n" +
            "  \"id\" : 1,\n" +
            "  \"number\" : \"4000003305061034\"\n" +
            "}, {\n" +
            "  \"id\" : 2,\n" +
            "  \"number\" : \"4000003305160034\"\n" +
            "} ]";

    private final String cardAnswer2 = "[ {\n" +
            "  \"id\" : 2,\n" +
            "  \"number\" : \"4000003305160034\"\n" +
            "} ]";

    private final String historyAnswer = "[ {\n" +
            "  \"transactionId\" : 1,\n" +
            "  \"amount\" : 1,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.1\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T16:00:00\",\n" +
            "  \"result\" : \"ALLOWED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 2,\n" +
            "  \"amount\" : 1,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.1\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T16:00:00\",\n" +
            "  \"result\" : \"ALLOWED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 3,\n" +
            "  \"amount\" : 199,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.1\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T16:01:00\",\n" +
            "  \"result\" : \"ALLOWED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 4,\n" +
            "  \"amount\" : 200,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.1\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T16:02:00\",\n" +
            "  \"result\" : \"ALLOWED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 5,\n" +
            "  \"amount\" : 201,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.1\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T16:03:00\",\n" +
            "  \"result\" : \"MANUAL_PROCESSING\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 6,\n" +
            "  \"amount\" : 1499,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.1\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T16:04:00\",\n" +
            "  \"result\" : \"MANUAL_PROCESSING\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 7,\n" +
            "  \"amount\" : 1500,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.1\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T16:05:00\",\n" +
            "  \"result\" : \"MANUAL_PROCESSING\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 8,\n" +
            "  \"amount\" : 1501,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.1\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T16:06:00\",\n" +
            "  \"result\" : \"PROHIBITED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 9,\n" +
            "  \"amount\" : 2000,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.1\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T16:07:00\",\n" +
            "  \"result\" : \"PROHIBITED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 10,\n" +
            "  \"amount\" : 1,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.1\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T16:00:00\",\n" +
            "  \"result\" : \"ALLOWED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 11,\n" +
            "  \"amount\" : 1000,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.67\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T16:00:00\",\n" +
            "  \"result\" : \"PROHIBITED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 12,\n" +
            "  \"amount\" : 1000,\n" +
            "  \"number\" : \"4000003305160034\",\n" +
            "  \"ip\" : \"192.168.1.1\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T16:00:00\",\n" +
            "  \"result\" : \"PROHIBITED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 13,\n" +
            "  \"amount\" : 1000,\n" +
            "  \"number\" : \"4000003305160034\",\n" +
            "  \"ip\" : \"192.168.1.67\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T16:00:00\",\n" +
            "  \"result\" : \"PROHIBITED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 14,\n" +
            "  \"amount\" : 2000,\n" +
            "  \"number\" : \"4000003305160034\",\n" +
            "  \"ip\" : \"192.168.1.67\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T16:00:00\",\n" +
            "  \"result\" : \"PROHIBITED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 15,\n" +
            "  \"amount\" : 1,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.2\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T17:10:00\",\n" +
            "  \"result\" : \"ALLOWED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 16,\n" +
            "  \"amount\" : 1,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.3\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T17:11:00\",\n" +
            "  \"result\" : \"ALLOWED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 17,\n" +
            "  \"amount\" : 1,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.3\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T17:12:00\",\n" +
            "  \"result\" : \"ALLOWED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 18,\n" +
            "  \"amount\" : 1,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.4\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T17:12:00\",\n" +
            "  \"result\" : \"MANUAL_PROCESSING\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 19,\n" +
            "  \"amount\" : 1,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.5\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T17:13:00\",\n" +
            "  \"result\" : \"PROHIBITED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 20,\n" +
            "  \"amount\" : 2000,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.5\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T17:14:00\",\n" +
            "  \"result\" : \"PROHIBITED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 21,\n" +
            "  \"amount\" : 1,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.2\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-21T17:10:00\",\n" +
            "  \"result\" : \"ALLOWED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 22,\n" +
            "  \"amount\" : 1,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.2\",\n" +
            "  \"region\" : \"ECA\",\n" +
            "  \"date\" : \"2022-01-21T17:11:00\",\n" +
            "  \"result\" : \"ALLOWED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 23,\n" +
            "  \"amount\" : 1,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.2\",\n" +
            "  \"region\" : \"ECA\",\n" +
            "  \"date\" : \"2022-01-21T17:12:00\",\n" +
            "  \"result\" : \"ALLOWED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 24,\n" +
            "  \"amount\" : 1,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.2\",\n" +
            "  \"region\" : \"HIC\",\n" +
            "  \"date\" : \"2022-01-21T17:13:00\",\n" +
            "  \"result\" : \"MANUAL_PROCESSING\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 25,\n" +
            "  \"amount\" : 1,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.2\",\n" +
            "  \"region\" : \"SSA\",\n" +
            "  \"date\" : \"2022-01-21T17:14:00\",\n" +
            "  \"result\" : \"PROHIBITED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 26,\n" +
            "  \"amount\" : 2000,\n" +
            "  \"number\" : \"4000008449433403\",\n" +
            "  \"ip\" : \"192.168.1.2\",\n" +
            "  \"region\" : \"SA\",\n" +
            "  \"date\" : \"2022-01-21T17:15:00\",\n" +
            "  \"result\" : \"PROHIBITED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "} ]\n";

    private final String historyAnswer2 = "[ {\n" +
            "  \"transactionId\" : 12,\n" +
            "  \"amount\" : 1000,\n" +
            "  \"number\" : \"4000003305160034\",\n" +
            "  \"ip\" : \"192.168.1.1\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T16:00:00\",\n" +
            "  \"result\" : \"PROHIBITED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 13,\n" +
            "  \"amount\" : 1000,\n" +
            "  \"number\" : \"4000003305160034\",\n" +
            "  \"ip\" : \"192.168.1.67\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T16:00:00\",\n" +
            "  \"result\" : \"PROHIBITED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "}, {\n" +
            "  \"transactionId\" : 14,\n" +
            "  \"amount\" : 2000,\n" +
            "  \"number\" : \"4000003305160034\",\n" +
            "  \"ip\" : \"192.168.1.67\",\n" +
            "  \"region\" : \"EAP\",\n" +
            "  \"date\" : \"2022-01-22T16:00:00\",\n" +
            "  \"result\" : \"PROHIBITED\",\n" +
            "  \"feedback\" : \"\"\n" +
            "} ]";




    public AntiFraudTest() {
        super(AntiFraudApplication.class, "../service_db.mv.db");
    }

    /**
     * Method for restarting application
     *
     */
    private CheckResult restartApplication() {
        try {
            reloadSpring();
        } catch (Exception ex) {
            throw new UnexpectedError(ex.getMessage());
        }
        return CheckResult.correct();
    }

    private HttpResponse checkResponseStatus(String user, String body,
                                             int status, String api, String method) {
        HttpRequest request = null;
        switch (method) {
            case "GET":
                request = get(api);
                break;
            case "POST":
                request = post(api, body);
                break;
            case "PUT":
                request = put(api, body);
                break;
            case "DELETE":
                request = delete(api);
                break;
        }

        if (user != null) {
            JsonObject userJson = getJson(user).getAsJsonObject();
            String password = userJson.get("password").getAsString();
            String login = userJson.get("username").getAsString().toLowerCase();
            request = request.basicAuth(login, password);
        }
        HttpResponse response = request.send();

        if (response.getStatusCode() != status) {
            throw new WrongAnswer(method + " " + api  + " should respond with "
                    + "status code " + status + ", responded: " + response.getStatusCode() + "\n"
                    + "Response body:\n" + response.getContent() + "\n");
        }
        return response;
    }

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            long l = Long.parseLong(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    CheckResult testAddUser(String body, int status, String role, TestHint hint) {

        System.out.println(hint.toString());

        HttpResponse response = checkResponseStatus(null, body, status, userApi, "POST");

        if (response.getStatusCode() == 201) {

            JsonObject rightResponse = getJson(body).getAsJsonObject();
            rightResponse.remove("password");

            // Check is it JSON in response or something else
            if (!response.getJson().isJsonObject()) {
                return CheckResult.wrong("Wrong object in response, expected JSON but was \n" +
                        response.getContent().getClass());

            }

            JsonObject jsonResponse = response.getJson().getAsJsonObject();

            // Check if password is presence in response
            if (jsonResponse.get("password") != null) {
                return CheckResult.wrong("You must remove password from response\n" +
                        jsonResponse);
            }

            if (jsonResponse.get("id") == null) {
                return CheckResult.wrong("Response must contain user ID\n" +
                        "Received response:\n" +
                        jsonResponse);
            }

            if (jsonResponse.get("id").isJsonNull()) {
                return CheckResult.wrong("Response must contain user ID\n" +
                        "Received response:\n" +
                        jsonResponse);
            }

            if (userIdList.contains(jsonResponse.get("id").getAsInt())) {
                return CheckResult.wrong("User ID must be unique!\n" +
                        "Received response:\n" +
                        jsonResponse);
            }

            rightResponse.addProperty("id", jsonResponse.get("id").toString());
            // Check JSON in response
            expect(response.getContent()).asJson().check(
                    isObject()
                            .value("id", isInteger())
                            .value("name", rightResponse.get("name").getAsString())
                            .value("role", role)
                            .value("username", isString(s -> s.equalsIgnoreCase(rightResponse.get("username").getAsString())))
            );
            userIdList.add(jsonResponse.get("id").getAsInt());
        }
        return CheckResult.correct();
    }

    CheckResult testDeleteUser(String body, int status, String user, TestHint hint) {

        System.out.println(hint.toString());

        HttpResponse response = checkResponseStatus(body, "", status, userApi + "/" + user, "DELETE");

        if (response.getStatusCode() == 200) {

            // Check is it JSON in response or something else
            if (!response.getJson().isJsonObject()) {
                return CheckResult.wrong("Wrong object in response, expected JSON but was \n" +
                        response.getContent().getClass());

            }

            JsonObject jsonResponse = response.getJson().getAsJsonObject();

            // Check JSON in response
            expect(response.getContent()).asJson().check(
                    isObject()
                            .value("status", "Deleted successfully!")
                            .value("username", isString(s -> s.equalsIgnoreCase(user)))
            );
        }
        return CheckResult.correct();
    }

    private CheckResult testListUser(String user, int status, String answer,
                                     int position, TestHint hint) {

        System.out.println(hint.toString());

        HttpResponse response = checkResponseStatus(user, "", status, userListApi, "GET");

        if (response.getStatusCode() == 200) {
            // Check is it array of JSON in response or something else
            if (!response.getJson().isJsonArray()) {
                return CheckResult.wrong("Wrong object in response, expected array of JSON but was \n" +
                        response.getContent().getClass());

            }

            JsonArray correctJson = getJson(answer).getAsJsonArray();
            JsonArray responseJson = getJson(response.getContent()).getAsJsonArray();
            if (responseJson.size() == 0) {
                throw new WrongAnswer("Empty array in response!");
            }

            if (responseJson.size() != position + 1) {
                throw new WrongAnswer("Incorrect number - " +  responseJson.size() +
                        " users in response, must be - " + (position + 1));
            }

            // Check JSON in response
            expect(responseJson.get(position).toString()).asJson().check(
                    isObject()
                            .value("id", isInteger())
                            .value("name", correctJson.get(position).getAsJsonObject().get("name").getAsString())
                            .value("role", correctJson.get(position).getAsJsonObject().get("role").getAsString())
                            .value("username",
                                    isString(s -> s.equalsIgnoreCase(correctJson.get(position).getAsJsonObject()
                                            .get("username").getAsString()))
                            ));

        }
        return CheckResult.correct();
    }

    private CheckResult testLock(String user, int status, String operation, String username, TestHint hint) {
        System.out.println(hint.toString());

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("username", username);
        jsonBody.addProperty("operation", operation);

        HttpResponse response = checkResponseStatus(user, jsonBody.toString(), status, lockApi, "PUT");
        if (response.getStatusCode() == 200) {
            expect(response.getContent()).asJson().check(
                    isObject()
                            .value("status", isString(s ->
                                    s.equalsIgnoreCase("User " + username + " " + operation + "ed!"))));
        }
        return CheckResult.correct();
    }

    private CheckResult testRole(String user, int status, String role, String username, TestHint hint) {
        System.out.println(hint.toString());

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("username", username);
        jsonBody.addProperty("role", role);

        HttpResponse response = checkResponseStatus(user, jsonBody.toString(), status, roleApi, "PUT");

        if (response.getStatusCode() == 200) {
            expect(response.getContent()).asJson().check(
                    isObject()
                            .value("id", isInteger())
                            .value("name", isString())
                            .value("role", role)
                            .value("username", isString(s -> s.equalsIgnoreCase(username))));
        }
        return CheckResult.correct();
    }

    private CheckResult testTransaction(String user, String api, String method, int status, String body,
                                        String answer, String answer2, TestHint hint) {

        System.out.println(hint.toString());

        HttpResponse response = checkResponseStatus(user, body, status, api, method);

        // Check JSON in response
        if (response.getStatusCode() == 200) {
            expect(response.getContent()).asJson().check(
                    isObject()
                            .value("result", answer)
                            .value("info", answer2));
        }
        return CheckResult.correct();
    }

    private CheckResult testAddIP(String user, int status, String ip, TestHint hint) {
        System.out.println(hint.toString());

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("ip", ip);

        HttpResponse response = checkResponseStatus(user, jsonBody.toString(), status, ipApi, "POST");
        if (response.getStatusCode() == 200) {
            expect(response.getContent()).asJson().check(
                    isObject()
                            .value("id", isInteger())
                            .value("ip", ip));
        }
        return CheckResult.correct();
    }

    private CheckResult testDeleteIP(String user, int status, String ip, TestHint hint) {
        System.out.println(hint.toString());

        HttpResponse response = checkResponseStatus(user, "", status, ipApi + "/" + ip, "DELETE");
        if (response.getStatusCode() == 200) {
            expect(response.getContent()).asJson().check(
                    isObject()
                            .value("status", "IP " + ip + " successfully removed!"));
        }
        return CheckResult.correct();
    }

    private CheckResult testAddCard(String user, int status, String number, TestHint hint) {
        System.out.println(hint.toString());

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("number", number);

        HttpResponse response = checkResponseStatus(user, jsonBody.toString(), status, cardApi, "POST");
        if (response.getStatusCode() == 200) {
            expect(response.getContent()).asJson().check(
                    isObject()
                            .value("id", isInteger())
                            .value("number", number));
        }
        return CheckResult.correct();
    }

    private CheckResult testDeleteCard(String user, int status, String number, TestHint hint) {
        System.out.println(hint.toString());

        HttpResponse response = checkResponseStatus(user, "", status, cardApi + "/" + number, "DELETE");
        if (response.getStatusCode() == 200) {
            expect(response.getContent()).asJson().check(
                    isObject()
                            .value("status", "Card " + number + " successfully removed!"));
        }
        return CheckResult.correct();
    }

    private CheckResult testGetIP(String user, int status, String answer, TestHint hint) {
        System.out.println(hint.toString());

        HttpResponse response = checkResponseStatus(user, "", status, ipApi, "GET");

        if (response.getStatusCode() == 200) {
            if (!response.getJson().isJsonArray()) {
                return CheckResult.wrong("Wrong object in response, expected array of JSON but was \n" +
                        response.getContent().getClass());

            }

            JsonArray correctJson = getJson(answer).getAsJsonArray();
            JsonArray responseJson = getJson(response.getContent()).getAsJsonArray();

            if (responseJson.size() != correctJson.size()) {
                throw new WrongAnswer("Incorrect number - " +  responseJson.size() +
                        " objects in response, must be - " + correctJson.size());
            }

            // Check JSON in response
            for (int i =0; i < correctJson.size(); i++) {
                expect(responseJson.get(i).toString()).asJson().check(
                        isObject()
                                .value("id", isInteger())
                                .value("ip", correctJson.get(i).getAsJsonObject().get("ip").getAsString())
                );
            }

        }
        return CheckResult.correct();
    }

    private CheckResult testGetCard(String user, int status, String answer, TestHint hint) {
        System.out.println(hint.toString());

        HttpResponse response = checkResponseStatus(user, "", status, cardApi, "GET");

        if (response.getStatusCode() == 200) {
            if (!response.getJson().isJsonArray()) {
                return CheckResult.wrong("Wrong object in response, expected array of JSON but was \n" +
                        response.getContent().getClass());

            }

            JsonArray correctJson = getJson(answer).getAsJsonArray();
            JsonArray responseJson = getJson(response.getContent()).getAsJsonArray();

            if (responseJson.size() != correctJson.size()) {
                throw new WrongAnswer("Incorrect number - " +  responseJson.size() +
                        " objects in response, must be - " + correctJson.size());
            }

            // Check JSON in response
            for (int i =0; i < correctJson.size(); i++) {
                expect(responseJson.get(i).toString()).asJson().check(
                        isObject()
                                .value("id", isInteger())
                                .value("number", correctJson.get(i).getAsJsonObject().get("number").getAsString())
                );
            }

        }
        return CheckResult.correct();
    }

    private CheckResult testGetTr(String user, int status, String answer, TestHint hint) {
        System.out.println(hint.toString());

        HttpResponse response = checkResponseStatus(user, "", status, historyApi, "GET");

        if (response.getStatusCode() == 200) {
            if (!response.getJson().isJsonArray()) {
                return CheckResult.wrong("Wrong object in response, expected array of JSON but was \n" +
                        response.getContent().getClass());

            }

            JsonArray correctJson = getJson(answer).getAsJsonArray();
            JsonArray responseJson = getJson(response.getContent()).getAsJsonArray();

            if (responseJson.size() != correctJson.size()) {
                throw new WrongAnswer("Incorrect number - " +  responseJson.size() +
                        " objects in response, must be - " + correctJson.size());
            }

            // Check JSON in response
            for (int i =0; i < correctJson.size(); i++) {
                expect(responseJson.get(i).toString()).asJson().check(
                        isObject()
                                .value("transactionId", isInteger())
                                .value("amount", correctJson.get(i).getAsJsonObject().get("amount").getAsLong())
                                .value("number", correctJson.get(i).getAsJsonObject().get("number").getAsString())
                                .value("ip", correctJson.get(i).getAsJsonObject().get("ip").getAsString())
                                .value("region", correctJson.get(i).getAsJsonObject().get("region").getAsString())
                                .value("date", correctJson.get(i).getAsJsonObject().get("date").getAsString())
                                .value("result", correctJson.get(i).getAsJsonObject().get("result").getAsString())
                                .value("feedback", correctJson.get(i).getAsJsonObject().get("feedback").getAsString())
                );
            }

        }
        return CheckResult.correct();
    }

    private CheckResult testGetTrByNumber(String user, int status, String answer,
                                          String number, TestHint hint) {
        System.out.println(hint.toString());

        HttpResponse response = checkResponseStatus(user, "", status,
                historyApi + "/" + number, "GET");

        if (response.getStatusCode() == 200) {
            if (!response.getJson().isJsonArray()) {
                return CheckResult.wrong("Wrong object in response, expected array of JSON but was \n" +
                        response.getContent().getClass());

            }

            JsonArray correctJson = getJson(answer).getAsJsonArray();
            JsonArray responseJson = getJson(response.getContent()).getAsJsonArray();

            if (responseJson.size() != correctJson.size()) {
                throw new WrongAnswer("Incorrect number - " +  responseJson.size() +
                        " objects in response, must be - " + correctJson.size());
            }

            // Check JSON in response
            for (int i =0; i < correctJson.size(); i++) {
                expect(responseJson.get(i).toString()).asJson().check(
                        isObject()
                                .value("transactionId", isInteger())
                                .value("amount", correctJson.get(i).getAsJsonObject().get("amount").getAsLong())
                                .value("number", correctJson.get(i).getAsJsonObject().get("number").getAsString())
                                .value("ip", correctJson.get(i).getAsJsonObject().get("ip").getAsString())
                                .value("region", correctJson.get(i).getAsJsonObject().get("region").getAsString())
                                .value("date", correctJson.get(i).getAsJsonObject().get("date").getAsString())
                                .value("result", correctJson.get(i).getAsJsonObject().get("result").getAsString())
                                .value("feedback", correctJson.get(i).getAsJsonObject().get("feedback").getAsString())
                );
            }

        }
        return CheckResult.correct();
    }

    private CheckResult testFeed(String userS, String userM, int status, int position,
                                 String fb, TestHint hint) {
        System.out.println(hint.toString());
        HttpResponse response = checkResponseStatus(userS, "", 200, historyApi, "GET");

        if (!response.getJson().isJsonArray()) {
            return CheckResult.wrong("Wrong object in response, expected array of JSON but was \n" +
                    response.getContent().getClass());

        }

        JsonArray responseJson = getJson(response.getContent()).getAsJsonArray();
        if (responseJson.size() < (position + 1)) {
            throw new WrongAnswer("Incorrect number - " +  responseJson.size() +
                    " transactions in history, must be at least - " + (position + 1));
        }

        if (!responseJson.get(position).getAsJsonObject().has("transactionId")) {
            return CheckResult.wrong("Json in response must have - 'id' field");
        }

        Long id = responseJson.get(position).getAsJsonObject().get("transactionId").getAsLong();

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("transactionId", id);
        jsonBody.addProperty("feedback", fb);

        response = checkResponseStatus(userM, jsonBody.toString(), status, transactionApi, "PUT");

        JsonObject answerJson = responseJson.get(position).getAsJsonObject();

        if (response.getStatusCode() == 200) {
            expect(response.getContent()).asJson().check(
                    isObject()
                            .value("transactionId", isInteger())
                            .value("amount", answerJson.get("amount").getAsLong())
                            .value("ip", answerJson.get("ip").getAsString())
                            .value("number", answerJson.get("number").getAsString())
                            .value("region", answerJson.get("region").getAsString())
                            .value("date", answerJson.get("date").getAsString())
                            .value("result", answerJson.get("result").getAsString())
                            .value("feedback", fb)
            );
        }
        return CheckResult.correct();
    }

    private CheckResult testNotFoundFeed(String userS, String userM, int status, int position,
                                         String fb, TestHint hint) {
        System.out.println(hint.toString());
        HttpResponse response = checkResponseStatus(userS, "", 200, historyApi, "GET");
        JsonArray responseJson = getJson(response.getContent()).getAsJsonArray();
        if (responseJson.size() < (position + 1)) {
            throw new WrongAnswer("Incorrect number - " +  responseJson.size() +
                    " transactions in history, must be at least - " + (position + 1));
        }
        Long id = responseJson.get(position).getAsJsonObject().get("transactionId").getAsLong() + 12345L;

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("transactionId", id);
        jsonBody.addProperty("feedback", fb);

        return CheckResult.correct();
    }


    @DynamicTest
    DynamicTesting[] dt = new DynamicTesting[]{
            // Test POST request for signup api
            () -> testAddUser(wronguser1, 400, "MERCHANT",
                    new TestHint(userApi, wronguser1, "In case of wrong data in request, endpoint" +
                            " must respond with BAD REQUEST  status (400).")), // 1

            () -> testAddUser(wronguser2, 400, "MERCHANT",
                    new TestHint(userApi, wronguser2, "In case of wrong data in request, endpoint" +
                            " must respond with BAD REQUEST  status (400).")), // 2

            () -> testAddUser(administrator, 201, "ADMINISTRATOR",
                    new TestHint(userApi, johndoe1, "If user successfully added, endpoint" +
                            " must respond with HTTP CREATED status (201) ")), // 3

            () -> testListUser(administrator, 200,  listAnswer1, 0,
                    new TestHint(userListApi, "", "Endpoint must respond with HTTP OK status (200)" +
                            " and body with array of objects representing the users sorted by ID in ascending order.")), // 4


            () -> testAddUser(johndoe1, 201, "MERCHANT",
                    new TestHint(userApi, johndoe1, "If user successfully added, endpoint" +
                            " must respond with HTTP CREATED status (201) ")), // 5

            () -> testListUser(administrator, 200,  listAnswer1, 1,
                    new TestHint(userListApi, "", "Endpoint must respond with HTTP OK status (200)" +
                            " and body with array of objects representing the users sorted by ID in ascending order.")), // 6

            () -> testListUser(wronguserCred1, 401,  listAnswer1, 0,
                    new TestHint(userListApi, "", "A user with incorrect credentials is not allowed")), // 7

            () -> testListUser(wronguserCred2, 401,  listAnswer1, 0,
                    new TestHint(userListApi, "", "A user with incorrect credentials is not allowed")), // 8

            () -> testTransaction(johndoe1, transactionApi, "POST", 401,tr1,
                    "ALLOWED", "none",
                    new TestHint(transactionApi, "", "Merchant after registration" +
                            " must be LOCKED")), // 9

            () -> testLock(administrator, 200, "UNLOCK", "johndoe1",
                    new TestHint(lockApi, "", "A user johndoe1 must be UNLOCKED")), // 10

            () -> testRole(administrator,200,"SUPPORT", "johndoe1",
                    new TestHint(roleApi, "", "If a role successfully changed," +
                            " respond with the HTTP OK status (200)")), // 11

            () -> testGetTr(johndoe1, 200, "[]",
                    new TestHint(historyApi, "", "User johndoe1 must be unlocked." +
                            "Endpoint must respond with the HTTP OK status (200) and" +
                            " empty array in case if history is empty.")), // 12


            () -> testRole(administrator,200,"MERCHANT", "johndoe1",
                    new TestHint(roleApi, "", "If a role successfully changed," +
                            " respond with the HTTP OK status (200)")), // 13


            () -> testTransaction(johndoe1, transactionApi, "POST", 200,tr1,
                    "ALLOWED", "none",
                    new TestHint(transactionApi, "", "A user johndoe1 must be UNLOCKED")), // 14

            () -> testTransaction(johndoe1, transactionApi, "POST", 200,tr1,
                    "ALLOWED", "none",
                    new TestHint(transactionApi, "amount = 1", "Result validating of Transaction" +
                            " must be 'ALLOWED'")), // 15

            // Testing persistence
            () -> restartApplication(), // 16

            () -> testListUser(administrator, 200,  listAnswer1, 1,
                    new TestHint(userListApi, "", "After restart user must exist")), // 17

            () -> testAddUser(johndoe1, 409, "MERCHANT",
                    new TestHint(userApi, johndoe1, "In case of an attempt to register an existing user," +
                            " endpoint must respond with HTTP CONFLICT status (409).")), // 18

            () -> testAddUser(johndoe2, 201, "MERCHANT",
                    new TestHint(userApi, johndoe2, "If user successfully added, endpoint" +
                            " must respond with HTTP CREATED status (201) ")), // 19

            () -> testListUser(administrator, 200,  listAnswer1, 2,
                    new TestHint(userListApi, "", "Endpoint must respond with HTTP OK status (200)" +
                            " and body with array of objects representing the users sorted by ID in ascending order.")), // 20

            () -> testTransaction(johndoe1, transactionApi, "POST", 200, tr2,
                    "ALLOWED", "none",
                    new TestHint(transactionApi, "amount = 199", "Result validating of Transaction" +
                            " must be 'ALLOWED'")), // 21

            () -> testTransaction(johndoe1, transactionApi, "POST", 200, tr3,
                    "ALLOWED", "none",
                    new TestHint(transactionApi, "amount = 200", "Result validating of Transaction" +
                            " must be 'ALLOWED'")), // 22

            () -> testTransaction(johndoe1, transactionApi, "POST", 200, tr4,
                    "MANUAL_PROCESSING", "amount",
                    new TestHint(transactionApi, "amount = 201", "Result validating of Transaction" +
                            " must be 'MANUAL_PROCESSING'")), // 23

            () -> testTransaction(johndoe1, transactionApi, "POST", 200, tr5,
                    "MANUAL_PROCESSING", "amount",
                    new TestHint(transactionApi, "amount = 1499", "Result validating of Transaction" +
                            " must be 'MANUAL_PROCESSING'")), // 24

            () -> testTransaction(johndoe1, transactionApi, "POST", 200, tr6,
                    "MANUAL_PROCESSING", "amount",
                    new TestHint(transactionApi, "amount = 1500", "Result validating of Transaction" +
                            " must be 'MANUAL_PROCESSING'")), // 25

            () -> testTransaction(johndoe1, transactionApi, "POST", 200, tr7,
                    "PROHIBITED", "amount",
                    new TestHint(transactionApi, "amount = 1501", "Result validating of Transaction" +
                            " must be 'PROHIBITED'")), // 26

            () -> testTransaction(johndoe1, transactionApi, "POST", 200, tr8,
                    "PROHIBITED", "amount",
                    new TestHint(transactionApi, "amount = 2000", "Result validating of Transaction" +
                            " must be 'PROHIBITED'")), // 27

            () -> testTransaction(johndoe1, transactionApi, "POST", 400, trW1,
                    "Wrong request!", "none",
                    new TestHint(transactionApi, "amount = -1", "Response status" +
                            " must be 'Bad request'")), // 28

            () -> testTransaction(johndoe1, transactionApi, "POST", 400, trW2,
                    "Wrong request!", "none",
                    new TestHint(transactionApi, "amount = 0", "Response status" +
                            " must be 'Bad request'")), // 29

            () -> testTransaction(johndoe1, transactionApi, "POST", 400,trW3,
                    "Wrong request!", "none",
                    new TestHint(transactionApi, "amount = null", "Response status" +
                            " must be 'Bad request'")), // 30

            () -> testTransaction(johndoe1, transactionApi, "POST", 400,trW4,
                    "Wrong request!", "none",
                    new TestHint(transactionApi, "amount = \"\"", "Response status" +
                            " must be 'Bad request'")), // 31

            () -> testTransaction(johndoe1, transactionApi, "POST", 400, trW5,
                    "Wrong request!",  "none",
                    new TestHint(transactionApi, "amount = \" \"", "Response status" +
                            " must be 'Bad request'")), // 32

            () -> testTransaction(johndoe1, transactionApi, "POST", 400, trW6,
                    "Wrong request!",   "none",
                    new TestHint(transactionApi, "empty body", "Response status" +
                            " must be 'Bad request'")), // 33

            () -> testDeleteUser(administrator,404,"somebody",
                    new TestHint(userApi, userApi + "/" + "somebody", "If a user is not found," +
                            " respond with HTTP Not Found status (404).")), // 34

            () -> testDeleteUser(administrator,200,"johndoe1",
                    new TestHint(userApi, userApi + "/" + "johndoe1", "The endpoint must delete" +
                            " the user and respond with HTTP OK status (200)")), // 35

            () -> testListUser(administrator, 200,  listAnswer2, 1,
                    new TestHint(userListApi, "", "User 'johndoe1' must be delete")), // 36

            () -> testRole(administrator,404,"MERCHANT", "johndoe22",
                    new TestHint(roleApi, "", "If a user is not found, endpoint" +
                            " must respond with the HTTP Not Found status (404).")), // 37

            () -> testRole(administrator,400,"USER", "johndoe2",
                    new TestHint(roleApi, "", "If a role is not found, endpoint" +
                            " must respond with the HTTP Bad Request status (400).")), // 38

            () -> testRole(administrator,400,"ADMINISTRATOR", "johndoe2",
                    new TestHint(roleApi, "", "If a role is ADMINISTRATOR, endpoint" +
                            " must respond with the HTTP Bad Request status (400).")), // 39

            () -> testRole(administrator,200,"SUPPORT", "johndoe2",
                    new TestHint(roleApi, "", "If a role successfully changed," +
                            " respond with the HTTP OK status (200)")), // 40

            () -> testRole(administrator,409,"SUPPORT", "johndoe2",
                    new TestHint(roleApi, "", "If a role already assigned to a user," +
                            " endpoint must respond with the HTTP Conflict status (409).")), // 41

            () -> testLock(administrator, 200, "UNLOCK", "johndoe2",
                    new TestHint(lockApi, "", "A user johndoe2 must be UNLOCKED")), // 42

            () -> testListUser(johndoe2, 200,  listAnswer3, 1,
                    new TestHint(userListApi, "", "Role for user 'johndoe2" +
                            "' must be changed to SUPPORT")), // 43

            () -> testTransaction(johndoe2, transactionApi, "POST", 403, tr1,
                    "ALLOWED",   "none",
                    new TestHint(transactionApi, "", "Role MERCHANT must be removed from" +
                            " user johndoe2!")), // 44

            // test Locking
            () -> testAddUser(johndoe3, 201, "MERCHANT",
                    new TestHint(userApi, johndoe3, "If user successfully added, endpoint" +
                            " must respond with HTTP CREATED status (201) ")), // 45

            () -> testTransaction(johndoe3, transactionApi, "POST", 401, tr1,
                    "ALLOWED",  "none",
                    new TestHint(transactionApi, "", "MERCHANT user after registration" +
                            " must be LOCKED")), // 46

            () -> testLock(administrator, 200, "UNLOCK", "johndoe3",
                    new TestHint(lockApi, "", "A user johndoe3 must be UNLOCKED")), // 47

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, tr1,
                    "ALLOWED",  "none",
                    new TestHint(transactionApi, "", "A user johndoe3 must be UNLOCKED")), // 48

            () -> testLock(administrator, 200, "LOCK", "johndoe3",
                    new TestHint(lockApi, "", "A user johndoe3 must be LOCKED")), // 49

            () -> testTransaction(johndoe3, transactionApi, "POST", 401, tr1,
                    "ALLOWED",  "none",
                    new TestHint(transactionApi, "", "A user johndoe3 must be LOCKED")), // 50

            () -> testLock(administrator, 200, "UNLOCK", "johndoe3",
                    new TestHint(lockApi, "", "A user johndoe3 must be UNLOCKED")), // 51

            // test role model
            () -> testTransaction(administrator, transactionApi, "POST", 403, tr1,
                    "ALLOWED",  "none",
                    new TestHint(transactionApi, "", "A user with role ADMINISTRATOR" +
                            " must not have access to " + transactionApi)), // 52

            () -> testTransaction(johndoe2, transactionApi, "POST", 403, tr1,
                    "ALLOWED",  "none",
                    new TestHint(transactionApi, "", "A user with role SUPPORT" +
                            " must not have access to " + transactionApi)), // 53

            () -> testTransaction(null, transactionApi, "POST", 401, tr1,
                    "ALLOWED",  "none",
                    new TestHint(transactionApi, "", "A user with role ANONYMOUS" +
                            " must not have access to " + transactionApi)), // 54

            () -> testDeleteUser(null,401,"johndoe1",
                    new TestHint(userApi, "", "A user with role ANONYMOUS" +
                            " must not have access to DELETE " + userApi)), // 55

            () -> testDeleteUser(johndoe2,403,"johndoe1",
                    new TestHint(userApi, "", "A user with role SUPPORT" +
                            " must not have access to DELETE " + userApi)), // 56

            () -> testDeleteUser(johndoe3,403,"johndoe1",
                    new TestHint(userApi, "", "A user with role MERCHANT" +
                            " must not have access to DELETE " + userApi)), // 57

            () -> testListUser(null, 401,  listAnswer3, 1,
                    new TestHint(userListApi, "", "A user with role ANONYMOUS" +
                            " must not have access to " + userListApi)), // 58

            () -> testListUser(johndoe3, 403,  listAnswer3, 1,
                    new TestHint(userListApi, "", "A user with role MERCHANT" +
                            " must not have access to " + userListApi)), // 59

            () -> testGetIP(administrator, 403, ipAnswerEmpty,
                    new TestHint(ipApi, "", "A user with role ADMINISTRATOR" +
                            " must not have access to GET " + ipApi)), // 60

            () -> testGetIP(johndoe3, 403, ipAnswerEmpty,
                    new TestHint(ipApi, "", "A user with role MERCHANT" +
                            " must not have access to GET " + ipApi)), // 61

            () -> testAddIP(administrator, 403, "192.168.1.66",
                    new TestHint(ipApi, "", "A user with role ADMINISTRATOR" +
                            " must not have access to POST " + ipApi)), // 62

            () -> testAddIP(johndoe3, 403, "192.168.1.66",
                    new TestHint(ipApi, "", "A user with role MERCHANT" +
                            " must not have access to POST " + ipApi)), // 63

            () -> testDeleteIP(administrator, 403, "192.168.1.66",
                    new TestHint(ipApi, "", "A user with role ADMINISTRATOR" +
                            " must not have access to DELETE " + ipApi)), // 64

            () -> testDeleteIP(johndoe3, 403, "192.168.1.66",
                    new TestHint(ipApi, "", "A user with role MERCHANT" +
                            " must not have access to DELETE " + ipApi)), // 65

            () -> testGetCard(administrator, 403, cardAnswerEmpty,
                    new TestHint(cardApi, "", "A user with role ADMINISTRATOR" +
                            " must not have access to GET " + cardApi)), // 66

            () -> testGetCard(johndoe3, 403, cardAnswerEmpty,
                    new TestHint(cardApi, "", "A user with role MERCHANT" +
                            " must not have access to GET " + cardApi)), // 67

            () -> testAddCard(administrator, 403, "4000003305061034",
                    new TestHint(cardApi, "", "A user with role ADMINISTRATOR" +
                            " must not have access to POST " + cardApi)), // 68

            () -> testAddCard(johndoe3, 403, "4000003305061034",
                    new TestHint(cardApi, "", "A user with role MERCHANT" +
                            " must not have access to POST " + cardApi)), // 69

            () -> testDeleteIP(administrator, 403, "4000003305061034",
                    new TestHint(cardApi, "", "A user with role ADMINISTRATOR" +
                            " must not have access to DELETE " + cardApi)), // 70

            () -> testDeleteIP(johndoe3, 403, "4000003305061034",
                    new TestHint(cardApi, "", "A user with role MERCHANT" +
                            " must not have access to DELETE " + cardApi)), // 71


            // Test ip black list
            () -> testGetIP(johndoe2, 200, ipAnswerEmpty,
                    new TestHint(ipApi, "", "Endpoint must respond with empty array")), // 72

            () -> testAddIP(johndoe2, 200, "192.168.1.66",
                    new TestHint(ipApi, "", "IP must be added to Black List")), // 73

            () -> testAddIP(johndoe2, 409, "192.168.1.66",
                    new TestHint(ipApi, "", "If IP already in database, " +
                            " endpoint must respond with the HTTP Conflict status (409).")), // 74

            () -> testAddIP(johndoe2, 400, "192.168.351.66",
                    new TestHint(ipApi, "", "If IP doesn't have right format," +
                            " respond with HTTP Bad Request status (400)")), // 75

            () -> testAddIP(johndoe2, 400, "192.168.1.",
                    new TestHint(ipApi, "", "If IP doesn't have right format," +
                            " respond with HTTP Bad Request status (400)")), // 76

            () -> testAddIP(johndoe2, 200, "192.168.1.67",
                    new TestHint(ipApi, "", "IP must be added to Black List")), // 77

            () -> testGetIP(johndoe2, 200, ipAnswer,
                    new TestHint(ipApi, "", "Endpoint must responds with Black List")), // 78

            () -> testDeleteIP(johndoe2, 200, "192.168.1.66",
                    new TestHint(ipApi, "", "IP must be removed from Black List")), // 79

            () -> testDeleteIP(johndoe2, 404, "192.168.1.66",
                    new TestHint(ipApi, "", "If IP not found in database, " +
                            "respond with the HTTP Not Found status (404).")), // 80

            () -> testDeleteIP(johndoe2, 400, "192.168.1.",
                    new TestHint(ipApi, "", "If IP doesn't have right format," +
                            " respond with HTTP Bad Request status (400)")), // 81

            () -> testGetIP(johndoe2, 200, ipAnswer2,
                    new TestHint(ipApi, "", "IP must be removed from Black List")), // 82


            // Test card number black list
            () -> testGetCard(johndoe2, 200, cardAnswerEmpty,
                    new TestHint(cardApi, "", "Endpoint must respond with empty array")), // 83

            () -> testAddCard(johndoe2, 200, "4000003305061034",
                    new TestHint(cardApi, "", "A card must be added to Black List")), // 84

            () -> testAddCard(johndoe2, 409, "4000003305061034",
                    new TestHint(cardApi, "", "If card-number already in database, " +
                            "endpoint must respond with the HTTP Conflict status (409).")), // 85

            () -> testAddCard(johndoe2, 400, "400000330506103",
                    new TestHint(cardApi, "", "If card-number doesn't have right format, " +
                            "endpoint must respond with HTTP Bad Request status (400).")), // 86

            () -> testAddCard(johndoe2, 400, "4000003305061033",
                    new TestHint(cardApi, "", "If checksum of card-number is wrong, " +
                            "endpoint must respond with HTTP Bad Request status (400).")), // 87

            () -> testAddCard(johndoe2, 200, "4000003305160034",
                    new TestHint(cardApi, "", "A card must be added to Black List")), // 88

            () -> testGetCard(johndoe2, 200, cardAnswer,
                    new TestHint(cardApi, "", "Endpoint must responds with Black List")), // 89

            () -> testDeleteCard(johndoe2, 200, "4000003305061034",
                    new TestHint(cardApi, "", "Card must be removed from Black List")), // 90

            () -> testDeleteCard(johndoe2, 404, "4000003305061034",
                    new TestHint(cardApi, "", "If card-number not found in database," +
                            " respond with the HTTP Not Found status (404).")), // 91

            () -> testDeleteCard(johndoe2, 400, "400000330506103",
                    new TestHint(cardApi, "", "If card-number doesn't have right format, " +
                            "endpoint must respond with HTTP Bad Request status (400).")), // 92

            () -> testGetCard(johndoe2, 200, cardAnswer2,
                    new TestHint(cardApi, "", "Card must be removed from Black List")), // 93

            // Test new validating
            () -> testTransaction(johndoe3, transactionApi, "POST", 200, trP1,
                    "PROHIBITED",  "ip",
                    new TestHint(transactionApi, "", "Transaction must be PROHIBITED" +
                            " due to - suspicious ip reason")), // 94

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, trP2,
                    "PROHIBITED",  "card-number",
                    new TestHint(transactionApi, "", "Transaction must be PROHIBITED" +
                            " due to - stolen card reason")), // 95

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, trP3,
                    "PROHIBITED",  "card-number, ip",
                    new TestHint(transactionApi, "", "Transaction must be PROHIBITED" +
                            " due to - stolen card, suspicious ip reasons")), // 96

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, trP4,
                    "PROHIBITED",  "amount, card-number, ip",
                    new TestHint(transactionApi, "", "Transaction must be PROHIBITED" +
                            " due to - amount, stolen card, suspicious ip reasons")), // 97

            // Test correlation rules
            () -> testTransaction(johndoe3, transactionApi, "POST", 200, corr1IP,
                    "ALLOWED",  "none",
                    new TestHint(transactionApi, "", "Transaction must be ALLOWED")), // 98

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, corr2IP,
                    "ALLOWED",  "none",
                    new TestHint(transactionApi, "", "Transaction must be ALLOWED")), // 99

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, corr22IP,
                    "ALLOWED",  "none",
                    new TestHint(transactionApi, "", "Transaction must be ALLOWED")), // 100

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, corr3IP,
                    "MANUAL_PROCESSING",  "ip-correlation",
                    new TestHint(transactionApi, "", "Transaction must be MANUAL_PROCESSING" +
                            " due to - ip correlation rule")), // 101

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, corr4IP,
                    "PROHIBITED",  "ip-correlation",
                    new TestHint(transactionApi, "", "Transaction must be PROHIBITED" +
                            " due to - ip correlation rule")), // 102

            () -> testAddCard(johndoe2, 200, "4000008449433403",
                    new TestHint(cardApi, "", "A card must be added to Black List")), // 103

            () -> testAddIP(johndoe2, 200, "192.168.1.5",
                    new TestHint(ipApi, "", "IP must be added to Black List")), // 104

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, corr5IP,
                    "PROHIBITED",  "amount, card-number, ip, ip-correlation",
                    new TestHint(transactionApi, "", "Transaction must be PROHIBITED" +
                            " due to - amount, card-number, ip, ip-correlation reasons")), // 105
            //
            () -> testDeleteCard(johndoe2, 200, "4000008449433403",
                    new TestHint(cardApi, "", "Card must be removed from Black List")), // 106

            () -> testDeleteIP(johndoe2, 200, "192.168.1.5",
                    new TestHint(ipApi, "", "IP must be removed from Black List")), // 107


            () -> testTransaction(johndoe3, transactionApi, "POST", 200, corr1Reg,
                    "ALLOWED",  "none",
                    new TestHint(transactionApi, "", "Transaction must be ALLOWED")), // 108

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, corr2Reg,
                    "ALLOWED",  "none",
                    new TestHint(transactionApi, "", "Transaction must be ALLOWED")), // 109

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, corr22Reg,
                    "ALLOWED",  "none",
                    new TestHint(transactionApi, "", "Transaction must be ALLOWED")), // 110

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, corr3Reg,
                    "MANUAL_PROCESSING",  "region-correlation",
                    new TestHint(transactionApi, "", "Transaction must be MANUAL_PROCESSING" +
                            " due to - region correlation rule")), // 111

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, corr4Reg,
                    "PROHIBITED",  "region-correlation",
                    new TestHint(transactionApi, "", "Transaction must be PROHIBITED" +
                            " due to - region correlation rule")), // 112

            () -> testAddCard(johndoe2, 200, "4000008449433403",
                    new TestHint(cardApi, "", "A card must be added to Black List")), // 113

            () -> testAddIP(johndoe2, 200, "192.168.1.2",
                    new TestHint(ipApi, "", "IP must be added to Black List")), // 114

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, corr5Reg,
                    "PROHIBITED",  "amount, card-number, ip, region-correlation",
                    new TestHint(transactionApi, "", "Transaction must be PROHIBITED" +
                            " due to - amount, card-number, ip, region-correlation reasons")), // 115


            // Stage 6 let's go

            // Preparations
            () -> testDeleteCard(johndoe2, 200, "4000008449433403",
                    new TestHint(cardApi, "", "Card must be removed from Black List")), // 116

            // Test history negative
            () -> testGetTrByNumber(johndoe2, 404, historyAnswer2, "4000004938320896",
                    new TestHint(historyApi + "/4000004938320896", "", "If transactions " +
                            "for specified card number is not found in database," +
                            " respond with the HTTP Not Found status (404).")), // 117

            () -> testGetTrByNumber(johndoe2, 400, historyAnswer2, "4000008449433402",
                    new TestHint(historyApi + "/4000008449433402", "", "If card-number " +
                            "doesn't have right format (Luhn algo), respond with HTTP Bad Request status (400).")), // 118

            // Test Feedback negative
            () -> testFeed(johndoe2, johndoe2, 422, 0, "ALLOWED",
                    new TestHint(transactionApi, "", "If feedback must throw Exception due to" +
                            " table in description section, respond with HTTP Unprocessable Entity status 422.")), // 119

            () -> testFeed(johndoe2, johndoe2, 422, 4, "MANUAL_PROCESSING",
                    new TestHint(transactionApi, "", "If feedback must throw Exception due to" +
                            " table in description section, respond with HTTP Unprocessable Entity status 422.")), // 120

            () -> testFeed(johndoe2, johndoe2, 422, 7, "PROHIBITED",
                    new TestHint(transactionApi, "", "If feedback must throw Exception due to" +
                            " table in description section, respond with HTTP Unprocessable Entity status 422.")), // 121

            () -> testNotFoundFeed(johndoe2, johndoe2, 404, 25, "PROHIBITED",
                    new TestHint(transactionApi, "", "If transaction is not found in history," +
                            " respond with the HTTP Not Found status (404).")), // 122

            // test Role model
            () -> testFeed(johndoe2, null, 401, 7, "PROHIBITED",
                    new TestHint(transactionApi, "", "A user with role ANONYMOUS" +
                            " must not have access to PUT " + transactionApi)), // 123

            () -> testFeed(johndoe2, administrator, 403, 7, "PROHIBITED",
                    new TestHint(transactionApi, "", "A user with role ADMINISTRATOR" +
                            " must not have access to PUT " + transactionApi)), // 124

            () -> testFeed(johndoe2, johndoe3, 403, 7, "PROHIBITED",
                    new TestHint(transactionApi, "", "A user with role MERCHANT" +
                            " must not have access to PUT " + transactionApi)), // 125

            () -> testGetTr(null, 401, historyAnswer,
                    new TestHint(historyApi, "", "A user with role ANONYMOUS" +
                            " must not have access to GET " + historyApi)), // 126

            () -> testGetTrByNumber(null, 401, historyAnswer2, "4000003305160034",
                    new TestHint(historyApi + "/4000003305160034", "", "A user with" +
                            " role ANONYMOUS must not have access to GET " + historyApi)), // 127

            () -> testGetTr(administrator, 403, historyAnswer,
                    new TestHint(historyApi, "", "A user with role ADMINISTRATOR" +
                            " must not have access to GET " + historyApi)), // 128

            () -> testGetTrByNumber(administrator, 403, historyAnswer2, "4000003305160034",
                    new TestHint(historyApi + "/4000003305160034", "", "A user with" +
                            " role ADMINISTRATOR must not have access to GET " + historyApi)), // 129

            () -> testGetTr(johndoe3, 403, historyAnswer,
                    new TestHint(historyApi, "", "A user with role MERCHANT" +
                            " must not have access to GET " + historyApi)), // 130

            () -> testGetTrByNumber(johndoe3, 403, historyAnswer2, "4000003305160034",
                    new TestHint(historyApi + "/4000003305160034", "", "A user with" +
                            " role MERCHANT must not have access to GET " + historyApi)), // 131


            // test History positive
            () -> testGetTr(johndoe2, 200, historyAnswer,
                    new TestHint(historyApi, "", "Endpoint must respond with the" +
                            " HTTP OK status (200) and body with array of JSON objects representing transactions," +
                            " sorted by id in ascending order")), // 132

            () -> testGetTrByNumber(johndoe2, 200, historyAnswer2, "4000003305160034",
                    new TestHint(historyApi + "/4000003305160034", "", "If transactions" +
                            " for specified card number is found, respond with the HTTP OK status (200)")), // 133


            // Test changing coefficients
            // Result - MANUAL_PROCESSING, FeedBack - ALLOWED
            () -> testTransaction(johndoe3, transactionApi, "POST", 200, tr9,
                    "MANUAL_PROCESSING",  "amount",
                    new TestHint(transactionApi, "", "Transaction must be MANUAL_PROCESSING")), // 134

            () -> testFeed(johndoe2, johndoe2, 200, 26, "ALLOWED",
                    new TestHint(transactionApi, "", "The limits of transaction validation updated." +
                            "Endpoint responds with the HTTP OK status (200)")), // 135

            () -> testFeed(johndoe2, johndoe2, 409, 26, "PROHIBITED",
                    new TestHint(transactionApi, "", "If feedback for specified transaction" +
                            " is already in database, respond with the HTTP Conflict status (409).")), // 136

            () -> testFeed(johndoe2, johndoe2, 400, 26, "ALLOWEDD",
                    new TestHint(transactionApi, "", "If feedback doesn't have right format" +
                            " (ALLOWED, MANUAL_PROCESSING, PROHIBITED), respond with HTTP Bad Request status (400).")), // 137

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, tr10,
                    "ALLOWED",  "none",
                    new TestHint(transactionApi, "", "Limits must be updated to:" +
                            " Allow = 202, Manual = 1500. Transaction must be ALLOWED")), // 138


            // Result - MANUAL_PROCESSING, FeedBack - PROHIBITED
            () -> testTransaction(johndoe3, transactionApi, "POST", 200, tr11,
                    "MANUAL_PROCESSING",  "amount",
                    new TestHint(transactionApi, "", "Transaction must be MANUAL_PROCESSING")), // 139

            () -> testFeed(johndoe2, johndoe2, 200, 28, "PROHIBITED",
                    new TestHint(transactionApi, "", "The limits of transaction validation updated." +
                            "Endpoint responds with the HTTP OK status (200)")), // 140

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, tr12,
                    "PROHIBITED",  "amount",
                    new TestHint(transactionApi, "", "Limits must be updated to:" +
                            " Allow = 202, Manual = 1160. " +
                            "Transaction must be PROHIBITED")), // 141


            // Result - ALLOWED, FeedBack - MANUAL_PROCESSING
            () -> testTransaction(johndoe3, transactionApi, "POST", 200, tr1,
                    "ALLOWED",  "none",
                    new TestHint(transactionApi, "", "Transaction must be ALLOWED")), // 142

            () -> testFeed(johndoe2, johndoe2, 200, 30, "MANUAL_PROCESSING",
                    new TestHint(transactionApi, "", "The limits of transaction validation updated." +
                            "Endpoint responds with the HTTP OK status (200)")), // 143

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, tr13,
                    "MANUAL_PROCESSING",  "amount",
                    new TestHint(transactionApi, "", "Limits must be updated to:" +
                            " Allow = 162, Manual = 1160. Transaction must be MANUAL_PROCESSING")), // 144


            // Result - ALLOWED, FeedBack - PROHIBITED
            () -> testTransaction(johndoe3, transactionApi, "POST", 200, tr1,
                    "ALLOWED",  "none",
                    new TestHint(transactionApi, "", "Transaction must be ALLOWED")), // 145

            () -> testFeed(johndoe2, johndoe2, 200, 32, "PROHIBITED",
                    new TestHint(transactionApi, "", "The limits of transaction validation updated." +
                            "Endpoint responds with the HTTP OK status (200)")), // 146

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, tr14,
                    "MANUAL_PROCESSING",  "amount",
                    new TestHint(transactionApi, "", "Limits must be updated to:" +
                            " Allow = 130, Manual = 928. Transaction must be MANUAL_PROCESSING")), // 147

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, tr15,
                    "PROHIBITED",  "amount",
                    new TestHint(transactionApi, "", "Limits must be updated to:" +
                            " Allow = 130, Manual = 928. Transaction must be PROHIBITED")), // 148


            // Result - PROHIBITED, FeedBack - ALLOWED
            () -> testFeed(johndoe2, johndoe2, 200, 34, "ALLOWED",
                    new TestHint(transactionApi, "", "The limits of transaction validation updated." +
                            "Endpoint responds with the HTTP OK status (200)")), // 149

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, tr16,
                    "ALLOWED",  "none",
                    new TestHint(transactionApi, "", "Limits must be updated to:" +
                            " Allow = 290, Manual = 929. Transaction must be ALLOWED")), // 150

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, tr15,
                    "MANUAL_PROCESSING",  "amount",
                    new TestHint(transactionApi, "", "Limits must be updated to:" +
                            " Allow = 290, Manual = 929. Transaction must be MANUAL_PROCESSING")), // 151

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, tr17,
                    "PROHIBITED",  "amount",
                    new TestHint(transactionApi, "", "PROHIBITED")), // 152


            // Result - PROHIBITED, FeedBack - MANUAL_PROCESSING
            () -> testFeed(johndoe2, johndoe2, 200, 37, "MANUAL_PROCESSING",
                    new TestHint(transactionApi, "", "The limits of transaction validation updated." +
                            "Endpoint responds with the HTTP OK status (200)")), // 153

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, tr17,
                    "MANUAL_PROCESSING",  "amount",
                    new TestHint(transactionApi, "", "Limits must be updated to:" +
                            " Allow = 290, Manual = 930. Transaction must be MANUAL_PROCESSING")), // 154

            () -> restartApplication(), // 155

            () -> testTransaction(johndoe3, transactionApi, "POST", 200, tr18,
                    "PROHIBITED",  "amount",
                    new TestHint(transactionApi, "", "Limits must be updated to:" +
                            " Allow = 290, Manual = 930. Transaction must be PROHIBITED")), // 156

    };
}