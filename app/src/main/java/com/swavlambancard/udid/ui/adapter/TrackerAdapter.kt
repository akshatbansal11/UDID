package com.swavlambancard.udid.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.ItemTrackerBinding
import com.swavlambancard.udid.model.ApplicationStatus
import com.swavlambancard.udid.model.StatusArray
import com.swavlambancard.udid.utilities.hideView
import com.swavlambancard.udid.utilities.showView

class TrackerAdapter(
    val context: Context,
    private val trackerSelectedList : ArrayList<ApplicationStatus>,
    private val trackerList : ArrayList<StatusArray>) :
    RecyclerView.Adapter<TrackerAdapter.MyViewHolder>() {

    class MyViewHolder(val mBinding: ItemTrackerBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val mBinding: ItemTrackerBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.item_tracker,
            viewGroup,
            false
        )
        return MyViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemTracker=trackerList[position]
        holder.mBinding.tvHeading.text = itemTracker.status_name

        for(compItem in trackerSelectedList){
            if(itemTracker.status_name == compItem.Pwdapplicationstatus.status_name){
                holder.mBinding.ivCircle.hideView()
                holder.mBinding.ivDoubleCircle.showView()
                holder.mBinding.ivDoubleCircle.setImageResource(R.drawable.ic_double_circle)
                holder.mBinding.ivLine.setImageResource(R.drawable.ic_line_blue)
                holder.mBinding.tvHeading.setTextColor(Color.parseColor("#00074C"))
                if(trackerList.size-1 == position){
                    holder.mBinding.ivCircle.showView()
                    holder.mBinding.ivDoubleCircle.hideView()
                    holder.mBinding.ivLine.hideView()
                }
                break
            }
            else{
                holder.mBinding.ivCircle.hideView()
                holder.mBinding.ivDoubleCircle.showView()
                holder.mBinding.ivDoubleCircle.setImageResource(R.drawable.ic_double_circle_grey)
                holder.mBinding.ivLine.setImageResource(R.drawable.ic_line_grey)
                holder.mBinding.tvHeading.setTextColor(Color.parseColor("#969696"))
                if(trackerList.size-1 == position){
                    holder.mBinding.ivCircle.showView()
                    holder.mBinding.ivCircle.setImageResource(R.drawable.ic_complete_grey)
                    holder.mBinding.ivDoubleCircle.hideView()
                    holder.mBinding.ivLine.hideView()
                }
                else if(position == 0){
                    holder.mBinding.ivCircle.showView()
                    holder.mBinding.ivCircle.setImageResource(R.drawable.ic_complete_grey)
                    holder.mBinding.ivDoubleCircle.hideView()
                    holder.mBinding.ivLine.showView()
                }
            }
        }
        if(position == 0){
            holder.mBinding.ivCircle.showView()
            holder.mBinding.ivDoubleCircle.hideView()
        }
        else if(trackerList.size-1 == position){
            holder.mBinding.ivCircle.showView()
            holder.mBinding.ivDoubleCircle.hideView()
            holder.mBinding.ivLine.hideView()
        }
    }

    override fun getItemCount(): Int {
        return trackerList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

