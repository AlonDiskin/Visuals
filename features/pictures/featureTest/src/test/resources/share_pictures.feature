Feature: Enable pictures sharing

In order to share content with my friends
As the user
I want to be able to share pictures items from my device

Acceptance criteria:
-enable pictures sharing,as singles or by collective selection

Scenario Outline: User share pictures
  Given User has public pictures on device
  And User opened the pictures browser screen
  When User selects "<num_pictures>" of them
  When User share selected pictures
  Then Pictures browser should share the pictures
  Examples:
    | num_pictures |
    | 1            |
    | 2            |
    | 3            |