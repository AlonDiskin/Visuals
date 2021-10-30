Feature: App usage workflows

  various scenarios that demonstrate e2e usage, of app features workflows.

  @settings-feature
  Scenario: Application settings usage workflow
    Given User launch app from device home screen
    When User navigates to default values settings screen
    Then App visual theme should be set as day theme
    When User selects the night theme
    Then App theme should be changed to night

  @pictures-browser-feature
  Scenario: Pictures browser usage workflow
    Given User has public pictures on device
    And User launch app from device home screen
    When User navigates to pictures browser screen
    Then All user device public pictures are shown by date in descended order
    Then Pictures are displayed as before
    When User selects the first listed picture for sharing
    Then App should share picture

 @picture-viewer-feature
 Scenario: Pictures viewer usage workflow
   Given User has public pictures on device
   And User launch app from device home screen
   When User navigates to pictures browser screen
   And Open the first shown picture
   Then Picture should be displayed in full view in own screen
   And Picture detail are shown

  @videos-browser-feature
  Scenario: Videos browser usage workflow
    Given User has public videos on his device
    And User launch app from device home screen
    When User navigates to videos browser screen
    Then All user device public videos should be shown by date in descending order
    When User rotates device
    Then Videos are displayed as before
    When User selects the first listed video for sharing
    Then App should share video
