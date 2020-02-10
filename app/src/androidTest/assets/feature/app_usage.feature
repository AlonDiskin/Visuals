Feature: App usage workflows

  various scenarios that demonstrate e2e usage workflows
  in application

  Background:
    Given User launch app from device home screen

  Scenario: App preferences changed from default
    When User navigates to default values settings screen
    Then App visual theme should be set as day theme
    When User selects the night theme
    Then App theme should be changed to night

