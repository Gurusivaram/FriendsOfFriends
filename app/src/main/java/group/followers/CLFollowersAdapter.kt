package group.followers

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.contactlistapp.R
import com.example.contactlistapp.databinding.ClItemContactsBinding
import common.CLUtil
import retrofit.models.CLUsers
import java.io.File
import java.util.*

class CLFollowersAdapter(
    private var followers: MutableList<CLUsers>,
    private var context: Activity,
    private var itemClickListener: OnItemClickListenerFollowersAdapter,
    followingsData: MutableList<CLUsers>?
) : RecyclerView.Adapter<CLFollowersAdapter.ViewHolder>() {
    companion object {
        private const val TAG = "CLFollowersAdapter"
    }

    private var followings: MutableMap<Int, CLUsers> = mutableMapOf()

    init {
        followingsData?.forEach {
            it.id?.let { it1 -> followings[it1] = it }
        }
    }

    interface OnItemClickListenerFollowersAdapter {
        fun onFollowClick(user: CLUsers, position: Int)
    }


    inner class ViewHolder(private val binding: ClItemContactsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CLUsers, position: Int) {
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
                if (followings.containsKey(item.id)) {
                    tvFollow.text = context.getString(R.string.following)
                } else {
                    tvFollow.text = context.getString(R.string.follow)
                }
                tvFollow.setOnClickListener {
                    itemClickListener.onFollowClick(item, position)
                }
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
        holder.bind(followers[holder.adapterPosition], holder.adapterPosition)
    }

    override fun getItemCount(): Int = followers.size

    fun updateList(list: MutableList<CLUsers>) {
        this.followers = list
        notifyDataSetChanged()
    }

    fun updateFollowings(updatedFollowings: MutableList<CLUsers>) {
        updatedFollowings.forEach {
            it.id?.let { it1 -> followings[it1] = it }
        }
        notifyDataSetChanged()
    }
}