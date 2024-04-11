package uz.otamurod.ecommerceapp.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import uz.otamurod.ecommerceapp.R
import uz.otamurod.ecommerceapp.data.Address
import uz.otamurod.ecommerceapp.databinding.AddressRvItemBinding

class AddressAdapter : RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

    private var selectedAddressPosition = -1

    private val diffCallback = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.addressTitle == newItem.addressTitle && oldItem.fullName == newItem.fullName
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    init {
        differ.addListListener { previousList, currentList ->
            notifyItemChanged(selectedAddressPosition)
        }
    }

    inner class ViewHolder(val addressRvItemBinding: AddressRvItemBinding) :
        RecyclerView.ViewHolder(addressRvItemBinding.root) {
        fun onBind(address: Address, isSelected: Boolean) {
            addressRvItemBinding.apply {
                buttonAddress.text = address.addressTitle
                if (isSelected) {
                    buttonAddress.background =
                        ColorDrawable(itemView.context.resources.getColor(R.color.g_blue))
                } else {
                    buttonAddress.background =
                        ColorDrawable(itemView.context.resources.getColor(R.color.g_white))
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressAdapter.ViewHolder {
        return ViewHolder(
            AddressRvItemBinding.inflate(
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
        val address = differ.currentList[position]
        holder.onBind(address, selectedAddressPosition == position)

        holder.addressRvItemBinding.buttonAddress.setOnClickListener {
            if (selectedAddressPosition >= 0) {
                notifyItemChanged(selectedAddressPosition)
            }
            selectedAddressPosition = holder.adapterPosition
            notifyItemChanged(selectedAddressPosition)
            onClick?.invoke(address)
        }
    }

    var onClick: ((Address) -> Unit)? = null
}