package com.example.demo.simplecrudwithrestapi;

import com.example.demo.simplecrudwithrestapi.entity.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.hamcrest.Matchers.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(
        classes = SimpleCrudWithRestAPIAerospikeDemoApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class SimpleCrudIntegrationTests extends SimpleCrudWithRestAPIAerospikeDemoApplicationTest {

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Order(0)
    @Test
    void returnsEmptyWithNoUser() {
        RestAssured.given()
                .get("/demo/users/1")
                .then()
                .assertThat()
                .statusCode(204)
                .noRootPath();
    }

    @Order(1)
    @Test
    void saveAndFind() {
        RestAssured.given()
                .body(new User(1, "John", "john@abc.com", 71))
                .contentType(ContentType.JSON)
                .post("/demo/users")
                .then()
                .assertThat()
                .statusCode(200);

        RestAssured.given()
                .get("/demo/users/1")
                .then()
                .assertThat()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("name", equalTo("John"))
                .body("email", equalTo("john@abc.com"))
                .body("age", equalTo(71));
    }

    @Order(2)
    @Test
    void savesAndFindsByLastName() {
        RestAssured.given()
                .contentType(ContentType.JSON)
                .delete("/demo/users/1")
                .then()
                .assertThat()
                .statusCode(200);

        RestAssured.given()
                .get("/demo/users/1")
                .then()
                .assertThat()
                .statusCode(204)
                .noRootPath();
    }

    /*
    For testing application with REST API and Aerospike Server Docker image:
    - run SimpleCrudWithRestAPIAerospikeDemoApplicationTest in debugger with Docker image running
    - send the requests:
    curl -X POST -H 'Content-Type: application/json' -d '{ "id": "1", "name": "name1" }' localhost:8080/demo/users
    curl -X GET localhost:8080/demo/users/1
    curl -X DELETE -H 'Content-Type: application/json' -d '{ "id": "1", "name": "name1" }' localhost:8080/demo/users/1
    */
}
