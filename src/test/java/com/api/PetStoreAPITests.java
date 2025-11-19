package com.api;


import com.utils.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PetStoreAPITests {
    private static final Logger logger = LogManager.getLogger(PetStoreAPITests.class);
    private static final String BASE_URI = ConfigReader.getPetStoreApiUrl();
    private long petId;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URI;
        logger.info("PetStore API tests initialized with base URI: " + BASE_URI);
    }

    @Test(priority = 1, description = "POST - Create a new pet (Positive)")
    public void testCreatePetPositive() {
        logger.info("Testing CREATE pet - Positive scenario");

        String requestBody = "{\n" +
                "  \"id\": 0,\n" +
                "  \"category\": {\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"Dogs\"\n" +
                "  },\n" +
                "  \"name\": \"Max\",\n" +
                "  \"photoUrls\": [\"string\"],\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"name\": \"friendly\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"status\": \"available\"\n" +
                "}";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/pet")
                .then()
                .statusCode(200)
                .body("name", equalTo("Max"))
                .body("status", equalTo("available"))
                .extract().response();

        petId = response.jsonPath().getLong("id");
        logger.info("Pet created successfully with ID: " + petId);

        Assert.assertNotNull(petId, "Pet ID should not be null");
    }

    @Test(priority = 2, description = "POST - Create pet with invalid data (Negative)")
    public void testCreatePetNegative() {
        logger.info("Testing CREATE pet - Negative scenario (invalid data)");

        String invalidRequestBody = "{\n" +
                "  \"id\": \"invalid\",\n" +
                "  \"name\": \"\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(invalidRequestBody)
                .when()
                .post("/pet")
                .then()
                .statusCode(anyOf(is(400), is(405), is(500)));

        logger.info("Invalid pet creation handled correctly");
    }

    @Test(priority = 3, description = "GET - Retrieve pet by ID (Positive)", dependsOnMethods = "testCreatePetPositive")
    public void testGetPetByIdPositive() {
        logger.info("Testing GET pet by ID - Positive scenario for ID: " + petId);

        given()
                .pathParam("petId", petId)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(200)
                .body("id", equalTo((int) petId))
                .body("name", equalTo("Max"))
                .body("status", equalTo("available"));

        logger.info("Pet retrieved successfully");
    }

    @Test(priority = 4, description = "GET - Retrieve non-existent pet (Negative)")
    public void testGetPetByIdNegative() {
        logger.info("Testing GET pet by ID - Negative scenario (non-existent ID)");

        long nonExistentId = 999999999L;

        given()
                .pathParam("petId", nonExistentId)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(404);

        logger.info("Non-existent pet handled correctly with 404");
    }

    @Test(priority = 5, description = "PUT - Update existing pet (Positive)", dependsOnMethods = "testCreatePetPositive")
    public void testUpdatePetPositive() {
        logger.info("Testing UPDATE pet - Positive scenario for ID: " + petId);

        String updateRequestBody = "{\n" +
                "  \"id\": " + petId + ",\n" +
                "  \"category\": {\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"Dogs\"\n" +
                "  },\n" +
                "  \"name\": \"Max Updated\",\n" +
                "  \"photoUrls\": [\"string\"],\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"name\": \"friendly\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"status\": \"sold\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(updateRequestBody)
                .when()
                .put("/pet")
                .then()
                .statusCode(200)
                .body("name", equalTo("Max Updated"))
                .body("status", equalTo("sold"));

        logger.info("Pet updated successfully");
    }

    @Test(priority = 6, description = "PUT - Update non-existent pet (Negative)")
    public void testUpdatePetNegative() {
        logger.info("Testing UPDATE pet - Negative scenario (non-existent pet)");

        long nonExistentId = 999999999L;

        String updateRequestBody = "{\n" +
                "  \"id\": " + nonExistentId + ",\n" +
                "  \"name\": \"Ghost Pet\",\n" +
                "  \"status\": \"available\"\n" +
                "}";

        given()
                .contentType(ContentType.JSON)
                .body(updateRequestBody)
                .when()
                .put("/pet")
                .then()
                .statusCode(anyOf(is(404), is(400)));

        logger.info("Non-existent pet update handled correctly");
    }

    @Test(priority = 7, description = "DELETE - Delete pet by ID (Positive)", dependsOnMethods = "testUpdatePetPositive")
    public void testDeletePetPositive() {
        logger.info("Testing DELETE pet - Positive scenario for ID: " + petId);

        given()
                .pathParam("petId", petId)
                .when()
                .delete("/pet/{petId}")
                .then()
                .statusCode(200);

        logger.info("Pet deleted successfully");

        // Verify deletion
        given()
                .pathParam("petId", petId)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(404);

        logger.info("Pet deletion verified - pet no longer exists");
    }

    @Test(priority = 8, description = "DELETE - Delete non-existent pet (Negative)")
    public void testDeletePetNegative() {
        logger.info("Testing DELETE pet - Negative scenario (non-existent pet)");

        long nonExistentId = 999999999L;

        given()
                .pathParam("petId", nonExistentId)
                .when()
                .delete("/pet/{petId}")
                .then()
                .statusCode(404);

        logger.info("Non-existent pet deletion handled correctly");
    }

    @Test(priority = 9, description = "GET - Find pets by status (Positive)")
    public void testFindPetsByStatusPositive() {
        logger.info("Testing GET pets by status - Positive scenario");

        given()
                .queryParam("status", "available")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0].status", equalTo("available"));

        logger.info("Pets found by status successfully");
    }

    @Test(priority = 10, description = "GET - Find pets by invalid status (Negative)")
    public void testFindPetsByStatusNegative() {
        logger.info("Testing GET pets by status - Negative scenario (invalid status)");

        given()
                .queryParam("status", "invalidStatus")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(anyOf(is(200), is(400)));

        logger.info("Invalid status handled correctly");
    }
}