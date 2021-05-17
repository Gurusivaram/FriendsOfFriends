package group

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.contactlistapp.databinding.ClActivityGroupBinding
import group.followers.CLFollowersFragment
import group.followings.CLFollowingFragment
import group.requests.CLRequestFragment

class CLGroupActivity : AppCompatActivity() {
    companion object {
        private const val REQUESTS_TAG = "Requests"
        private const val FOLLOWERS_TAG = "Followers"
        private const val FOLLOWINGS_TAG = "Followings"
        const val LOGGED_USER_INTENT_KEY = "logged_user_intent"
    }

    private lateinit var groupBinding: ClActivityGroupBinding
    private lateinit var groupViewModel: CLGroupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupBinding = ClActivityGroupBinding.inflate(layoutInflater)
        groupViewModel = ViewModelProvider(this).get(CLGroupViewModel::class.java)
        setContentView(groupBinding.root)
        initViewPager()
        if (savedInstanceState == null) {
            initData()
        }
        initCLickListeners()
    }

    private fun initData() {
        intent.getStringExtra(LOGGED_USER_INTENT_KEY)?.let {
            groupViewModel.loggedUser = it
            groupBinding.tvTitleGroup.text = it
        }
    }

    private fun initViewPager() {
        val adapter = CLViewPagerAdapter(supportFragmentManager)
        adapter.also {
            it.addFragment(CLRequestFragment(), REQUESTS_TAG)
            it.addFragment(CLFollowersFragment(), FOLLOWERS_TAG)
            it.addFragment(CLFollowingFragment(), FOLLOWINGS_TAG)
        }
        with(groupBinding) {
            viewPager.adapter = adapter
            tabs.setupWithViewPager(viewPager)
        }
    }

    private fun initCLickListeners() {
        groupBinding.btnCancel.setOnClickListener {
            finish()
        }
    }
}