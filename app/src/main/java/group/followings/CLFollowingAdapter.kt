package group.followings

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.contactlistapp.databinding.ClItemContactsBinding
import common.CLUtil
import retrofit.models.CLUsers
import java.io.File

class CLFollowingAdapter(
    private var following: MutableList<CLUsers>,
    private var context: Context
) : RecyclerView.Adapter<CLFollowingAdapter.ViewHolder>() {
    companion object {
        private const val TAG = "CLFollowingAdapter"
    }

    inner class ViewHolder(private val binding: ClItemContactsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CLUsers) {
            with(binding) {
                tvFirstName.text = CLUtil.getCapitalized(item.firstName)
                tvLastName.text = CLUtil.getCapitalized(item.lastName)
                if (!item.avatar.isNullOrEmpty()) {
                    item.avatar?.let { it1 ->
                        try {
                            if (File(it1).exists()) {
                                tvProfileInitial.visibility = View.INVISIBLE
                                ivProfilePic.visibility = View.VISIBLE
                                Glide.with(context).load(item.avatar).into(ivProfilePic)
                            } else {
                                useInitial(item)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, e.toString())
                        }
                    }
                } else {
                    useInitial(item)
                }
                tvFollow.visibility = View.GONE
            }
        }

        private fun useInitial(item: CLUsers) {
            with(binding) {
                ivProfilePic.visibility = View.INVISIBLE
                tvProfileInitial.visibility = View.VISIBLE
                tvProfileInitial.text = CLUtil.getInitials(item.firstName, item.lastName)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ClItemContactsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(following[holder.adapterPosition])
    }

    override fun getItemCount(): Int = following.size

    fun updateList(list: MutableList<CLUsers>) {
        this.following = list
        notifyDataSetChanged()
    }
}