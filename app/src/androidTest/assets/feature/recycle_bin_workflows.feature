Feature: Media items Recycle bin

  In order to collect media items for potential deletion
  As the user
  I want to be able to move media items to recycle bin

  Background:
    Given User has trashed items from device videos and pictures
    And User launch app from device home screen
    When User navigates to recycle bin screen

  @browse-trashed-items
  Scenario: Trashed items are listed
    Then All trashed items should be shown sorted by trashing date in desc order
    When User filters items to show only trashed pictures
    Then Only trashed pictures should be displayed

  @restore-trashed
  Scenario: User restores trashed items
    When User restore all items in trash
    Then All items should be restored
