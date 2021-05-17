package contactList

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

class CLContactListAdapter(
    private var contacts: MutableList<CLUsers>,
    private val itemClickListener: OnItemClickListener,
    private var followings: MutableMap<String, CLUsers>?,
    private val loggedUserMail: String
) :
    RecyclerView.Adapter<CLContactListAdapter.ViewHolder>() {
    companion object {
        private const val TAG = "CLContactListAdapter"
    }

    interface OnItemClickListener {
        fun onItemClicked(user: CLUsers)

        fun onFollowClick(user: CLUsers, position: Int)
    }

    private val context by lazy {
        itemClickListener as Activity
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
                if (loggedUserMail.isNotEmpty() && item.email != loggedUserMail) {
                    tvFollow.visibility = View.VISIBLE
                    item.id?.let {
                        if (followings?.containsKey(item.email) == true) {
                            tvFollow.text = context.getString(R.string.following)
                        } else {
                            tvFollow.text = context.getString(R.string.follow)
                        }
                    }
                } else {
                    tvFollow.visibility = View.GONE
                }
                tvFollow.setOnClickListener {
                    itemClickListener.onFollowClick(item, position)
                }
            }
            itemView.setOnClickListener {
                itemClickListener.onItemClicked(item)
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

    override fun onBindViewHolder(holder: CLContactListAdapter.ViewHolder, position: Int) {
        holder.bind(contacts[holder.adapterPosition], holder.adapterPosition)
    }

    override fun getItemCount(): Int = contacts.size

    fun applySearchResult(filteredList: MutableList<CLUsers>?) {
        if (filteredList != null) {
            contacts = filteredList
        }
        notifyDataSetChanged()
    }

    fun updateList(list: MutableList<CLUsers>) {
        this.contacts = list
        notifyDataSetChanged()
    }

    fun updateFollowings(list: MutableMap<String, CLUsers>?) {
        this.followings = list
        notifyDataSetChanged()
    }
}