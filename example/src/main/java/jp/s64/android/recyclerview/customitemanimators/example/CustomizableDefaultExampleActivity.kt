package jp.s64.android.recyclerview.customitemanimators.example

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import jp.s64.android.recyclerview.customitemanimators.core.CustomizableDefaultItemAnimator
import jp.s64.android.recyclerview.customitemanimators.example.databinding.TestItemBinding

class CustomizableDefaultExampleActivity : AbsExampleActivity<CustomizableDefaultExampleActivity.MyVH>() { // ktlint-disable max-line-length

    override fun createAdapter(state: IState): AbsAdapter<MyVH> {
        return MyAdapter(state)
    }

    override fun createItemAnimator(): RecyclerView.ItemAnimator {
        return CustomizableDefaultItemAnimator()
    }

    override fun beforeCreate() {
        setContentView(R.layout.common_example_activity)
    }

    override fun autoScroll(): CheckBox = findViewById(R.id.autoscroll)

    override fun doAction(): Button = findViewById(R.id.do_action)

    override fun recycler(): RecyclerView = findViewById(R.id.recycler)

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
