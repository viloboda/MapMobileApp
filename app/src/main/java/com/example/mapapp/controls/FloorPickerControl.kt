package com.example.mapapp.controls

import android.content.Context
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.example.mapapp.R
import model.FloorItem

class FloorPickerControl : FrameLayout {
    private val inflater: LayoutInflater
    private lateinit var vFloors: RecyclerView
    private lateinit var vFadeTop: View
    private lateinit var vFadeBottom: View
    private lateinit var rvAdapter: FloorsAdapter

    constructor(context: Context) : super(context) {

        inflater = LayoutInflater.from(context)
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        inflater = LayoutInflater.from(context)
        init()
    }

    fun init() {
        val control = inflater.inflate(R.layout.floor_picker_control, this, true)
        vFloors = control.findViewById(R.id.fpc_floors)
        vFadeTop = control.findViewById(R.id.fpc_fade_top)
        vFadeBottom = control.findViewById(R.id.fpc_fade_bottom)

        rvAdapter = FloorsAdapter(inflater.context, arrayListOf(FloorItem(0)))
        val lm = SpeedyLinearLayoutManager(inflater.context)
        vFloors.apply {
            setHasFixedSize(true)
            layoutManager = lm
            adapter = rvAdapter
        }
        rvAdapter.setRevycler(vFloors)

        var cyrrentYPos = 0
        vFloors.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                cyrrentYPos += dy

                val totalItemCount = lm.itemCount

                val lastVisible = lm.findLastCompletelyVisibleItemPosition() + 1

                if (lastVisible == totalItemCount) {
                    vFadeBottom.visibility = View.GONE
                } else {
                    vFadeBottom.visibility = View.VISIBLE
                }

                val firstVisible = lm.findFirstCompletelyVisibleItemPosition()
                if (firstVisible == 0) {
                    vFadeTop.visibility = View.GONE
                } else {
                    vFadeTop.visibility = View.VISIBLE
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val partlyVisible = lm.findFirstVisibleItemPosition()
                    val fullyVisible = lm.findFirstCompletelyVisibleItemPosition()

                    if (partlyVisible == fullyVisible) {
                        return
                    }

                    val itemView = lm.getChildAt(partlyVisible) ?: return

                    val relativePos = cyrrentYPos - (partlyVisible * itemView.height)

                    if (relativePos < itemView.height / 2) {
                        vFloors.smoothScrollToPosition(partlyVisible)
                    } else {
                        vFloors.smoothScrollToPosition(lm.findLastVisibleItemPosition())
                    }
                }
            }
        })
    }

    fun setItems(items: List<FloorItem>) {
        rvAdapter.setItems(items)
    }

    fun setSelectedIndex(index: Int) {
        rvAdapter.setSelectedIndex(index)
    }

    fun setOnSelectionChanged(listener: OnItemSelectedListener) {
        rvAdapter.setOnSelectionChanged(listener)
    }

    interface OnItemSelectedListener {
        fun onItemSelected(item: FloorItem)
    }

    class SpeedyLinearLayoutManager(context: Context) : LinearLayoutManager(context) {

        override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State?, position: Int) {

            val linearSmoothScroller = object : LinearSmoothScroller(recyclerView.context) {

                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
                }
            }

            linearSmoothScroller.targetPosition = position
            startSmoothScroll(linearSmoothScroller)
        }

        companion object {

            private const val MILLISECONDS_PER_INCH = 200f // (bigger = slower)
        }
    }

    class FloorsAdapter(private val context: Context, private var items: List<FloorItem>) :
            RecyclerView.Adapter<FloorsAdapter.ViewHolder>() {

        private var selectedIndex: Int = 0
        private var listener: OnItemSelectedListener? = null

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder.
        // Each data item is just a string in this case that is shown in a TextView.
        class ViewHolder(view: View, val context: Context) : RecyclerView.ViewHolder(view) {
            private var vValue: TextView = view.findViewById(R.id.fpci_value)

            fun setText(value: String) {
                vValue.text = value
            }

            fun setSelected(selected: Boolean) {
                if (selected) {
                    vValue.setBackgroundColor(ContextCompat.getColor(context, R.color.mercury))
                } else {
                    vValue.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                }
            }
        }

        private lateinit var rv: RecyclerView
        private var selectedView: View? = null

        fun setRevycler(rv: RecyclerView) {
            this.rv = rv
        }


        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(parent: ViewGroup,
                                        viewType: Int): ViewHolder {
            // create a new view
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.floor_picker_control_item, parent, false)

            view.setOnClickListener { v ->

                if (selectedView != null) {
                    (rv.getChildViewHolder(selectedView!!) as ViewHolder).setSelected(false)
                }

                selectedIndex = rv.getChildAdapterPosition(view)
                (rv.getChildViewHolder(view) as ViewHolder).setSelected(true)
                selectedView = view
                (rv.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(selectedIndex, v.height)

                if (listener != null) {
                    listener!!.onItemSelected(items[selectedIndex])
                }
            }
            // set the view's size, margins, paddings and layout parameters
            return ViewHolder(view, context)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.setText(items[position].id.toString())

            if (position == selectedIndex) {
                if (selectedView != null) {
                    (rv.getChildViewHolder(selectedView!!) as ViewHolder).setSelected(false)
                }
                holder.setSelected(true)
                selectedView = holder.itemView
            } else {
                holder.setSelected(false)
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = items.size

        fun setItems(items: List<FloorItem>) {
            this.items = items
            notifyDataSetChanged()
        }

        fun setSelectedIndex(index: Int) {
            selectedIndex = index

            Handler().postDelayed({

                var scrollTo = selectedIndex
                //rv.scrollToPosition(selectedIndex)
                if (index > 1 && index < items.size - 1) {
                    scrollTo++
                }
                rv.layoutManager!!.smoothScrollToPosition(rv, null, scrollTo)
            }, 200)
        }

        fun setOnSelectionChanged(listener: OnItemSelectedListener) {
            this.listener = listener
        }
    }
}