package com.udid.ui.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.udid.R
import com.udid.databinding.DashboardItemBinding
import com.udid.model.DashboardData
import com.udid.ui.activity.ApplicationStatusActivity
import com.udid.ui.activity.LoginActivity
import com.udid.ui.activity.MyAccountActivity
import com.udid.ui.activity.TrackYourCardActivity
import com.udid.utilities.AppConstants
import com.udid.utilities.Preferences
import com.udid.utilities.Utility


class DashboardAdapter(
    private val context: Context,
    private var items: ArrayList<DashboardData>,

    ) : RecyclerView.Adapter<DashboardAdapter.MyViewHolder>() {

    class MyViewHolder(val mBinding: DashboardItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {}

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val mBinding: DashboardItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.dashboard_item,
            viewGroup,
            false
        )
        return MyViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = items[position]

        holder.mBinding.ivImage.setImageResource(currentItem.image)
        holder.mBinding.tvTitle.text = currentItem.title
        when (position) {
              0 -> {
                holder.mBinding.llParent.setOnClickListener {
                    context.startActivity(Intent(context,MyAccountActivity::class.java))
                }
            }
            7 -> {
                holder.mBinding.llParent.setOnClickListener {
                    context.startActivity(Intent(context, TrackYourCardActivity::class.java))
                }
            }
            9 -> {
                holder.mBinding.llParent.setOnClickListener {
                    context.startActivity(Intent(context, ApplicationStatusActivity::class.java))
                }
            }
//            17 ->{
//                holder.mBinding.llParent.setOnClickListener {
//                    Preferences.removeAllPreference(context)
//                    Utility.clearAllPreferencesExceptDeviceToken(context)
//                    val intent = Intent(context, LoginActivity::class.java)
//                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                    context.startActivity(intent)
//                }
//            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}