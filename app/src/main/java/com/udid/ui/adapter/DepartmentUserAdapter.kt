package com.udid.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.udid.R
import com.udid.databinding.DepartmentListItemBinding
import com.udid.ui.activity.PwdApplicationViewActivity

class DepartmentUserAdapter(
    val context: Context) :
    RecyclerView.Adapter<DepartmentUserAdapter.MyViewHolder>() {

    class MyViewHolder(val mBinding: DepartmentListItemBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val mBinding: DepartmentListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.department_list_item,
            viewGroup,
            false
        )
        return MyViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.mBinding.rlParent.setOnClickListener {
            context.startActivity(Intent(context,PwdApplicationViewActivity::class.java))
        }
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

