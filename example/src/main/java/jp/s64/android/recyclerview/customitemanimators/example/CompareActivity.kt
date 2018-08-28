package jp.s64.android.recyclerview.customitemanimators.example

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import jp.s64.android.recyclerview.customitemanimators.core.CustomizableDefaultItemAnimator
import jp.s64.android.recyclerview.customitemanimators.example.databinding.CompareActivityBinding
import jp.s64.android.recyclerview.customitemanimators.example.databinding.CompareItemBinding
import jp.s64.android.recyclerview.customitemanimators.liftup.FadeLiftUpItemAnimator
import jp.s64.android.recyclerview.customitemanimators.liftup.LiftUpItemAnimator
import jp.s64.android.recyclerview.customitemanimators.liftup.PlaceAndFadeLiftUpItemAnimator
import java.util.UUID
import java.util.concurrent.TimeUnit

class CompareActivity : RxAppCompatActivity() {

    private lateinit var binding: CompareActivityBinding

    private lateinit var sharedAdapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedAdapter = MyAdapter()

        binding = DataBindingUtil.setContentView<CompareActivityBinding>(this, R.layout.compare_activity)
                .apply {
                    recycler1.apply {
                        this.layoutManager = createLM()
                        this.adapter = sharedAdapter
                        this.itemAnimator = CustomizableDefaultItemAnimator()
                    }
                    recycler2.apply {
                        this.layoutManager = createLM()
                        this.adapter = sharedAdapter
                        this.itemAnimator = FadeLiftUpItemAnimator(this, 250)
                    }
                    recycler3.apply {
                        this.layoutManager = createLM()
                        this.adapter = sharedAdapter
                        this.itemAnimator = LiftUpItemAnimator(this, 250)
                    }
                    recycler4.apply {
                        this.layoutManager = createLM()
                        this.adapter = sharedAdapter
                        this.itemAnimator = PlaceAndFadeLiftUpItemAnimator(this, 250)
                    }
                }

        Observable.interval(1, TimeUnit.SECONDS)
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    sharedAdapter.add(listOf(
                            binding.recycler1,
                            binding.recycler2,
                            binding.recycler3,
                            binding.recycler4
                    ))
                }
    }

    fun createLM(): RecyclerView.LayoutManager {
        return LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false
        )
    }

    class MyAdapter : RecyclerView.Adapter<MyVH>() {

        private val list: MutableList<UUID> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVH {
            return MyVH(CompareItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: MyVH, position: Int) {
            holder.binding.uuid = list[position]
        }

        fun add(targets: List<RecyclerView>) {
            list.add(UUID.randomUUID())
            notifyItemInserted(list.size - 1)
            targets.forEach {
                it.scrollToPosition(list.size - 1)
            }
        }

    }

    data class MyVH(val binding: CompareItemBinding) : RecyclerView.ViewHolder(binding.root)

}