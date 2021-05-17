package group.followers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.contactlistapp.databinding.ClFragmentFollowersBinding
import group.CLGroupFragment
import retrofit.models.CLUsers

class CLFollowersFragment : CLGroupFragment(),
    CLFollowersAdapter.OnItemClickListenerFollowersAdapter {
    private lateinit var followersBinding: ClFragmentFollowersBinding
    private lateinit var followersViewModel: CLFollowersViewModel
    private var followersAdapter: CLFollowersAdapter? = null
    private val refreshListener by lazy {
        SwipeRefreshLayout.OnRefreshListener {
            followersBinding.swipeToRefreshContactList.isRefreshing = true
            initData(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        followersViewModel = ViewModelProvider(this).get(CLFollowersViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        followersBinding = ClFragmentFollowersBinding.inflate(layoutInflater)
        followersBinding.swipeToRefreshContactList.setOnRefreshListener(refreshListener)
        return followersBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        initObserver()
        if (savedInstanceState == null) {
            initData()
        }
    }

    override fun onFollowClick(user: CLUsers, position: Int) {
        followersViewModel.followUser(user)
    }

    private fun initRecycler() {
        followersBinding.rvFollowers.apply {
            linearLayoutManager = LinearLayoutManager(groupActivityContext)
            layoutManager = linearLayoutManager
            adapter = followersAdapter
        }
    }

    private fun initObserver() {
        with(followersBinding) {
            with(followersViewModel) {
                followersViewModel.followers.observe(viewLifecycleOwner, {
                    it?.let {
                        if (followersAdapter == null) {
                            followersAdapter = CLFollowersAdapter(
                                it,
                                groupActivityContext,
                                this@CLFollowersFragment,
                                commonGroupViewModel.followings.value
                            )
                            rvFollowers.adapter = followersAdapter
                        } else {
                            followersAdapter?.updateList(it)
                        }
                    }
                    checkEmptyList()
                    followersBinding.swipeToRefreshContactList.isRefreshing = false
                })
                commonGroupViewModel.followings.observe(viewLifecycleOwner, {
                    followersAdapter?.updateFollowings(it)
                })
                isLoading.observe(viewLifecycleOwner, {
                    if (it == true) {
                        progressBar.visibility = View.VISIBLE
                    } else {
                        progressBar.visibility = View.INVISIBLE
                    }
                })
                isUserFollowSuccess.observe(viewLifecycleOwner, {
                    if (it != null) {
                        alerts.showToast(it)
                    }
                })
                isErrorException.observe(viewLifecycleOwner, {
                    it?.let {
                        alerts.showToast(it)
                    }
                })
                isErrorAPI.observe(viewLifecycleOwner, {
                    it?.let {
                        alerts.showToast(it)
                    }
                })
                followRequestState.observe(viewLifecycleOwner, {
                    if (it == true) {
                        followersBinding.progressBarFullScreen.visibility = View.VISIBLE
                        groupActivityContext.window.setFlags(
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        );
                    } else {
                        followersBinding.progressBarFullScreen.visibility = View.GONE
                        groupActivityContext.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                })
            }
        }
    }

    private fun initData(isRefreshed: Boolean = false) {
        commonGroupViewModel.getFollowingsList(isRefreshed)
        followersViewModel.getFollowersList(isRefreshed)
    }

    private fun checkEmptyList() {
        with(followersBinding) {
            if (followersAdapter?.itemCount == 0) {
                tvEmptyFollowers.visibility = View.VISIBLE
            } else {
                tvEmptyFollowers.visibility = View.GONE
            }
        }
    }
}