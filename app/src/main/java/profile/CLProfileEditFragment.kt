package profile

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import com.example.contactlistapp.R
import com.example.contactlistapp.databinding.ClFragmentProfileEditBinding
import common.CLUtil
import profile.viewModel.CLProfileEditViewModel
import retrofit.models.CLUsers
import java.io.File

class CLProfileEditFragment : CLProfileFragment() {
    companion object {
        const val PERMISSION_CODE_CAMERA = 5000
        const val PERMISSION_CODE_STORAGE = 6000
        const val REQUEST_CODE_CAMERA = 3000
        const val REQUEST_CODE_GALLERY = 4000
        const val TYPE_IMAGE = "image/"
        const val CHOOSER_FRAGMENT_TAG = "CHOOSER"
        const val TAG = "CLProfileEditFragment"
    }

    private lateinit var profileEditBinding: ClFragmentProfileEditBinding
    private lateinit var profileEditViewModel: CLProfileEditViewModel
    private val chooser by lazy {
        CLEditContactChooserFragment.newInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileEditViewModel = ViewModelProvider(this).get(CLProfileEditViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileEditBinding = ClFragmentProfileEditBinding.inflate(layoutInflater)
        return profileEditBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        if (savedInstanceState == null) {
            initData()
        }
        initTextWatchers()
        initClickListeners()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when {
            grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED -> {
                if (requestCode == PERMISSION_CODE_CAMERA) {
                    openCamera()
                } else if (requestCode == PERMISSION_CODE_STORAGE) {
                    pickImageFromGallery()
                }
            }
            requestCode == PERMISSION_CODE_STORAGE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) -> {
                alerts.showToast(getString(R.string.alert_storage_permission_denied))
            }
            requestCode == PERMISSION_CODE_CAMERA && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(
                Manifest.permission.CAMERA
            ) -> {
                alerts.showToast(getString(R.string.alert_camera_permission_denied))
            }
            else -> {
                if (requestCode == PERMISSION_CODE_STORAGE) {
                    alerts.showAlertDialog(getString(R.string.alert_storage_permission_denied_permanently))
                } else if (requestCode == PERMISSION_CODE_CAMERA) {
                    alerts.showAlertDialog(getString(R.string.alert_camera_permission_denied_permanently))
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CAMERA -> {
                    profileEditBinding.ivProfilePic.setImageURI(profileEditViewModel.saveImage())
                }
                REQUEST_CODE_GALLERY -> {
                    val uri = data?.data
                    if (uri != null) {
                        profileEditViewModel.profileImageURI = uri
                        profileEditBinding.ivProfilePic.setImageURI(profileEditViewModel.saveImage())
                    }
                }
            }
        }
    }

    private fun initObservers() {
        with(profileEditViewModel) {
            user.observe(viewLifecycleOwner, {
                with(profileEditBinding) {
                    tieFirstName.setText(CLUtil.getCapitalized(it.firstName))
                    tieLastName.setText(CLUtil.getCapitalized(it.lastName))
                    cpCcp.resetToDefaultCountry()
                    tiePhone.setText(it.phoneNumber)
                    tieAddress.setText(it.address)
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
                }
            })
            isProfileEditSuccess.observe(viewLifecycleOwner, {
                if (it == true) {
                    navigateToProfileView()
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
        with(profileEditBinding) {
            ivProfilePic.visibility = View.INVISIBLE
            tvProfileInitial.visibility = View.VISIBLE
            tvProfileInitial.text = CLUtil.getInitials(item.firstName, item.lastName)
        }
    }

    private fun initData() {
        profileEditViewModel.populateData(profileActivityContext.getProfileViewModel().email)
    }

    private fun checkPermission(requestCode: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context?.let {
                if (requestCode == PERMISSION_CODE_CAMERA) {
                    if (ActivityCompat.checkSelfPermission(
                            it,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_DENIED
                    ) {
                        val neededPermissions = arrayOf(Manifest.permission.CAMERA)
                        requestPermissions(neededPermissions, requestCode)
                        return false
                    }
                    return true
                } else if (requestCode == PERMISSION_CODE_STORAGE) {
                    if (ActivityCompat.checkSelfPermission(
                            it,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_DENIED
                    ) {
                        val neededPermissions = arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        requestPermissions(neededPermissions, requestCode)
                        return false
                    }
                    return true
                }
            }
        }
        return true
    }

    private fun initTextWatchers() {
        with(profileEditViewModel) {
            with(profileEditBinding) {
                tieFirstName.doAfterTextChanged {
                    user.value?.firstName = it.toString()
                }
                tieLastName.doAfterTextChanged {
                    user.value?.lastName = it.toString()
                }
                tiePhone.doAfterTextChanged {
                    user.value?.phoneNumber = it.toString()
                }
                tieAddress.doAfterTextChanged {
                    user.value?.address = it.toString()
                }
            }
        }
    }

    private fun initClickListeners() {
        with(profileEditBinding) {
            tvSave.setOnClickListener {
                profileActivityContext.getProfileViewModel().isEdited = true
                profileEditViewModel.saveUser()
            }
            tvCancel.setOnClickListener {
                navigateToProfileView()
            }
            tvEditPic.setOnClickListener {
                chooser.show(profileActivityContext.supportFragmentManager, CHOOSER_FRAGMENT_TAG)
            }
        }
        chooser.listener = {
            when (it) {
                R.id.iv_camera -> {
                    if (checkPermission(PERMISSION_CODE_CAMERA)) {
                        openCamera()
                    }
                }
                R.id.iv_gallery -> {
                    if (checkPermission(PERMISSION_CODE_STORAGE)) {
                        pickImageFromGallery()
                    }
                }
            }
            chooser.dismiss()
        }
    }

    private fun navigateToProfileView() {
        profileActivityContext.supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<CLProfileViewFragment>(R.id.root_fragment_container_profile)
            addToBackStack(null)
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.apply {
            put(MediaStore.Images.Media.TITLE, profileEditViewModel.userName)
            put(MediaStore.Images.Media.DESCRIPTION, profileEditViewModel.userName)
        }
        profileActivityContext.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
            ?.let {
                profileEditViewModel.profileImageURI = it
            }
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, profileEditViewModel.profileImageURI)
            startActivityForResult(this, REQUEST_CODE_CAMERA)
        }
    }

    private fun pickImageFromGallery() {
        Intent(Intent.ACTION_PICK).apply {
            type = TYPE_IMAGE
            startActivityForResult(this, REQUEST_CODE_GALLERY)
        }
    }
}