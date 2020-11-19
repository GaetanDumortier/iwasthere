package com.ap.iwasthere.models.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.ap.iwasthere.R
import com.ap.iwasthere.models.Signature

class SignatureAdapter(
    context: Context, @LayoutRes private val layoutResource: Int, values: ArrayList<Signature>
) : ArrayAdapter<Signature>(context, layoutResource, values) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        val cView = layoutInflater.inflate(layoutResource, parent, false)

        val imageView: ImageView = cView.findViewById(R.id.signatureImage)
        val location: TextView = cView.findViewById(R.id.lblSignatureLocation)
        val date: TextView = cView.findViewById(R.id.lblSignatureDate)

        imageView.setImageBitmap(getItem(position)?.decodeSignature())
        location.text = getItem(position)?.location
        date.text = getItem(position)?.date

        return cView
    }
}