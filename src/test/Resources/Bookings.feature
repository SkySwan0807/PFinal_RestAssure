Feature: Booking endpoint

  @run
  Scenario: GET all bookings
    Given I perform a GET call to the booking endpoint
    Then I verify that the status code is 200

  @run
  Scenario: GET booking by id
    Given I perform a GET call to the booking endpoint with id "290"
    Then I verify that the status code is 200


  @run
  Scenario Outline: POST a new booking
    Given I perform a POST call to the booking endpoint with the following data
      | <firstname> | <lastname> | <totalprice> | <depositpaid> | <checkin> | <checkout> | <additionalneeds> |
    Then I verify that the status code is 200
    And I verify that the field "booking.firstname" contains "<firstname>"

    Examples:
      | firstname | lastname | totalprice | depositpaid | checkin   | checkout  | additionalneeds |
      | John      | Doe      | 150        | true        | 2025-09-20 | 2025-09-25 | Breakfast       |

  @run
  Scenario: Generate auth token
    Given I generate a new auth token
    Then I verify that the status code is 200
    And I verify that the field token is not empty

  @run
  Scenario Outline: PUT update a booking
    Given I generate a new auth token
    Then I verify that the status code is 200
    And I perform a PUT call to the booking endpoint with id "290" and the following data
      | <firstname> | <lastname> | <totalprice> | <depositpaid> | <checkin> | <checkout> | <additionalneeds> |
    Then I verify that the status code is 200
    And I verify that the field "firstname" contains "<firstname>"

    Examples:
      | firstname | lastname | totalprice | depositpaid | checkin   | checkout  | additionalneeds |
      | Jane      | Smith    | 200        | false       | 2025-10-01 | 2025-10-05 | Lunch          |

  @run
  Scenario Outline: DELETE a booking
    Given I generate a new auth token
    And I perform a DELETE call to the booking endpoint with id "<id>"
    Then I verify that the status code is 201

    Examples:
      | id |
      | 1  |
