import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Test;
import pojo.Data;
import pojo.JsonRoot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ApiTestOne {

    final String reqresUrl = "https://reqres.in/";
    final String BreakingBadUrl = "https://breaking-bad-quotes.herokuapp.com";

    @Test
    public void listOfAllUsers() {

        Response response = given().baseUri(reqresUrl).param("page=2").
                when().get("api/users").then().extract().response();

        System.out.println(response.prettyPrint());
    }

    @Test
    public void getUsersEmails() {

        String response = given().baseUri(reqresUrl).param("page=2").
                when().get("api/users").then().extract().response().asString();

        JsonPath jsonPath = new JsonPath(response);

        int count = jsonPath.getInt("data.size");

        for (int i = 0; i < count; i++) {
            String email = jsonPath.get("data[" + i + "].email");
            System.out.println("email: " + email);
        }
    }

    @Test
    public void getFullNameLoopExample () {

        String response = given().baseUri(reqresUrl).param("page=2").
                when().get("api/users").then().extract().response().asString();

        JsonPath jsonPath = new JsonPath(response);

        int count = jsonPath.getInt("data.size");

        for (int i = 0; i < count; i++) {
            String first_name = jsonPath.get("data[" + i + "].first_name");
            String last_name = jsonPath.get("data[" + i + "].last_name");
            System.out.println("Full name: " + first_name + " " + last_name);
        }
    }

    @Test
    public void pojoClassExample() {

        JsonRoot getAll = given().baseUri(reqresUrl).param("page=2").
                when().get("api/users").as(JsonRoot.class);

        List<Data> allData = getAll.getData();

        ArrayList<String> avatarsList = new ArrayList<>();

        for (int i = 0; i < allData.size(); i++) {
            avatarsList.add(allData.get(i).getAvatar());
        }

        avatarsList.forEach(value -> System.out.println("avatar: " + value));
    }

    @Test
    public void jacksonLibraryExample() throws JsonProcessingException {

        String response = given().baseUri(reqresUrl).param("page=2").
                when().get("api/users").asString();

        ObjectMapper mapper = new ObjectMapper();

        jackson.JsonRoot rootLevel = mapper.readValue(response, jackson.JsonRoot.class);

        List<jackson.Data> allData = rootLevel.data;

        ArrayList<String> emailLists = new ArrayList<>();

        for (int i = 0; i < allData.size(); i++) {
            emailLists.add(allData.get(i).email);
        }

        emailLists.forEach(value -> System.out.println("email: " + value));

    }

    @Test
    public void authorizationExample(){

        String jsonBody = "{\n" +
                "    \"email\": \"eve.holt@reqres.in\",\n" +
                "    \"password\": \"cityslicka\"\n" +
                "}";

        given().baseUri(reqresUrl).contentType(ContentType.JSON).body(jsonBody)
                .when().post("api/login").then().statusCode(200)
                .log().body().body("token", is (notNullValue()));

    }

    @Test
    public void authorizationExampleWithReadingFile () throws IOException {

        String jsonBody = new String(Files.readAllBytes(Paths.get("src/test/resources/loginData")));

        given().baseUri(reqresUrl).contentType(ContentType.JSON).body(jsonBody)
                .when().post("api/login").then().statusCode(200)
                .log().body().body("token", is (notNullValue()));

    }

    @Test
    public void breakingBadQuotesAndAuthors () {

        Response response = given().baseUri(BreakingBadUrl).
                when().get("/v1/quotes/5").then().extract().response();

        System.out.println(response.prettyPrint());

    }

    @Test
    public void breakingBadQuotesList() {

        String response = given().baseUri(BreakingBadUrl).
                when().get("/v1/quotes/5").then().extract().response().asString();

        JsonPath jsonPath = new JsonPath(response);

        List<String> quotesList = jsonPath.getList("quote");

        quotesList.forEach(value -> System.out.println("quote: " + value));

    }

    @Test
    public void breakingBadQuotesLoop() {

        String response = given().baseUri(BreakingBadUrl).
                when().get("/v1/quotes/5").then().extract().response().asString();

        JsonPath jsonPath = new JsonPath(response);

        int count = jsonPath.getInt("$.size");

        for (int i = 0; i < count; i++) {
            String author = jsonPath.get("author[" + i + "]");
            String quote = jsonPath.get("quote[" + i + "]");
            System.out.println(author + ": " + quote);
        }
    }

    @Test
    public void breakingBadQuotesMap() {

        String response = given().baseUri(BreakingBadUrl).
                when().get("/v1/quotes/5").then().extract().response().asString();

        JsonPath jsonPath = new JsonPath(response);

        int count = jsonPath.getInt("$.size");

        Map<String, String> quotesAuthors = new HashMap<>();

        for (int i = 0; i < count; i++) {
            String author = jsonPath.get("author[" + i + "]");
            String quote = jsonPath.get("quote[" + i + "]");
            quotesAuthors.put(author,quote);
        }

        quotesAuthors.forEach((k,v) -> System.out.println((k + ": " + v)));
    }









}
