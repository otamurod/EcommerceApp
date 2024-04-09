package uz.otamurod.ecommerceapp.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.otamurod.ecommerceapp.databinding.ColorRvItemBinding

class ColorsAdapter : RecyclerView.Adapter<ColorsAdapter.ViewHolder>() {
    private var selectedColorPosition = -1

    inner class ViewHolder(private val colorRvItemBinding: ColorRvItemBinding) :
        RecyclerView.ViewHolder(colorRvItemBinding.root) {
        fun onBind(color: Int, position: Int) {
            val colorDrawable = ColorDrawable(color)
            colorRvItemBinding.productColor.setImageDrawable(colorDrawable)
            if (position == selectedColorPosition) {
                colorRvItemBinding.apply {
                    productColorShadow.isVisible = true
                    imagePicked.isVisible = true
                }
            } else {
                colorRvItemBinding.apply {
                    productColorShadow.isVisible = false
                    imagePicked.isVisible = false
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            ColorRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ColorsAdapter.ViewHolder, position: Int) {
        val color = differ.currentList[position]
        holder.onBind(color, position)
        holder.itemView.setOnClickListener {
            if (selectedColorPosition >= 0) { // to unselect previous selection
                notifyItemChanged(selectedColorPosition)
            }
            selectedColorPosition = holder.adapterPosition
            notifyItemChanged(selectedColorPosition)
            onColorClick?.invoke(color)
        }
    }

    var onColorClick: ((Int) -> Unit)? = null
}