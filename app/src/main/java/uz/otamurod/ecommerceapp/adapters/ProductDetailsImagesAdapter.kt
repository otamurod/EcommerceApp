package uz.otamurod.ecommerceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.otamurod.ecommerceapp.databinding.ProductDetailsImageItemBinding

class ProductDetailsImagesAdapter : RecyclerView.Adapter<ProductDetailsImagesAdapter.ViewHolder>() {

    inner class ViewHolder(private val productDetailsImageItemBinding: ProductDetailsImageItemBinding) :
        RecyclerView.ViewHolder(productDetailsImageItemBinding.root) {
        fun onBind(imagePath: String) {
            productDetailsImageItemBinding.apply {
                Glide.with(itemView).load(imagePath).into(productDetailsImage)
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
            ProductDetailsImageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ProductDetailsImagesAdapter.ViewHolder, position: Int) {
        val imagePath = differ.currentList[position]
        holder.onBind(imagePath)
    }
}