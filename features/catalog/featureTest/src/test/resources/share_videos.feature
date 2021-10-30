Feature: Enable videos sharing

In order to share content with my friends
As the user
I want to be able to share videos items from my device

Acceptance criteria:
-enable videos sharing,as singles or by collective selection

Scenario Outline: User share videos
  Given User has public videos on device
  And User opened the videos browser screen
  When User selects "<num_videos>" of them
  When User share selected videos
  Then Videos browser should share the videos
  Examples:
    | num_videos |
    | 1          |
    | 2          |
    | 3          |