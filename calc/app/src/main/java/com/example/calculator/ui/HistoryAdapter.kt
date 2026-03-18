package com.example.calculator.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.calculator.data.HistoryEntry
import android.util.Log
import com.example.calculator.databinding.ItemHistoryBinding

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var historyList = listOf<HistoryEntry>()

    fun submitList(list: List<HistoryEntry>) {
        historyList = list
        Log.d("HistoryAdapter", "List size: ${list.size}")
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    override fun getItemCount() = historyList.size

    class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: HistoryEntry) {
            binding.textExpression.text = entry.expression
            binding.textResult.text = "= ${entry.result}"
            // Можно добавить форматирование даты из timestamp
        }
    }
}