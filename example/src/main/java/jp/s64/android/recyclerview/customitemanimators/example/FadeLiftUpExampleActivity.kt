package jp.s64.android.recyclerview.customitemanimators.example

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import jp.s64.android.recyclerview.customitemanimators.example.databinding.TestItemBinding
import jp.s64.android.recyclerview.customitemanimators.liftup.FadeLiftUpItemAnimator
import jp.s64.android.recyclerview.customitemanimators.util.ItemAnimatorUtils

class FadeLiftUpExampleActivity : AbsExampleActivity<HogeAdapter.MyVH>() {

    override fun beforeCreate() {
        setContentView(R.layout.common_example_activity)
    }

    override fun autoScroll(): CheckBox = findViewById(R.id.autoscroll)

    override fun doAction(): Button = findViewById(R.id.do_action)

    override fun recycler(): RecyclerView = findViewById(R.id.recycler)

    override fun createAdapter(state: IState): AbsAdapter<HogeAdapter.MyVH> {
        return HogeAdapter(state)
    }

    override fun createItemAnimator(): RecyclerView.ItemAnimator {
        return FadeLiftUpItemAnimator(recycler(), 200)
    }

    override fun maxDuration(): Long {
        return ItemAnimatorUtils.maxSingleDuration(recycler().itemAnimator)
    }
}

class HogeAdapter(
    override val state: AbsExampleActivity.IState
) : AbsExampleActivity.AbsAdapter<HogeAdapter.MyVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVH {
        return MyVH(TestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MyVH, position: Int) {
        holder.binding.item = list[position].toString()
        holder.binding.itemIndex = position
    }

    class MyVH(val binding: TestItemBinding) : RecyclerView.ViewHolder(binding.root)
}
