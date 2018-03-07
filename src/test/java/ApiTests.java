import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.given;


public class ApiTests {

    private final String BASE_URL = "http://localhost:7003";
    private final String FIB_SERIES = BASE_URL + "/fib";
    private final String FIB_ATINDEX = BASE_URL + "/fib/{index}";
    private final String FIB_RANGE = BASE_URL + "/fib/range";

    @Test
    public void getStandardFibonacciSequenceApiTest() {

        List<String> expectedFibonacciSeries = Arrays.asList("0", "1", "1", "2", "3", "5", "8", "13", "21", "34");
        Response response = given().when().
                get(FIB_SERIES)
                .then()
                .statusCode(200).contentType(ContentType.JSON).
                        extract().response();
        Assert.assertEquals(response.jsonPath().getList(""), expectedFibonacciSeries);
    }

    @Test
    public void getFibonacciNumberAtGivenIndexInSequenceApiTest() {

        int index = 6;
        int expectedValueAtIndex = 8;

        Response response = given().when()
                .pathParam("index", index)
                .get(FIB_ATINDEX)
                .then()
                .statusCode(200).contentType(ContentType.JSON).
                        extract().response();
        Assert.assertEquals(Integer.valueOf(response.asString()).intValue(), expectedValueAtIndex);
    }

    @Test
    public void getFibonacciNumberAtGivenIndexInSequenceAndCompareWithSeries() {
        int index = 9;
        Response response = given().when().
                get(FIB_SERIES)
                .then()
                .statusCode(200).contentType(ContentType.JSON).
                        extract().response();
        List<String> actualSeries = response.jsonPath().getList("");


        Response response1 = given().when()
                .pathParam("index", index)
                .get(FIB_ATINDEX)
                .then()
                .statusCode(200).contentType(ContentType.JSON).
                        extract().response();
        Assert.assertEquals(response1.asString(), actualSeries.get(index));
    }

    @Test
    public void getFibonacciSequenceBetweenTwoIndexsApiTest() {
        int startIndex = 3;
        int finishIndex = 7;
        List<String> expectedFibonacciRange = Arrays.asList("2", "3", "5", "8");
        Response response = given().when()
                .queryParam("startIndex", startIndex)
                .queryParam("finishIndex", finishIndex)
                .get(FIB_RANGE)
                .then()
                .statusCode(200).contentType(ContentType.JSON).
                        extract().response();
        Assert.assertEquals(response.jsonPath().getList(""), expectedFibonacciRange);
    }

    @Test
    public void getFibonacciSequenceBetweenTwoIndexsAndCompareWithSeriesApiTest() {
        int startIndex = 4;
        int finishIndex = 9;
        List<Integer> actualSeries = given().when().
                get(FIB_SERIES)
                .then()
                .statusCode(200).contentType(ContentType.JSON).
                        extract().response().jsonPath().getList("");
        List<Integer> expectedSeries = new ArrayList<Integer>();
        for (int i = startIndex; i < finishIndex; i++) {
            expectedSeries.add(actualSeries.get(i));

        }
        Response response = given().when()
                .queryParam("startIndex", startIndex)
                .queryParam("finishIndex", finishIndex)
                .get(FIB_RANGE)
                .then()
                .statusCode(200).contentType(ContentType.JSON).
                        extract().response();
        Assert.assertEquals(response.jsonPath().getList(""), expectedSeries);
    }

    @Test
    public void getFibonacciSequenceBetweenInvalidTwoIndexsApiTest() {
        int startIndex = 12;
        int finishIndex = 3;
        Response response = given().when()
                .queryParam("startIndex", startIndex)
                .queryParam("finishIndex", finishIndex)
                .get(FIB_RANGE)
                .then()
                .statusCode(200).contentType(ContentType.JSON).
                        extract().response();
        Assert.assertEquals(response.jsonPath().getList(""), Collections.EMPTY_LIST);
    }


    @Test
    public void getFibonacciNumberAtGivenIndexOutofIndexRange() {

       // Integer index out of range
        given().when()
                .pathParam("index", "2147483648")
                .get(FIB_ATINDEX)
                .then()
                .statusCode(404) ;
    }


}
