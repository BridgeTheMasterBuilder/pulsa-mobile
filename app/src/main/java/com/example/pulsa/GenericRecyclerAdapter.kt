package com.example.pulsa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

open class GenericRecyclerAdapter<T : Any>(
    private var items: MutableList<T>,
    private val onClick: ((T) -> Unit)?,
    @LayoutRes val layoutId: Int
) : RecyclerView.Adapter<GenericRecyclerAdapter.GenericViewHolder<T>>() {

    fun swapList(list: MutableList<T>) {
        this.items.clear()
        this.items.addAll(list)
        notifyDataSetChanged()
    }

    fun addItem(item: T, position: Int? = null) {
        if (position != null) {
            this.items.add(position, item)
            notifyItemInserted(position)
        } else {
            this.items.add(item)
            notifyItemInserted(this.items.size - 1)
        }
    }

    fun updateItem(item: T, position: Int) {
        this.items[position] = item
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<T> {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            layoutId,
            parent,
            false
        )

        onClick?.let { return GenericViewHolder(binding, onClick) }
        return GenericViewHolder(binding, null)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: GenericViewHolder<T>, position: Int) {
        holder.bind(items[position])
    }

    class GenericViewHolder<T>(
        private val binding: ViewDataBinding,
        val onClick: ((T) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(item: T) {
            binding.setVariable(BR.listItem, item)
            onClick?.run { itemView.setOnClickListener { onClick.invoke(item) } }
        }
    }
}