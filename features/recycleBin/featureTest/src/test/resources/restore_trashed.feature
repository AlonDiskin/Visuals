Feature: Enable trashed items restoration

  In order to return media items from recycle bin to normal app usage
  As the user
  I want to be able to restore trashed items

  acceptance criteria:
  - provide trashed item selection for restoration to
  regular browsed media item in app(multi select,restore all)

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

  @restore-selected
  Scenario: User restore selected items
    And Selects the next trashed items
      | uri  |
      | uri_1|
      | uri_2|
    And User restore selected items
    Then Restored items should be removed from recycle bin
      | uri  |
      | uri_1|
      | uri_2|
    Then Only trashed items should be shown sorted by trashing date in desc order
      | type  | uri  |
      | image | uri_4|
      | video | uri_3|

  @restore-all
  Scenario: User restores all trash items
    When User selects to restore all shown items
    Then All all restored items should be removed form app recycle bin storage
    And Recycle bin browser screen should remove all displayed items