# Fix Category Navigation and Implement Category Detail Screen

The user reported that clicking a category in the Home screen navigates to the "filter" screen (Search screen) instead of showing the list of wallpapers for that category. This plan implements a dedicated `CategoryDetailFragment` to display wallpapers by category.

## User Review Required

> [!IMPORTANT]
> A new `CategoryDetailFragment` will be created. This screen will display the category title and a list of wallpapers belonging to that category, similar to the Favorites screen.

## Proposed Changes

### UI Layer

#### [NEW] [fragment_category_detail.xml](file:///E:/Pion-Base/app/src/main/res/layout/fragment_category_detail.xml)
Create a new layout for the category detail screen, featuring a header with the category name and a back button, and a RecyclerView for the wallpapers.

#### [MODIFY] [nav_main.xml](file:///E:/Pion-Base/app/src/main/res/navigation/nav_main.xml)
- Add `categoryDetailFragment` to the navigation graph.
- Define an argument `categoryName` (String) for `categoryDetailFragment`.
- Add an action from `homeFragment` to `categoryDetailFragment`.

### Category Detail Feature

#### [NEW] [CategoryDetailFragment.kt](file:///E:/Pion-Base/app/src/main/java/pion/tech/pionbase/feature/categoryDetail/CategoryDetailFragment.kt)
Implement the fragment extending `BaseFragment`. It will observe the UI state and display the list of wallpapers.

#### [NEW] [CategoryDetailFragmentEx.kt](file:///E:/Pion-Base/app/src/main/java/pion/tech/pionbase/feature/categoryDetail/CategoryDetailFragmentEx.kt)
Implement extension functions for UI initialization and event handling (back button, wallpaper click).

#### [NEW] [CategoryDetailViewModel.kt](file:///E:/Pion-Base/app/src/main/java/pion/tech/pionbase/feature/categoryDetail/CategoryDetailViewModel.kt)
Implement the ViewModel extending `BaseViewModel`. It will use `GetWallpapersByCategoryUseCase` to fetch wallpapers.

### Integration

#### [MODIFY] [HomeFragment.kt](file:///E:/Pion-Base/app/src/main/java/pion/tech/pionbase/feature/home/HomeFragment.kt)
Update `onClickCategory` to navigate to `categoryDetailFragment` instead of `searchFragment`.

#### [MODIFY] [ViewModelModule.kt](file:///E:/Pion-Base/app/src/main/java/pion/tech/pionbase/di/ViewModelModule.kt)
Register `CategoryDetailViewModel` in the Koin module.

## Verification Plan

### Automated Tests
- N/A (UI-driven logic)

### Manual Verification
1. Open the app and go to the Home screen.
2. Select the "Categories" tab.
3. Click on any category (e.g., "Nature").
4. Verify that the app navigates to a new screen showing the "Nature" title and a list of wallpapers.
5. Verify that clicking a wallpaper in this list navigates to the Wallpaper Detail screen.
6. Verify that the back button returns to the Home screen.
