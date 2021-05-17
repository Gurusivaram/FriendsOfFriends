package group.followings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.contactlistapp.databinding.ClFragmentFollowingBinding
import group.CLGroupFragment

class CLFollowingFragment : CLGroupFragment() {
    private lateinit var followingBinding: ClFragmentFollowingBinding
    private var followingAdapter: CLFollowingAdapter? = null
    private val refreshListener by lazy {
        SwipeRefreshLayout.OnRefreshListener {
            followingBinding.swipeToRefreshContactList.isRefreshing = true
            commonGroupViewModel.getFollowingsList(true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        followingBinding = ClFragmentFollowingBinding.inflate(layoutInflater)
        followingBinding.swipeToRefreshContactList.setOnRefreshListener(refreshListener)
        return followingBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        initObserver()
    }

    private fun initRecycler() {
        followingBinding.rvFollowing.apply {
            layoutManager = linearLayoutManager
            adapter = followingAdapter
        }
    }

    private fun initObserver() {
        with(followingBinding) {
            with(commonGroupViewModel) {
                followings.observe(viewLifecycleOwner, {
                    it?.let {
                        if (followingAdapter == null) {
                            followingAdapter = CLFollowingAdapter(it, groupActivityContext)
                            rvFollowing.adapter = followingAdapter
                        } else {
                            followingAdapter?.updateList(it)
                        }
                    }
                    checkEmptyList()
                    followingBinding.swipeToRefreshContactList.isRefreshing = false
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
                isLoading.observe(viewLifecycleOwner, {
                    if (it == true) {
                        progressBar.visibility = View.VISIBLE
                    } else {
                        progressBar.visibility = View.INVISIBLE
                    }
                })
            }
        }
    }

    private fun checkEmptyList() {
        with(followingBinding) {
            if (followingAdapter?.itemCount == 0) {
                tvEmptyFollowing.visibility = View.VISIBLE
            } else {
                tvEmptyFollowing.visibility = View.GONE
            }
        }
    }
}