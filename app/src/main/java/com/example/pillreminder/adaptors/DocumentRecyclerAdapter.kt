package com.example.pillreminder.adaptors

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pillreminder.R
import com.example.pillreminder.fragments.FragmentProfileDocumentDetails
import com.example.pillreminder.models.Document
import kotlinx.android.synthetic.main.view_holder_document.view.*
import kotlinx.android.synthetic.main.view_holder_document.view.tvNumber

class DocumentRecyclerAdapter(
    internal var context: Context,
    internal var documentList: List<Document>,
    internal var supportFragmentManager: FragmentManager
) : RecyclerView.Adapter<DocumentRecyclerAdapter.DocumentViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentRecyclerAdapter.DocumentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_document,parent,false)
        return DocumentViewHolder(documentList,itemView,supportFragmentManager)
    }

    override fun getItemCount(): Int {
        return documentList.size
    }

    override fun onBindViewHolder(holder: DocumentRecyclerAdapter.DocumentViewHolder, position: Int) {
        holder.txtNumber.text = (position+1).toString()
        holder.txt_title.text = documentList[position].title
        holder.setItemPosition(position)
    }

    class DocumentViewHolder(
        documentList: List<Document>,
        itemView: View,
        supportFragmentManager: FragmentManager
    ) : RecyclerView.ViewHolder(itemView){
        val txtNumber = itemView.tvNumber
        val txt_title = itemView.txt_list_document_title
        var itemPos : Int = 0
        fun setItemPosition(itemPos : Int){
            this.itemPos = itemPos
        }
        init {
            itemView.setOnClickListener {
//                Log.e("position",itemPos.toString())
                val bundle = Bundle()
                bundle.putInt("position", itemPos)
                bundle.putLong("id", documentList[itemPos].localId)
                bundle.putString("title", documentList[itemPos].title)
                bundle.putString("uri", documentList[itemPos].uri)
                bundle.putString("file", documentList[itemPos].file)
                bundle.putString("type", documentList[itemPos].type)
                if (documentList[itemPos].remoteId!=null)
                bundle.putInt("remoteId", documentList[itemPos].remoteId!!)
                val pop = FragmentProfileDocumentDetails()
                pop.arguments = bundle
                val fm = supportFragmentManager
                pop.show(fm, "fragmentProfileDocumentDetails")
            }
        }
    }

}