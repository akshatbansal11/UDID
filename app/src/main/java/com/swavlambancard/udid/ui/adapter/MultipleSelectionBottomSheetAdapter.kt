package com.swavlambancard.udid.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.swavlambancard.udid.R
import com.swavlambancard.udid.databinding.ItemSelectBinding
import com.swavlambancard.udid.model.DropDownResult

class MultipleSelectionBottomSheetAdapter (val context: Context,
                                           private var item: ArrayList<DropDownResult>,
                                           private var matchItems: ArrayList<DropDownResult>,
                                           private val id:(String)->Unit):
    RecyclerView.Adapter<MultipleSelectionBottomSheetAdapter.ViewHolder>() {
    var selectedItems = ArrayList<DropDownResult>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val mBinding: ItemSelectBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_select, parent, false
        )
        return ViewHolder(mBinding)
    }

    class ViewHolder(val mBinding: ItemSelectBinding) :
        RecyclerView.ViewHolder(mBinding.root)

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        selectedItems = matchItems
        Log.d("LIST",matchItems.toString())
        selectedItems.toString().let { Log.d("LIST2", it) }
        val item = item[position]
        holder.mBinding.tvName.text= item.name
        if(selectedItems.contains(DropDownResult(item.id,item.name))){
            holder.mBinding.checkbox.setImageDrawable(context.resources.getDrawable(R.drawable.ic_check_box_selected))
        }
        else{
            holder.mBinding.checkbox.setImageDrawable(context.resources.getDrawable(R.drawable.ic_check_box))
        }

        holder.mBinding.selectItem.setOnClickListener {
            if(selectedItems.contains(DropDownResult(item.id,item.name))){
                selectedItems.remove(DropDownResult(item.id,item.name))
                id.invoke(item.id)
                holder.mBinding.checkbox.setImageDrawable(context.resources.getDrawable(R.drawable.ic_check_box))
            } else {
                holder.mBinding.checkbox.setImageDrawable(context.resources.getDrawable(R.drawable.ic_check_box_selected))
                selectedItems.add(DropDownResult(item.id,item.name))
                id.invoke(item.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}