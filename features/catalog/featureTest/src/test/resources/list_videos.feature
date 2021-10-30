Feature: List user videos

  In order to browse all my public videos
  As the user
  I want to have a listing of all my videos

  acceptance criteria:
  - list all public videos listed by date in descending order

  @show-videos-happy-path
  Scenario: User public videos are shown
    Given User has public videos on device
    When User opens videos browser screen
    Then All videos are shown sorted by date in descending order
    When User removes a video from device
    Then Videos browser screen should update shown videos accordingly

  @show-videos-sad-path
  Scenario: App fail to fetch device videos
    Given User opens videos browser screen
    When App fail to fetch device videos
    Then User should see an error message displayed