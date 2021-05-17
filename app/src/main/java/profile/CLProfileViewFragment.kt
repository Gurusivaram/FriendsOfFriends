package profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import authentication.CLAuthenticationActivity
import com.example.contactlistapp.R
import com.example.contactlistapp.databinding.ClFragmentProfileViewBinding
import common.CLUtil
import profile.viewModel.CLProfileViewViewModel
import retrofit.models.CLUsers
import java.io.File

class CLProfileViewFragment : CLProfileFragment() {
    companion object {
        private const val TAG = "CLProfileViewFragment"
    }

    private lateinit var profileViewBinding: ClFragmentProfileViewBinding
    private lateinit var profileViewViewModel: CLProfileViewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewViewModel = ViewModelProvider(this).get(CLProfileViewViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileViewBinding = ClFragmentProfileViewBinding.inflate(layoutInflater)
        return profileViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        if (savedInstanceState == null) {
            initData()
        }
        initClickListeners()
    }

    private fun initObservers() {
        with(profileViewViewModel) {
            user.observe(viewLifecycleOwner, {
                val name =
                    "${CLUtil.getCapitalized(it.firstName)} ${CLUtil.getCapitalized(it.lastName)}"
                profileViewBinding.apply {
                    tvName.text = name
                    tvCompany.text = getString(R.string.mallow_technology)
                    tvPhone.text = it.phoneNumber
                    tvEmail.text = it.email
                    tvAddress.text = it.address
                    if (!it.avatar.isNullOrEmpty()) {
                        it.avatar?.let { it1 ->
                            try {
                                if (File(it1).exists()) {
                                    tvProfileInitial.visibility = View.INVISIBLE
                                    ivProfilePic.visibility = View.VISIBLE
                                    ivProfilePic.setImageURI(it1.toUri())
                                } else {
                                    useInitial(it)
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, e.toString())
                            }
                        }
                    } else {
                        useInitial(it)
                    }
                    if (profileActivityContext.getProfileViewModel().loggedUserEmail != it.email) {
                        tvEdit.visibility = View.GONE
                        tvLogout.visibility = View.GONE
                    } else {
                        tvEdit.visibility = View.VISIBLE
                        tvLogout.visibility = View.VISIBLE
                    }
                }
            })
            isLogoutSuccess.observe(viewLifecycleOwner, {
                if (it != null) {
                    Intent(profileActivityContext, CLAuthenticationActivity::class.java).apply {
                        startActivity(this)
                        profileActivityContext.finish()
                    }
                }
            })
            isErrorException.observe(viewLifecycleOwner, {
                it?.let {
                    alerts.showToast(it)
                }
            })
        }
    }

    private fun useInitial(item: CLUsers) {
        with(profileViewBinding) {
            ivProfilePic.visibility = View.INVISIBLE
            tvProfileInitial.visibility = View.VISIBLE
            tvProfileInitial.text = CLUtil.getInitials(item.firstName, item.lastName)
        }
    }

    private fun initData() {
        profileViewViewModel.getUserDetails(profileActivityContext.getProfileViewModel().email)
    }

    private fun initClickListeners() {
        profileViewBinding.apply {
            ivBackContactDetails.setOnClickListener {
                backToList()
            }
            tvBackContacts.setOnClickListener {
                backToList()
            }
            tvEdit.setOnClickListener {
                profileActivityContext.supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<CLProfileEditFragment>(R.id.root_fragment_container_profile)
                    addToBackStack(null)
                }
            }
            tvLogout.setOnClickListener {
                profileViewViewModel.logoutUser()
            }
        }
    }

    private fun backToList(){
        if (profileActivityContext.getProfileViewModel().isEdited) {
            profileActivityContext.setResult(RESULT_OK)
        }
        profileActivityContext.finish()
    }
}