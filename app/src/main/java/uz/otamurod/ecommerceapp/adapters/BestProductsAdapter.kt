package uz.otamurod.ecommerceapp.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.otamurod.ecommerceapp.data.Product
import uz.otamurod.ecommerceapp.databinding.BestProductRvItemBinding

class BestProductsAdapter : RecyclerView.Adapter<BestProductsAdapter.ViewHolder>() {
    inner class ViewHolder(private val bestProductRvItemBinding: BestProductRvItemBinding) :
        RecyclerView.ViewHolder(bestProductRvItemBinding.root) {
        fun onBind(product: Product) {
            bestProductRvItemBinding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgProduct)
                product.offerPercentage?.let {
                    val remainingPricePercentage = 1f - it
                    val priceAfterDiscount = remainingPricePercentage * product.price
                    tvNewPrice.text = String.format("$%.2f", priceAfterDiscount)
                    tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
                if (product.offerPercentage == null) {
                    tvNewPrice.isVisible = false
                }
                tvPrice.text = String.format("$%.2f", product.price)
                tvName.text = product.name
                progressBarBestProduct.isVisible = false
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
    ): BestProductsAdapter.ViewHolder {
        return ViewHolder(
            BestProductRvItemBinding.inflate(
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
        val product = differ.currentList[position]
        holder.onBind(product)
        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    var onClick: ((Product) -> Unit)? = null
}