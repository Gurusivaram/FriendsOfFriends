package profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.contactlistapp.databinding.ClFragmentPictureChooserBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CLEditContactChooserFragment : BottomSheetDialogFragment() {
    companion object {
        fun newInstance() = CLEditContactChooserFragment()
    }

    private lateinit var pictureChooserBinding: ClFragmentPictureChooserBinding
    var listener: (Int) -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        pictureChooserBinding = ClFragmentPictureChooserBinding.inflate(layoutInflater)
        with(pictureChooserBinding) {
            ivCamera.setOnClickListener {
                listener(it.id)
            }
            ivGallery.setOnClickListener {
                listener(it.id)
            }
        }
        return pictureChooserBinding.root
    }
}