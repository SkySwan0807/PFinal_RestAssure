package StepDefinitions;

import Entities.Booking;
import Entities.BookingDates;
import Entities.Credenciales;
import Utils.Request;
import Constants.BookingEndPoints;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.hamcrest.Matchers;


import java.util.List;

import static org.hamcrest.Matchers.hasKey;

public class BookingSteps {

    Response response;
    Credenciales creds = new Credenciales(); // Para auth token

    // ---------------- GET all bookings ----------------
    @When("I perform a GET call to the booking endpoint")
    public void getAllBookings() throws InterruptedException {
        Thread.sleep(5000);
        response = Request.get(BookingEndPoints.GET_BOOKINGS);
    }

    @And("I verify that the status code is {int}")
    public void verifyStatusCode(int statusCode) {
        response.then().assertThat().statusCode(statusCode);
    }

    // ---------------- GET booking by ID ----------------
    @When("I perform a GET call to the booking endpoint with id {string}")
    public void getBookingById(String id) {
        response = Request.getById(BookingEndPoints.GET_BOOKING, id);
    }

    @Then("I verify that the field {string} contains {string}")
    public void verifyFieldValue(String field, String value) {
        response.then().assertThat().body(field, Matchers.equalTo(value));
    }

    // ---------------- POST booking ----------------
    @When("I perform a POST call to the booking endpoint with the following data")
    public void createBooking(DataTable bookingData) throws Exception {
        Thread.sleep(5000);
        List<String> data = bookingData.transpose().asList(String.class);
        //Map<String, String> data = bookingData.asMaps(String.class, String.class).get(0);

        Booking booking = new Booking();
        booking.setFirstname(data.get(0));
        booking.setLastname(data.get(1));
        booking.setTotalprice(Integer.parseInt(data.get(2)));
        booking.setDepositpaid(Boolean.parseBoolean(data.get(3)));
        booking.setAdditionalneeds(data.get(6));

        // Crear BookingDates y asignar checkin/checkout
        BookingDates dates = new BookingDates();
        dates.setCheckin(data.get(4));
        dates.setCheckout(data.get(5));

        booking.setBookingdates(dates);

        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(booking);

        response = Request.post(BookingEndPoints.POST_BOOKING, payload);
    }

    // ---------------- Auth Token ----------------
    @When("I generate a new auth token")
    public void generateAuthToken() throws InterruptedException {
        Thread.sleep(5000);
        response = Request.createAuthToken(creds);
    }

    @And("I verify that the field token is not empty")
    public void verifyToken() {
        // Verificar que el token no sea null ni vacío
        assert creds.getToken() != null && !creds.getToken().isEmpty()
                : "El token está vacío o nulo";

        System.out.println("Token generado: " + creds.getToken());
    }

    // ---------------- PUT booking ----------------
    @When("I perform a PUT call to the booking endpoint with id {string} and the following data")
    public void updateBooking(String id, DataTable bookingData) throws Exception {
        List<String> data = bookingData.transpose().asList(String.class);

        BookingDates dates = new BookingDates();
        dates.setCheckin(data.get(4));
        dates.setCheckout(data.get(5));

        Booking booking = new Booking();
        booking.setFirstname(data.get(0));
        booking.setLastname(data.get(1));
        booking.setTotalprice(Integer.parseInt(data.get(2)));
        booking.setDepositpaid(Boolean.parseBoolean(data.get(3)));
        booking.setBookingdates(dates);
        booking.setAdditionalneeds(data.get(6));

        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(booking);

        // PUT con token
        response = Request.putWithAuth(BookingEndPoints.PUT_BOOKING, id, payload, creds.getToken());
    }

    // ---------------- DELETE booking ----------------
    @When("I perform a DELETE call to the booking endpoint with id {string}")
    public void deleteBooking(String id) {
        response = Request.deleteWithAuth(BookingEndPoints.DELETE_BOOKING, id, creds.getToken());
    }

    // ---------------- Verify JSON fields ----------------
    @And("I verify that the following fields are present in the root")
    public void verifyRootFields(DataTable fields) {
        List<String> data = fields.transpose().asList(String.class);
        for (String field : data) {
            response.then().assertThat().body("$", hasKey(field));
        }
    }
}
