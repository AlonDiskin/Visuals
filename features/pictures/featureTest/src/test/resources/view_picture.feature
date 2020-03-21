Feature: Enable picture viewing

  In order to see up close device pictures
  As the user
  I want to be able to view my pictures

  acceptance criteria:
  - open full view of existing picture
  - show picture detail:size,date added,path,resolution,and title

  @view-picture-happy-path
  Scenario Outline: User views public picture
    Given User has public pictures on device
      | date                  | size | path        | title           | width | height |
      | 13 March 2020 20:12   | 3.35 | DCIM/Camera | 20200313_23.jpg | 1200  | 600    |
      | 04 June 2019 07:34    | 4.78 | DCIM/Camera | 20190604_35.jpg | 800   | 960    |
    When User open "<selected>" picture in picture viewing screen
    Then Picture is shown in picture viewing screen
    And Selected picture detail is displayed
     | date                  | size   | path        | title           | resolution |
     | 13 March 2020 20:12   | 3.35MB | DCIM/Camera | 20200313_23.jpg | 1200*600   |
     | 04 June 2019 07:34    | 4.78MB | DCIM/Camera | 20190604_35.jpg | 800*960    |
    Examples:
      | selected |
      | 0        |
      | 1        |

  @view-picture-sad-path
  Scenario: Picture viewing fail
    Given User has opened picture viewing screen to view existing picture
    When App fail to load existing picture for viewing
    Then User should see an error message displayed