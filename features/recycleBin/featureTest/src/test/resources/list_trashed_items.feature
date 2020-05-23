Feature: List all items currently in recycle bin

  In order to know which media items i have staged for removal
  As the user
  I want to have a listing of all trashed items in recycle bin

  acceptance criteria:
  - list all trashed items by date added in descending order

  Background:
    Given User has public media on device
      | type  | uri  |
      | video | uri_1|
      | image | uri_2|
      | video | uri_3|
      | image | uri_4|
      | image | uri_5|
      | video | uri_6|
    And User hes media items in app recycle bin
      | type  | uri  | trashed_date         |
      | video | uri_1| 13 May 2020 06:12    |
      | image | uri_2| 6 January 2020 20:02 |
      | video | uri_3| 22 March 2020 12:34  |
      | image | uri_4| 17 June 2020 09:22   |
    When User opens recycle bin screen
    Then Only trashed items should be shown sorted by trashing date in desc order
      | type  | uri  |
      | image | uri_4|
      | video | uri_1|
      | video | uri_3|
      | image | uri_2|

  @trashed-state-shown
  Scenario: Trashed items state are displayed to user
    When Items are removed from recycle bin
      | type  | uri  |
      | video | uri_1|
      | video | uri_3|
    And Items Are added
      | type  | uri  | trashed_date         |
      | image | uri_5| 22 June 2020 19:32   |
    Then Updated trash items list should be displayed
      | type  | uri  |
      | image | uri_5|
      | image | uri_4|
      | image | uri_2|

  @trashed-deleted-not-by-app
  Scenario: User delete trashed from device outside app
    When User deletes trashed item from device not via app
      | type  | uri  |
      | video | uri_1|
    Then Trashed items in recycle bin should be updated
      | type  | uri  |
      | image | uri_2|
      | video | uri_3|
      | image | uri_4|
    And Updated trashed state should be displayed sorted by trashing date in desc order
      | type  | uri  |
      | image | uri_4|
      | video | uri_3|
      | image | uri_2|

