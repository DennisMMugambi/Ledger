package com.marknjunge.ledger.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.marknjunge.ledger.R
import com.marknjunge.ledger.data.models.MessageGroup
import com.marknjunge.ledger.data.models.MpesaMessage
import com.marknjunge.ledger.utils.DateTime
import kotlinx.android.synthetic.main.item_group.view.*
import java.util.*

/**
 * Created by MarkNjunge.
 * mark.kamau@outlook.com
 * https://github.com/MarkNjunge
 */

class GroupMessageAdapter(private val context: Context, private val onClick: (MpesaMessage) -> Unit) : RecyclerView.Adapter<GroupMessageAdapter.ViewHolder>() {

    private var data: List<MessageGroup> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(context, data[position], onClick)

    fun setItems(data: List<MessageGroup>) {
        this.data = data
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(context: Context, item: MessageGroup, onClick: (MpesaMessage) -> Unit) = with(itemView) {
            val date = DateTime.fromTimestamp(item.date).format("EE, dd - MMM - YY")
            tvHeaderText.text = date
            tvTransactions.text = if (item.messages.size > 1) "${item.messages.size} transactions" else "${item.messages.size} transaction"

            val messageAdapter = MessageAdapter(context, onClick)
            messageAdapter.setItems(item.messages)

            rvMessages.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            rvMessages.adapter = messageAdapter
        }
    }
}