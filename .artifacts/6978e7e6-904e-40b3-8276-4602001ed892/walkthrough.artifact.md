# Walkthrough: Refactor MainActivity to follow MVVM Architecture

## Changes Made

- Removed `dataStoreRepository` injection from `MainActivity`.
- Removed `LanguageManager.init` call from `MainActivity.onCreate`.
- Moved `LanguageManager` initialization to `MyApplication` to ensure it runs during app startup, following the architecture better by avoiding direct repository usage in the UI layer (MainActivity).

## Validation Results

- Code compiles successfully.
- Dependency on `DataStoreRepository` removed from `MainActivity`.
- Language initialization moved to application lifecycle as requested.
