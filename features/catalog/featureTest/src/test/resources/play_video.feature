Feature: Enable video playing

  In order to watch my device videos
  As the user
  I want to be able to play my videos

  acceptance criteria:
  -provide video preview when video selected for playing
  -play video with device video player
  -provide video detail info :size, date added,file path on device,format,duration

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

  @provide-video-info
  Scenario: Video detail is displayed
    Given User has public video on device
      | date                  | size   | path        | title           | width | height | duration | uri    |
      | 13 March 2020 20:12   | 138.35 | DCIM/Camera | 20200313_23.mp4 | 1920  | 600    | 00:54    | test 1 |
    And User selects video from browser
    When User select to view video info
    Then Video data should be displayed
      | date                  | size     | path        | title           | resolution | duration |
      | 13 March 2020 20:12   | 138.35MB | DCIM/Camera | 20200313_23.mp4 | 1920*600   | 00:54    |





