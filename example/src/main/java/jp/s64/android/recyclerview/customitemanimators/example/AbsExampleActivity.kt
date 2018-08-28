package jp.s64.android.recyclerview.customitemanimators.example

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import jp.s64.android.recyclerview.customitemanimators.util.ItemAnimatorUtils
import jp.s64.android.recyclerview.customitemanimators.util.RecyclerViewLastDetector
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.max

abstract class AbsExampleActivity<VH : RecyclerView.ViewHolder> : RxAppCompatActivity() {

    protected lateinit var recycler: RecyclerView
    protected lateinit var autoscroll: CheckBox

    protected val onClickAction: PublishSubject<Unit> = PublishSubject.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.abs_example_activity)

        recycler = findViewById(R.id.recycler)
        autoscroll = findViewById(R.id.autoscroll)

        val myAdapter = createAdapter(object : IState {

            override fun autoScroll(): Boolean = autoscroll.isChecked

        })

        recycler.apply {
            this.layoutManager = LinearLayoutManager(
                    this@AbsExampleActivity,
                    LinearLayoutManager.VERTICAL,
                    false
            )
            this.adapter = myAdapter
            this.itemAnimator = createItemAnimator()
        }

        findViewById<Button>(R.id.do_action)
                .setOnClickListener {
                    onClickAction.onNext(Unit)
                }

        onClickAction
                .throttleFirst(
                        ItemAnimatorUtils.maxSingleDuration(recycler.itemAnimator),
                        TimeUnit.MILLISECONDS
                )
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    myAdapter.actionRandom(recycler)
                }
    }

    abstract fun createAdapter(state: IState): AbsAdapter<VH>
    abstract fun createItemAnimator(): RecyclerView.ItemAnimator

    interface IState {

        fun autoScroll(): Boolean

    }

    abstract class AbsAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

        val list: MutableList<UUID> = mutableListOf()
        abstract val state: IState

        fun actionRandom(target: RecyclerView) {
            val isLast = RecyclerViewLastDetector.isLast(target)
            val flag = Random().nextInt(9 + 1)

            when {
                list.size > 5 && flag == 6 -> { // remove
                    Toast.makeText(target.context, "Remove", Toast.LENGTH_SHORT).show()

                    val removeIdx = (max(0, list.size - 5)..(list.size - 1)).shuffled().first()
                    list.removeAt(removeIdx)
                    notifyItemRemoved(removeIdx)
                }
                list.size > 5 && flag == 7 -> { // insert
                    Toast.makeText(target.context, "Insert", Toast.LENGTH_SHORT).show()

                    val insertIdx = (max(0, list.size - 5)..(list.size - 1)).shuffled().first()
                    list.add(insertIdx, UUID.randomUUID())
                    notifyItemInserted(insertIdx)
                }
                list.size > 5 && flag == 8 -> { // move
                    Toast.makeText(target.context, "Move", Toast.LENGTH_SHORT).show()

                    val first = (max(0, list.size - 5)..(list.size - 2)).shuffled().first()
                    val value = list[first]
                    list.removeAt(first)

                    val last = (first..(list.size - 1)).shuffled().first()
                    list.add(last, value)
                    notifyItemMoved(first, last)
                }
                list.size > 5 && flag == 9 -> { // change
                    Toast.makeText(target.context, "Change", Toast.LENGTH_SHORT).show()

                    val idx = (max(0, list.size - 5)..(list.size - 1)).shuffled().first()
                    list.removeAt(idx)
                    list.add(idx, UUID.randomUUID())
                    notifyItemChanged(idx)
                }
                else -> { // add
                    Toast.makeText(target.context, "Add", Toast.LENGTH_SHORT).show()

                    list.add(UUID.randomUUID())
                    (list.size - 1).also {
                        notifyItemInserted(it)
                    }
                }
            }

            if (state.autoScroll()) {
                if (isLast) {
                    target.scrollToPosition(list.size - 1)
                } else {
                    target.smoothScrollToPosition(list.size - 1)
                }
            }
        }

        override fun getItemCount(): Int = list.size

    }

}
