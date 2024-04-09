package uz.otamurod.ecommerceapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.otamurod.ecommerceapp.databinding.SizeRvItemBinding

class SizesAdapter : RecyclerView.Adapter<SizesAdapter.ViewHolder>() {
    private var selectedSizePosition = -1

    inner class ViewHolder(private val sizeRvItemBinding: SizeRvItemBinding) :
        RecyclerView.ViewHolder(sizeRvItemBinding.root) {
        fun onBind(size: String, position: Int) {
            sizeRvItemBinding.tvSize.text = size

            if (position == selectedSizePosition) {
                sizeRvItemBinding.apply {
                    productColorShadow.visibility = View.VISIBLE
                }
            } else {
                sizeRvItemBinding.apply {
                    productColorShadow.visibility = View.INVISIBLE
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            SizeRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val size = differ.currentList[position]
        holder.onBind(size, position)
        holder.itemView.setOnClickListener {
            if (selectedSizePosition >= 0) { // to unselect previous selection
                notifyItemChanged(selectedSizePosition)
            }
            selectedSizePosition = holder.adapterPosition
            notifyItemChanged(selectedSizePosition)
            onColorClick?.invoke(size)
        }
    }

    var onColorClick: ((String) -> Unit)? = null
}