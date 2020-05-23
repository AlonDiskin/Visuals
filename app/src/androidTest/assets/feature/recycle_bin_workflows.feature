Feature: Media items Recycle bin

  In order to collect media items for potential deletion
  As the user
  I want to be able to move media items to recycle bin

  @browse-trashed-items
  Scenario: Trashed items are listed
    Given User has trashed items from device videos and pictures
    And User launch app from device home screen
    When User navigates to recycle bin screen
    Then All trashed items should be shown sorted by trashing date in desc order
