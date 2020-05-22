Feature: Home features e2e workflows

  In order to facilitate app features usage
  As the user
  I want to have a centralized home screen

  @features-nav
  Scenario: User navigates to app features workflow
    Given User launch app from device home screen
    And App home screen is displayed
    When User navigates to settings
    Then Settings should be displayed in own screen
    When User navigates to videos browser
    Then Videos browser ui should show as composite in home screen
    When User navigates to pictures browser
    Then Pictures browser ui should show as composite in home screen
    When User navigates to recycle bin
    Then Recycle bin ui should show as composite in home screen