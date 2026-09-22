# Development Rules & PR Checklist - Pion-Base

This document defines the mandatory development rules for the **Pion-Base** project. All developers and AI agents must adhere to these rules.

## 1. Architectural Integrity (MVVM + Clean Architecture)

- **Domain Layer Isolation**:
    - UseCases are the **ONLY** way to access data.
    - ViewModels **NEVER** reference Repositories directly.
- **Base Class Usage**:
    - Always extend `BaseFragment`, `BaseViewModel`, `BaseListAdapter`, etc.
- **UI Logic Separation**:
    - UI initialization and click listeners must be in `{Feature}FragmentEx.kt`.
    - `{Feature}Fragment.kt` is strictly for state observation (`subscribeObserver`).

## 2. Coding Standards & Conventions

- **Click Listeners**: Use `setPreventDoubleClick` or `setPreventDoubleClickScaleView` for all view interactions.
- **Navigation**: Always use the `navigator` property (provided by `BaseFragment`) to navigate. **NEVER** use `findNavController()` directly.
- **Coroutines**: 
    - Use `launchIO`, `launchMain`, `launchDefault` from base classes.
    - **NEVER** use `viewModelScope.launch()` or `lifecycleScope.launch()` directly.
- **State Observation**:
    - Use `collectFlowOnView` with `map + distinctUntilChanged`.
    - **NEVER** collect flows directly from `lifecycleScope` inside `onCreateView`.

## 3. Localization & Hardcoding

- **No Hardcoded Strings**: All UI strings must be in `strings.xml`.
- **Constants**: Extract magic strings (like Bundle keys) to `companion object` constants within their respective files.

## 4. Dependencies & Imports

- **Koin**: Register dependencies strictly in the `di/` module files (`AppModule.kt`).
- **Clean Imports**: Keep imports organized. Remove unused imports before committing.

## 5. PR Checklist (Mandatory before submitting)

- [ ] Does the feature follow the 3-file pattern (`Fragment`, `FragmentEx`, `ViewModel`)?
- [ ] Are all strings extracted to `strings.xml`?
- [ ] Are all click listeners using `setPreventDoubleClick`?
- [ ] Is navigation handled via `navigator`?
- [ ] Are all background tasks using `launchIO` / `launchMain` / `launchDefault`?
- [ ] Are there any unused imports or redundant qualifiers?
- [ ] Is the feature logic called via a `UseCase`?

## 6. Testing Strategy

- **Unit Tests**: Mandatory for all `UseCase` classes to ensure logic correctness.
- **Flow/Integration Tests**: Required for critical user flows:
    - **Happy Path**: Successfully setting a wallpaper from Home screen.
    - **Crash Prevention**: Testing behavior when files are missing or permissions are denied.
    - **Stability**: Ensuring transitions between fragments (Navigation) do not crash on low-end devices.

---
*Failure to follow these rules will result in PR rejection.*
