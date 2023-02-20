package com.example.pulsa


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pulsa.databinding.ListItemBinding


class FrontPageAdapter(private var items: MutableList<Post>, private val onClick: (Post) -> Unit) :
    RecyclerView.Adapter<FrontPageAdapter.ViewHolder>() {
    class ViewHolder(itemBinding: ListItemBinding, val onClick: (Post) -> Unit) :
        RecyclerView.ViewHolder(itemBinding.root) {
        private val title = itemBinding.postTitle
        private val text = itemBinding.postText
        private val sub = itemBinding.subId
        private val img = itemBinding.postImage

        fun bind(item: Post) {
            title.text = item.title
            text.text = item.content?.text
            sub.text = item.sub?.name
            img.setImageResource(R.drawable.pulsa)
            itemView.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}