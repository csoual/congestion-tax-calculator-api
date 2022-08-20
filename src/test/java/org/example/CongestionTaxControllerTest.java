package test.java.org.example;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class CongestionTaxControllerTest {

    @Test
    void testComputeTax() {
        given().when()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "vehicleType": "Car",
                            "dates": [
                                "2013-01-02T06:00:00Z"
                            ]
                        }
                        """)
                .post("/tax_calculator")
                .then()
                .statusCode(200)
                .body("taxAmount", equalTo(8))
                .log().all();
    }

    @Test
    void testComputeTaxWithExemptVehicle() {
        given().when()
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "vehicleType": "Military",
                            "dates": [
                                "2013-01-02T06:00:00Z"
                            ]
                        }
                        """)
                .post("/tax_calculator")
                .then()
                .statusCode(200)
                .body("taxAmount", equalTo(0))
                .log().all();
    }

}
