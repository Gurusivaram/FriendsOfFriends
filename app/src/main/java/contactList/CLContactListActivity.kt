package contactList

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.contactlistapp.databinding.ClActivityContactListBinding
import common.CLActivity
import common.CLInternetMonitor
import common.CLPagination
import group.CLGroupActivity
import profile.CLProfileActivity
import retrofit.models.CLUsers
import java.util.*

class CLContactListActivity : CLActivity(), CLContactListAdapter.OnItemClickListener {
    companion object {
        const val USER_INTENT_KEY = "user_intent"
        const val LOGGED_USER_INTENT_KEY = "logged_user_intent"
        const val VIEW_CODE = 1000
    }

    private lateinit var contactListBinding: ClActivityContactListBinding
    private lateinit var contactListViewModel: CLContactListViewModel
    private var contactListAdapter: CLContactListAdapter? = null
    private val linearLayoutManager by lazy {
        LinearLayoutManager(this)
    }
    private val refreshListener by lazy {
        SwipeRefreshLayout.OnRefreshListener {
            contactListBinding.swipeToRefreshContactList.isRefreshing = true
            initData(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactListBinding = ClActivityContactListBinding.inflate(layoutInflater)
        contactListViewModel = ViewModelProvider(this).get(CLContactListViewModel::class.java)
        setContentView(contactListBinding.root)
        CLInternetMonitor.connectivityReceiverListener = this
        registerReceiver(internetMonitor, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        contactListBinding.swipeToRefreshContactList.setOnRefreshListener(refreshListener)
        initRecycler()
        initObservers()
        if (savedInstanceState == null) {
            initData()
        }
        initTextWatchers()
        initCLickListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(internetMonitor)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == VIEW_CODE) {
            contactListViewModel.getUpdatedLoggedUser()
        }
    }

    override fun onItemClicked(user: CLUsers) {
        Intent(this, CLProfileActivity::class.java).apply {
            putExtra(USER_INTENT_KEY, user.email)
            putExtra(LOGGED_USER_INTENT_KEY, contactListViewModel.loggedUserEmail)
            startActivityForResult(this, VIEW_CODE)
        }
    }

    override fun onFollowClick(user: CLUsers, position: Int) {
        contactListViewModel.followUser(user)
    }

    private fun initRecycler() {
        contactListBinding.rvContactList.layoutManager = linearLayoutManager
    }

    private fun initObservers() {
        with(contactListViewModel) {
            contacts.observe(this@CLContactListActivity, {
                it?.let {
                    if (contactListAdapter == null) {
                        contactListAdapter = CLContactListAdapter(
                            it,
                            this@CLContactListActivity,
                            followings.value,
                            loggedUserEmail
                        )
                        contactListBinding.rvContactList.adapter = contactListAdapter
                    } else {
                        contactListAdapter?.updateList(it)
                    }
                }
                checkForEmptyList()
                contactListBinding.swipeToRefreshContactList.isRefreshing = false
            })
            searchResult.observe(this@CLContactListActivity, {
                if (it != null) {
                    contactListAdapter?.updateList(it.toMutableList())
                }
                checkForEmptyList()
            })
            followings.observe(this@CLContactListActivity, {
                contactListAdapter?.updateFollowings(it)
            })
            isLoading.observe(this@CLContactListActivity, {
                if (it == true) {
                    contactListBinding.progressBar.visibility = View.VISIBLE
                } else {
                    contactListBinding.progressBar.visibility = View.GONE
                }
            })
            isUserFollowSuccess.observe(this@CLContactListActivity, {
                if (it != null) {
                    alerts.showToast(it)
                }
            })
            isErrorException.observe(this@CLContactListActivity, {
                it?.let {
                    alerts.showToast(it)
                }
            })
            isErrorAPI.observe(this@CLContactListActivity, {
                it?.let {
                    alerts.showToast(it)
                }
            })
            isUserUpdated.observe(this@CLContactListActivity, {
                if (it == true) {
                    contactListAdapter?.notifyItemChanged(0)
                }
            })
            followRequestState.observe(this@CLContactListActivity, {
                if (it == true) {
                    contactListBinding.progressBarFullScreen.visibility = View.VISIBLE
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }else{
                    contactListBinding.progressBarFullScreen.visibility = View.GONE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            })
        }
    }

    private fun initData(isRefreshed: Boolean = false) {
        with(contactListViewModel) {
            getFollowingsList()
            getList(isRefreshed)
        }
    }

    private fun initTextWatchers() {
        with(contactListViewModel) {
            contactListBinding.apply {
                svContactList.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (query != null) {
                            searchFollowers(query)
                        }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText.isNullOrEmpty()) {
                            contactListAdapter?.applySearchResult(contacts.value)
                        }
                        checkForEmptyList()
                        return true
                    }
                })
            }
        }
    }

    private fun initCLickListeners() {
        with(contactListBinding) {
            rvContactList.addOnScrollListener(object :
                CLPagination(linearLayoutManager) {
                override fun loadMore() {
                    if (contactListViewModel.searchResult.value.isNullOrEmpty()) {
                        contactListViewModel.getList()
                    }
                }
            })
        }
        contactListBinding.tvGroups.setOnClickListener {
            Intent(this, CLGroupActivity::class.java).apply {
                putExtra(LOGGED_USER_INTENT_KEY, contactListViewModel.loggedUserName)
                startActivity(this)
            }
        }
    }

    private fun checkForEmptyList() {
        with(contactListBinding) {
            if (contactListAdapter?.itemCount == 0) {
                tvEmptyList.visibility = View.VISIBLE
            } else {
                tvEmptyList.visibility = View.GONE
            }
        }
    }
}