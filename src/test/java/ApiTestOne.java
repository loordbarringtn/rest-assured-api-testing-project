import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Test;
import pojo.Data;
import pojo.JsonRoot;

import java.util.*;

import static io.restassured.RestAssured.given;

public class ApiTestOne {

    final String reqresUrl = "https://reqres.in/";

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
    public void getFullName() {

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
    public void pojoExample() {

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
    public void jacksonExample() throws JsonProcessingException {

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
    public void breakingBadQuotes() {

        Response response = given().baseUri("https://breaking-bad-quotes.herokuapp.com").
                when().get("/v1/quotes/5").then().extract().response();

        System.out.println(response.prettyPrint());

    }

    @Test
    public void breakingBadQuotesList() {

        String response = given().baseUri("https://breaking-bad-quotes.herokuapp.com").
                when().get("/v1/quotes/5").then().extract().response().asString();

        JsonPath jsonPath = new JsonPath(response);

        List<String> quotesList = jsonPath.getList("quote");

        quotesList.forEach(value -> System.out.println("quote: " + value));

    }

    @Test
    public void breakingBadQuotesLoop() {

        String response = given().baseUri("https://breaking-bad-quotes.herokuapp.com").
                when().get("/v1/quotes/5").then().extract().response().asString();

        JsonPath jsonPath = new JsonPath(response);

        int count = jsonPath.getInt("$.size");

        for (int i = 0; i < count; i++) {
            String author = jsonPath.get("author[" + i + "]");
            String quote = jsonPath.get("quote[" + i + "]");
            System.out.println(author + ": " + quote);
        }
    }


}
