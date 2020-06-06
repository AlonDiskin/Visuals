Feature: User videos browser

  In order to interact my device videos
  As the user
  I want to be able access with my device videos

  Background:
    Given User has public videos on his device
    And User launch app from device home screen
    When User navigates to videos browser screen

  @videos-browser
  Scenario: Videos browser usage
    Then All user device public videos should be shown by date in descending order
    When User selects the first listed video for sharing
    Then App should share video
    When User trash a video
    Then Video should be moved to recycle bin
    When User undo trashing
    Then Video should be restored in browser

 @videos-player
 Scenario: Videos player usage
   When User selects the first listed video for playing
   Then Video playback preview is shown
   When User selects to play video
   Then App should ask device to play video from an available player app
   When User open video detail
   Then Video detail should be displayed