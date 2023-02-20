package com.example.pulsa


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pulsa.databinding.ListItemBinding


class PostPageAdapter(private var items: ArrayList<Reply>) :
    RecyclerView.Adapter<PostPageAdapter.ViewHolder>() {
    class ViewHolder(itemBinding: ListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        private val text = itemBinding.postText

        fun bind(item: Reply) {
            text.text = item.content.text
//            itemView.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}