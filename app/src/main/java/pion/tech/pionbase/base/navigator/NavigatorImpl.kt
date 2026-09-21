package pion.tech.pionbase.base.navigator

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions

class NavigatorImpl(
    private val navController: NavController,
) : Navigator {

    override fun getCurrentDestinationId(): Int = navController.currentDestination?.id ?: 0

    override fun navigateTo(
        actionId: Int,
        bundle: Bundle?,
    ) {
        navController.navigate(actionId, bundle, null)
    }

    override fun navigateTo(
        actionId: Int,
        bundle: Bundle?,
        clearBackStack: Boolean,
    ) {
        val navOptions =
            if (clearBackStack) {
                NavOptions
                    .Builder()
                    .setPopUpTo(navController.graph.startDestinationId, true)
                    .build()
            } else {
                null
            }
        navController.navigate(actionId, bundle, navOptions)
    }

    override fun navigateTo(directions: androidx.navigation.NavDirections) {
        navController.navigate(directions)
    }

    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun addOnDestinationChangedListener(listener: NavController.OnDestinationChangedListener) {
        navController.addOnDestinationChangedListener(listener)
    }

    override fun removeOnDestinationChangedListener(listener: NavController.OnDestinationChangedListener) {
        navController.removeOnDestinationChangedListener(listener)
    }

    override fun isCameFrom(destinationId: Int): Boolean = navController.previousBackStackEntry?.destination?.id == destinationId

    override fun popBackStack(
        destinationId: Int,
        inclusive: Boolean,
    ) {
        navController.popBackStack(destinationId, inclusive)
    }
}
