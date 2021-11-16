package com.rita.calendarprooo

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.rita.calendarprooo.databinding.ActivityMainBinding
import com.rita.calendarprooo.ext.getVmFactory
import com.rita.calendarprooo.login.UserManager
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rita.calendarprooo.databinding.NavHeaderBinding

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var userToken = UserManager.userToken
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    val viewModel by viewModels<MainViewModel> { getVmFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(
            this, R.layout.activity_main
        )

        val toolbar = binding.myToolbar
        setSupportActionBar(toolbar)

        val actionbar = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.menu)
        }

        drawerLayout = binding.drawerLayout
        val navController = this.findNavController(R.id.myNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, bundle: Bundle? ->
            if (nd.id == nc.graph.startDestination) {
                // prevent nav gesture if not on start destination
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                toolbar.visibility = View.VISIBLE
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
        NavigationUI.setupWithNavController(binding.navView, navController)


        viewModel.navigateToHome.observe(this, Observer {
            if (it == true) {
                findNavController(R.id.myNavHostFragment)
                    .navigate(NavigationDirections.navigateToHomeFragment())
                toolbar.visibility = View.VISIBLE
                viewModel.doneNavigated()
            }
        })

        viewModel.navigateToLogin.observe(this, Observer {
            if (it == true) {
                findNavController(R.id.myNavHostFragment)
                    .navigate(NavigationDirections.navigateToLoginFragment())
                toolbar.visibility = View.GONE
                viewModel.doneNavigated()
            }
        })



        auth = Firebase.auth
        var currentUser = auth.currentUser
        Log.i(
            "Rita",
            "MainActivity currentUser: ${currentUser} , userToken: ${UserManager.userToken}"
        )
        if (currentUser == null) {
            //login first
            if (!UserManager.isLoggedIn) {
                viewModel.navigateToLogin.value = true
            } else {
                firebaseAuthWithGoogle(userToken!!)
            }
        } else {
            UserManager.userToken?.let { viewModel.getUserData(it) }
            viewModel.navigateToHome.value = true
        }

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        val navigationView = findViewById<NavigationView>(R.id.navView)

        // we have to bind the viewModel manually to the header-view
        // since NavigationView does not support databinding by itself
        val headerView = navigationView.getHeaderView(0)
        val headerViewBinding = NavHeaderBinding.bind(headerView)
        headerViewBinding.viewModel = viewModel
        // this new line is important to get notifications about livedata-changes!!!
        headerViewBinding.lifecycleOwner = this

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    Log.d(TAG, "signInWithCredential:success with user email: ${user!!.email}")
                    UserManager.userToken?.let { viewModel.getUserData(it) }
                    viewModel.navigateToLogin.value = true
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }


}
