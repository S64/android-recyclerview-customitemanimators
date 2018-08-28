package jp.s64.android.recyclerview.customitemanimators.example

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import jp.s64.android.recyclerview.customitemanimators.util.ItemAnimatorUtils
import jp.s64.android.recyclerview.customitemanimators.util.RecyclerViewLastDetector
import java.util.Random
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.math.max

abstract class AbsExampleActivity<VH : RecyclerView.ViewHolder> : RxAppCompatActivity() {

    protected val onClickAction: PublishSubject<Unit> = PublishSubject.create()

    abstract fun recycler(): RecyclerView
    abstract fun autoScroll(): CheckBox
    abstract fun doAction(): Button

    abstract fun beforeCreate()

    abstract fun maxDuration(): Long

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        beforeCreate()

        val myAdapter = createAdapter(object : IState {

            override fun autoScroll(): Boolean = this@AbsExampleActivity.autoScroll().isChecked
        })

        recycler().apply {
            this.layoutManager = LinearLayoutManager(
                    this@AbsExampleActivity,
                    LinearLayoutManager.VERTICAL,
                    false
            )
            this.adapter = myAdapter
            this.itemAnimator = createItemAnimator()
        }

        doAction()
                .setOnClickListener {
                    onClickAction.onNext(Unit)
                }

        onClickAction
                .throttleFirst(
                        maxDuration(),
                        TimeUnit.MILLISECONDS
                )
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    myAdapter.actionRandom(recycler())
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

            val toastText: String

            when {
                list.size > 5 && flag == 6 -> { // remove
                    toastText = "Remove"

                    val removeIdx = (max(0, list.size - 5)..(list.size - 1)).shuffled().first()
                    list.removeAt(removeIdx)
                    notifyItemRemoved(removeIdx)
                }
                list.size > 5 && flag == 7 -> { // insert
                    toastText = "Insert"

                    val insertIdx = (max(0, list.size - 5)..(list.size - 1)).shuffled().first()
                    list.add(insertIdx, UUID.randomUUID())
                    notifyItemInserted(insertIdx)
                }
                list.size > 5 && flag == 8 -> { // move
                    toastText = "Move"

                    val first = (max(0, list.size - 5)..(list.size - 2)).shuffled().first()
                    val value = list[first]
                    list.removeAt(first)

                    val last = (first..(list.size - 1)).shuffled().first()
                    list.add(last, value)
                    notifyItemMoved(first, last)
                }
                list.size > 5 && flag == 9 -> { // change
                    toastText = "Change"

                    val idx = (max(0, list.size - 5)..(list.size - 1)).shuffled().first()
                    list.removeAt(idx)
                    list.add(idx, UUID.randomUUID())
                    notifyItemChanged(idx)
                }
                else -> { // add
                    toastText = "Add"

                    list.add(UUID.randomUUID())
                    (list.size - 1).also {
                        notifyItemInserted(it)
                    }
                }
            }

            Toast.makeText(target.context, toastText, Toast.LENGTH_SHORT).show()

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
