package com.udid.ui.activity

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.udid.R
import com.udid.databinding.ActivityListForDepartmentUserBinding
import com.udid.ui.adapter.DepartmentUserAdapter
import com.udid.utilities.BaseActivity

class ListForDepartmentUserActivity : BaseActivity<ActivityListForDepartmentUserBinding>() {
    private var mBinding: ActivityListForDepartmentUserBinding? = null
    private var departmentUserAdapter: DepartmentUserAdapter? = null
    private var layoutManager: LinearLayoutManager? = null
    override val layoutId: Int
        get() = R.layout.activity_list_for_department_user

    override fun initView() {
        mBinding = viewDataBinding
        mBinding?.clickAction = ClickActions()

        departmentListAdapter()
    }

    inner class ClickActions {
        fun backPress(view: View) {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun departmentListAdapter() {
        departmentUserAdapter = DepartmentUserAdapter(this)
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding?.rvApplicationStatus?.layoutManager = layoutManager
        mBinding?.rvApplicationStatus?.adapter = departmentUserAdapter
    }

    override fun setVariables() {

    }

    override fun setObservers() {

    }
}