Feature: List user pictures

  In order for my to browse my pictures
  As the user
  I want have a listing of all my pictures

  acceptance criteria:
  - observe device pictures

  @observe-device-pictures-happy-path
  Scenario: User device pictures displayed
    Given User has test pictures on device
    And User opens pictures browser screen
    Then Pictures browser screen should display pictures by date in descending order
    When User removes one of test pictures from device
    Then Pictures browser screen should update displayed data

  @observe-device-pictures-sad-path
  Scenario: App fail to fetch device pictures
    Given User opens pictures browser screen
    When App fail to fetch device pictures
    Then User should see an error message displayed