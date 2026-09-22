# Refactor MainActivity to follow MVVM Architecture

The goal is to remove direct repository access from `MainActivity` and move logic into `CommonViewModel` to comply with the project's 3-layer architecture.

## User Review Required

- The `LanguageManager.init` logic, which currently runs in `onCreate` of `MainActivity`, will be moved. I will ensure that the initialization order remains correct or handled appropriately within the app lifecycle.

## Open Questions

None.

## Proposed Changes

### App Module

#### [MODIFY] [MainActivity.kt](file:///E:/Pion-Base/app/src/main/java/pion/tech/pionbase/app/MainActivity.kt)
- Remove `dataStoreRepository` injection.
- Remove `LanguageManager.init` call from `onCreate`.

#### [MODIFY] [CommonViewModel.kt](file:///E:/Pion-Base/app/src/main/java/pion/tech/pionbase/app/CommonViewModel.kt)
- Inject necessary UseCases or keep logic encapsulated within this ViewModel to handle language initialization if needed, or trigger it from `Application` class if more appropriate.

## Verification Plan

### Automated Tests
- Run existing tests to ensure no regression.

### Manual Verification
- Launch the app and verify that language settings are still applied correctly.
