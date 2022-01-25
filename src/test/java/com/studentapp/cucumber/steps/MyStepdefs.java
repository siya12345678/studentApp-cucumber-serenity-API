package com.studentapp.cucumber.steps;

import com.studentapp.constants.EndPoints;
import com.studentapp.model.StudentPojo;
import com.studentapp.studentinfo.StudentSteps;
import com.studentapp.utils.TestUtils;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.response.ValidatableResponse;
import net.serenitybdd.rest.SerenityRest;
import net.thucydides.core.annotations.Steps;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.hasValue;

/**
 * Created by Jay
 */
public class MyStepdefs {
    @Steps
    StudentSteps studentSteps;
    static String firstName = "PrimUser" + TestUtils.getRandomValue();
    static String lastName = "PrimeUser" + TestUtils.getRandomValue();
    static String programme = "Api Testing";
    static String email = TestUtils.getRandomValue() + "xyz@gmail.com";
    static int studentId;

    @Given("^user have database connection and performing different requests$")
    public void userHaveDatabaseConnectionAndPerformingDifferentRequests() {
        System.out.println("**********************************************************");
        System.out.println("Student Database is connected");
        System.out.println("**********************************************************");

    }

    @When("^user send get request and receive status code (\\d+)$")
    public void userSendGetRequestAndReceiveStatusCode(int arg0) {
        int atcualResponce = studentSteps.getAllStudent();
        Assert.assertEquals("Status Code not 200",atcualResponce,arg0);
        System.out.println("**********************************************************");
        System.out.println("Expected Status Code :"+arg0);
        System.out.println("Actual Status Code : "+atcualResponce);
        System.out.println("**********************************************************");
    }


    @Then("^creating student with all require fields$")
    public void creatingStudentWithAllRequireFields() {
        List<String> courseList = new ArrayList<>();
        courseList.add("Scala");
        courseList.add("Java");
        ValidatableResponse response = studentSteps.createStudent(firstName, lastName, email, programme, courseList);
        response.log().all().statusCode(201);
        System.out.println("**********************************************************");
        System.out.println("User is created with Firstname:" +firstName+ " and Last name:"+lastName);
        System.out.println("**********************************************************");
    }




    @And("^I try to get User database with its firstname$")
    public void iTryToGetUserDatabaseWithItsFirstname() {
        String p1 = "findAll{it.firstName=='";
        String p2 = "'}.get(0)";
        HashMap<String, Object> value = SerenityRest.given().log().all()
                .when()
                .get(EndPoints.GET_ALL_STUDENT)
                .then()
                .statusCode(200)
                .extract()
                .path(p1 + firstName + p2);
        Assert.assertThat(value, hasValue(firstName));
        studentId = (int) value.get("id");
        System.out.println("**********************************************************");
        System.out.println("Database found with user name :"+firstName);
        System.out.println("**********************************************************");
    }


    @And("^I try to update user by updating its firstname$")
    public void iTryToUpdateUserByUpdatingItsFirstname() {
        firstName = firstName + "_updated";

        List<String> courseList = new ArrayList<>();
        courseList.add("Scala");
        courseList.add("Java");

        StudentPojo studentPojo = new StudentPojo();
        studentPojo.setFirstName(firstName);
        studentPojo.setLastName(lastName);
        studentPojo.setEmail(email);
        studentPojo.setProgramme(programme);
        studentPojo.setCourses(courseList);
        SerenityRest.given().log().all()
                .header("Content-Type", "application/json")
                .pathParam("studentID", studentId)
                .body(studentPojo)
                .when()
                .put(EndPoints.UPDATE_STUDENT_BY_ID)
                .then().log().all().statusCode(200);

        String p1 = "findAll{it.firstName=='";
        String p2 = "'}.get(0)";

        HashMap<String, Object> value = SerenityRest.given().log().all()
                .when()
                .get(EndPoints.GET_ALL_STUDENT)
                .then()
                .statusCode(200)
                .extract()
                .path(p1 + firstName + p2);
        Assert.assertThat(value, hasValue(firstName));
        System.out.println("**********************************************************");
        System.out.println("Database Update with user name :"+firstName);
        System.out.println("**********************************************************");
    }


    @Then("^I delete student from database and confirm it$")
    public void iDeleteStudentFromDatabaseAndConfirmIt() {
        studentSteps.deleteStudent(studentId).statusCode(204);
        studentSteps.getStudentById(studentId) .statusCode(404);
    }
}
