package pion.tech.pionbase.base.navigator

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions

class NavigatorImpl(
    private val navController: NavController,
    private val lifecycle: Lifecycle,
    private val currentDestinationId: Int,
) : Navigator {

    private var navObserver: androidx.lifecycle.LifecycleEventObserver? = null

    private fun safeAction(action: () -> Unit) {
        if (!isAtCurrentDestination()) return
        runCatching {
            navObserver =
                object : androidx.lifecycle.LifecycleEventObserver {
                    override fun onStateChanged(
                        source: androidx.lifecycle.LifecycleOwner,
                        event: androidx.lifecycle.Lifecycle.Event,
                    ) {
                        if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                            lifecycle.removeObserver(this)
                            runCatching {
                                if (navController.currentDestination?.id == currentDestinationId) {
                                    action()
                                }
                            }
                        }
                    }
                }
            lifecycle.addObserver(navObserver!!)

            navController.addOnDestinationChangedListener(
                object :
                    NavController.OnDestinationChangedListener {
                    override fun onDestinationChanged(
                        controller: NavController,
                        destination: androidx.navigation.NavDestination,
                        arguments: Bundle?,
                    ) {
                        if (destination.id != currentDestinationId) {
                            navController.removeOnDestinationChangedListener(this)
                            lifecycle.removeObserver(navObserver!!)
                        }
                    }
                },
            )

            if (navController.currentDestination?.id == currentDestinationId) {
                action()
            }
        }
    }

    private fun isAtCurrentDestination(): Boolean {
        return navController.currentDestination?.id == currentDestinationId
    }

    override fun getCurrentDestinationId(): Int = navController.currentDestination?.id ?: 0

    override fun navigateTo(
        actionId: Int,
        bundle: Bundle?,
    ) {
        safeAction { navController.navigate(actionId, bundle, null) }
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
        safeAction { navController.navigate(actionId, bundle, navOptions) }
    }

    override fun navigateTo(directions: NavDirections) {
        safeAction { navController.navigate(directions) }
    }

    override fun navigateUp() {
        // For navigateUp, we use the navController directly but still check lifecycle
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            runCatching {
                navController.navigateUp()
            }
        }
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
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            runCatching {
                navController.popBackStack(destinationId, inclusive)
            }
        }
    }
}
