package jp.s64.android.recyclerview.customitemanimators.example

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import jp.s64.android.recyclerview.customitemanimators.example.databinding.TestItemBinding
import jp.s64.android.recyclerview.customitemanimators.liftup.FadeLiftUpItemAnimator

class FadeLiftUpExampleActivity : AbsExampleActivity<HogeAdapter.MyVH>() {

    override fun createAdapter(state: IState): AbsAdapter<HogeAdapter.MyVH> {
        return HogeAdapter(state)
    }

    override fun createItemAnimator(): RecyclerView.ItemAnimator {
        return FadeLiftUpItemAnimator(recycler)
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
