package group.requests

import android.app.Activity
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

class CLRequestAdapter(
    private var requests: MutableList<CLUsers>,
    private var context: Activity,
    private var itemClickListener: OnRequestActionListener
) : RecyclerView.Adapter<CLRequestAdapter.ViewHolder>() {
    companion object {
        private const val TAG = "CLRequestAdapter"
    }

    interface OnRequestActionListener {
        fun onAccept(user: CLUsers, position: Int)
        fun onDecline(user: CLUsers, position: Int)
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
                tvFollow.visibility = View.GONE
                ivAccept.visibility = View.VISIBLE
                ivDecline.visibility = View.VISIBLE
                ivAccept.setOnClickListener {
                    itemClickListener.onAccept(item, position)
                }
                ivDecline.setOnClickListener {
                    itemClickListener.onDecline(item, position)
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
        holder.bind(requests[holder.adapterPosition], holder.adapterPosition)
    }

    override fun getItemCount(): Int = requests.size

    fun updateList(list: MutableList<CLUsers>) {
        this.requests = list
        notifyDataSetChanged()
    }

    fun removedAtPosition(position: Int) {
        requests.removeAt(position)
        notifyItemRemoved(position)
    }
}