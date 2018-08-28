package jp.s64.android.recyclerview.customitemanimators.example

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import jp.s64.android.recyclerview.customitemanimators.core.CustomizableDefaultItemAnimator
import jp.s64.android.recyclerview.customitemanimators.example.databinding.TestItemBinding

class CustomizableDefaultExampleActivity : AbsExampleActivity<CustomizableDefaultExampleActivity.MyVH>() {

    override fun createAdapter(state: IState): AbsAdapter<MyVH> {
        return MyAdapter(state)
    }

    override fun createItemAnimator(): RecyclerView.ItemAnimator {
        return CustomizableDefaultItemAnimator()
    }

    class MyAdapter(
        override val state: AbsExampleActivity.IState
    ) : AbsExampleActivity.AbsAdapter<MyVH>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVH {
            return MyVH(TestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun onBindViewHolder(holder: MyVH, position: Int) {
            holder.binding.item = list[position].toString()
            holder.binding.itemIndex = position
        }
    }

    class MyVH(val binding: TestItemBinding) : RecyclerView.ViewHolder(binding.root)
}
