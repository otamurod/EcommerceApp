package uz.otamurod.ecommerceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.otamurod.ecommerceapp.data.Product
import uz.otamurod.ecommerceapp.databinding.ProductDetailsImageItemBinding

class ProductDetailsImagesAdapter : RecyclerView.Adapter<ProductDetailsImagesAdapter.ViewHolder>() {

    inner class ViewHolder(private val productDetailsImageItemBinding: ProductDetailsImageItemBinding) :
        RecyclerView.ViewHolder(productDetailsImageItemBinding.root) {
        fun onBind(product: Product) {
            productDetailsImageItemBinding.apply {
                Glide.with(itemView).load(product.images[0]).into(productDetailsImage)
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
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
        val product = differ.currentList[position]
        holder.onBind(product)
    }
}