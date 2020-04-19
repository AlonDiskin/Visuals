Feature: Enable video playing

  In order to watch my device videos
  As the user
  I want to be able to play my videos

  acceptance criteria:
  -provide video preview when video selected for playing

  @show-video-preview
  Scenario Outline: Video preview is shown
    Given User has public video on device with a uri "<uri>"
    And Video was opened in player screen
    Then Video preview is shown
    Examples:
      | uri      |
      | vid uri  |