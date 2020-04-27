Feature: Videos features e2e workflows

  In order to access my device videos
  As the user
  I want to be able interact with my device videos

  Background:
    Given User has public videos on his device
    And User launch app from device home screen
    When User navigates to videos browser screen

  @videos-browser
  Scenario: Videos browser usage
    Then All user device public videos should be shown by date in descending order
    When User rotates device
    Then Videos are displayed as before
    When User selects the first listed video for sharing
    Then App should share video

 @videos-player
 Scenario: Videos player usage
   When User selects the first listed video for playing
   Then Video playback preview is shown