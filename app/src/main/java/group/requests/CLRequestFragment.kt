package group.requests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.contactlistapp.R
import com.example.contactlistapp.databinding.ClFragmentRequestsBinding
import group.CLGroupFragment
import retrofit.models.CLUsers

class CLRequestFragment : CLGroupFragment(), CLRequestAdapter.OnRequestActionListener {
    companion object {
        private const val ACCEPT = "Accept"
        private const val DECLINE = "Decline"
    }

    private lateinit var requestsBinding: ClFragmentRequestsBinding
    private lateinit var requestsViewModel: CLRequestsViewModel
    private var requestAdapter: CLRequestAdapter? = null
    private val refreshListener by lazy {
        SwipeRefreshLayout.OnRefreshListener {
            requestsBinding.swipeToRefreshContactList.isRefreshing = true
            initData(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestsViewModel = ViewModelProvider(this).get(CLRequestsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requestsBinding = ClFragmentRequestsBinding.inflate(layoutInflater)
        requestsBinding.swipeToRefreshContactList.setOnRefreshListener(refreshListener)
        return requestsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        initObserver()
        if (savedInstanceState == null) {
            initData()
        }
    }
    override fun onAccept(user: CLUsers, position: Int) {
        requestsViewModel.actionRequest(user, position, ACCEPT)
    }

    override fun onDecline(user: CLUsers, position: Int) {
        requestsViewModel.actionRequest(user, position, DECLINE)
    }

    private fun initRecycler() {
        requestsBinding.rvRequests.apply {
            linearLayoutManager = LinearLayoutManager(groupActivityContext)
            layoutManager = linearLayoutManager
            adapter = requestAdapter
        }
    }

    private fun initObserver() {
        with(requestsBinding) {
            with(requestsViewModel) {
                requests.observe(viewLifecycleOwner, {
                    it?.let {
                        if (requestAdapter == null) {
                            requestAdapter =
                                CLRequestAdapter(it, groupActivityContext, this@CLRequestFragment)
                            rvRequests.adapter = requestAdapter
                        } else {
                            requestAdapter?.updateList(it)
                        }
                    }
                    checkEmptyList()
                    requestsBinding.swipeToRefreshContactList.isRefreshing = false
                })
                isLoading.observe(viewLifecycleOwner, {
                    if (it == true) {
                        progressBar.visibility = View.VISIBLE
                    } else {
                        progressBar.visibility = View.INVISIBLE
                    }
                })
                isRequestSuccess.observe(viewLifecycleOwner, {
                    if (it != -1 && it != null) {
                        requestAdapter?.removedAtPosition(it)
                        alerts.showToast(getString(R.string.request_success))
                    }
                })
                isErrorException.observe(viewLifecycleOwner, {
                    it?.let {
                        alerts.showToast(it)
                    }
                })
            }
        }
    }

    private fun initData(isRefreshed: Boolean = false) {
        requestsViewModel.getFollowRequestsList(isRefreshed)
    }

    private fun checkEmptyList() {
        with(requestsBinding) {
            if (requestAdapter?.itemCount == 0) {
                tvEmptyRequest.visibility = View.VISIBLE
            } else {
                tvEmptyRequest.visibility = View.GONE
            }
        }
    }
}