Feature: Enable Videos trashing

  In order to aggregate video for deletion
  As the user
  I want to be able to move videos to app recycle bin

  acceptance criteria:
  - allow user to select videos from browser to be trashed to recycle bin
  - provide 'undo' option to quickly restore trashed videos

  Background:
    Given User has public videos on device
      | video_uri   | duration | date_added           |
      | uri_1       | 01:45    | 11 May 2020 06:12    |
      | uri_2       | 00:15    | 22 March 2020 12:34  |
      | uri_3       | 00:32    | 7 June 2020 19:25    |
      | uri_4       | 01:02    | 6 January 2020 23:02 |

  @trash-selected-videos
  Scenario: User trash selected videos
    And User open videos browser screen
    Then Videos should be displayed by date added,in descending order
      | video_uri   | duration |
      | uri_3       | 00:32    |
      | uri_1       | 01:45    |
      | uri_2       | 00:15    |
      | uri_4       | 01:02    |
    When User selects the next videos
      | video_uri   |
      | uri_2       |
      | uri_3       |
    And Select to trash them
    Then Browser should require additional user confirmation for trashing
    When User approve trashing
    Then Selected trashed videos should be moved to app recycle bin
    And Videos browser should update shown videos,to exclude trashed videos
      | video_uri   | duration |
      | uri_1       | 01:45    |
      | uri_4       | 01:02    |

  @update-restored
  Scenario: Restored videos are re displayed
    And Videos in app recycle bin
      | video_uri   |
      | uri_1       |
      | uri_4       |
    And User open videos browser screen
    When Trashed videos are restored in recycle bin
    Then Videos browser should update shown videos,to include restored videos
      | video_uri   | duration |
      | uri_3       | 00:32    |
      | uri_1       | 01:45    |
      | uri_2       | 00:15    |
      | uri_4       | 01:02    |

   @undo-trashing
   Scenario: User undo videos trashing
     And User open videos browser screen
     When User selects the next videos
       | video_uri   |
       | uri_4       |
       | uri_2       |
     And Trash them
     When User undo trashing
     Then Videos browser should restore trashed from recycle bin
     And All device videos should be by date added,in descending order
       | video_uri   | duration |
       | uri_3       | 00:32    |
       | uri_1       | 01:45    |
       | uri_2       | 00:15    |
       | uri_4       | 01:02    |

