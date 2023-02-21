package com.example.pulsa


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pulsa.databinding.ReplyBinding


class PostPageAdapter(private var items: MutableList<Reply>, private val onClick: (Reply) -> Unit) :
    RecyclerView.Adapter<PostPageAdapter.ViewHolder>() {
    class ViewHolder(itemBinding: ReplyBinding, val onClick: (Reply) -> Unit) :
        RecyclerView.ViewHolder(itemBinding.root) {
        private val text = itemBinding.textView

        fun bind(item: Reply) {
            text.text = item.content?.text
            itemView.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            ReplyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}