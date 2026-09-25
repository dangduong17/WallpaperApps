package pion.tech.pionbase.base.navigator

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions

class NavigatorImpl(
    private val navController: NavController,
    private val lifecycle: Lifecycle,
    private val currentDestinationId: Int,
) : Navigator {
    private var navObserver: LifecycleEventObserver? = null

    private fun isAtCurrentDestination(): Boolean = navController.currentDestination?.id == currentDestinationId

    private fun safeAction(action: () -> Unit) {
        if (!isAtCurrentDestination()) return
        runCatching {
            navObserver =
                object : LifecycleEventObserver {
                    override fun onStateChanged(
                        source: LifecycleOwner,
                        event: Lifecycle.Event,
                    ) {
                        if (event == Lifecycle.Event.ON_RESUME) {
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
                        destination: NavDestination,
                        arguments: Bundle?,
                    ) {
                        if (destination.id != currentDestinationId) {
                            navController.removeOnDestinationChangedListener(this)
                            lifecycle.removeObserver(navObserver as LifecycleEventObserver)
                        }
                    }
                },
            )

            if (navController.currentDestination?.id == currentDestinationId) {
                action()
            }
        }
    }

    override fun getCurrentDestinationId(): Int = navController.currentDestination?.id ?: 0

    override fun navigateTo(
        actionId: Int,
        bundle: Bundle?,
    ) {
        safeNavigate(actionId, bundle)
    }

    override fun navigateTo(directions: NavDirections) {
        safeNavigate(directions)
    }

    // [ADDED BY AI]: Cài đặt điều hướng an toàn độc lập không qua safeAction
    override fun safeNavigate(directions: NavDirections) {
        try {
            navController.navigate(directions)
        } catch (e: Exception) {
            timber.log.Timber.e(e, "safeNavigate failed for directions: $directions")
        }
    }

    // [ADDED BY AI]: Cài đặt điều hướng an toàn độc lập không qua safeAction
    override fun safeNavigate(actionId: Int, bundle: Bundle?) {
        try {
            navController.navigate(actionId, bundle)
        } catch (e: Exception) {
            timber.log.Timber.e(e, "safeNavigate failed for actionId: $actionId")
        }
    }

    // [ADDED BY AI]: Cài đặt quay lại an toàn độc lập không qua safeAction
    override fun safeNavigateUp() {
        try {
            navController.navigateUp()
        } catch (e: Exception) {
            timber.log.Timber.e(e, "safeNavigateUp failed")
        }
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

    override fun navigateUp() {
        safeAction { navController.navigateUp() }
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
        safeAction { navController.popBackStack(destinationId, inclusive) }
    }
}
