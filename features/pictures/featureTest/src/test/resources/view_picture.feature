Feature: Enable picture viewing

  In order to see up close device pictures
  As the user
  I want to be able to view my pictures

  acceptance criteria:
  - open full view of existing picture

  Scenario: User views public picture
    Given User has public pictures on device
    When User opens an existing picture to view it
    Then Picture is shown in picture viewing screen