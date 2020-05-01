Feature: Enable video playing

  In order to watch my device videos
  As the user
  I want to be able to play my videos

  acceptance criteria:
  -provide video preview when video selected for playing
  -play video with device video player

  @show-video-preview
  Scenario Outline: Video preview is shown
    Given User has public video on device with a uri "<uri>"
    And User selects video from browser
    Then Video preview should be displayed in own screen
    Examples:
      | uri      |
      | vid uri  |

  @play-video-on-device
  Scenario Outline: Video is played in device player
    Given User has public video on device with a uri "<uri>"
    And User device has an available video player app
    When Video is opened in preview screen
    And User selects to play video
    Then Video should be played in device video player app
    Examples:
      | uri      |
      | vid uri  |




