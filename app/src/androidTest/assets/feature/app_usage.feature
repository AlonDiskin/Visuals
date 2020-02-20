Feature: App usage workflows

  various scenarios that demonstrate e2e usage workflows
  in application

  @settings-features
  Scenario: Application settings usage workflow
    Given User launch app from device home screen
    When User navigates to default values settings screen
    Then App visual theme should be set as day theme
    When User selects the night theme
    Then App theme should be changed to night

  @pictures-browser-features
  Scenario: Pictures browser usage workflow
    Given User has public pictures on device
    And User launch app from device home screen
    When User navigates to pictures browser screen
    Then All user device public pictures are shown by date in descended order

  @videos-browser-features
  Scenario: Videos browser usage workflow
    Given User launch app from device home screen
    When User navigates to videos browser screen
