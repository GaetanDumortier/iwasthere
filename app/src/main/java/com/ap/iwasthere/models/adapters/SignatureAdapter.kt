package com.ap.iwasthere.models.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import com.ap.iwasthere.R
import com.ap.iwasthere.helpers.FirebaseHelper
import com.ap.iwasthere.helpers.LocationHelper
import com.ap.iwasthere.models.FirebaseCallback
import com.ap.iwasthere.models.Location
import com.ap.iwasthere.models.Signature

class SignatureAdapter(
    context: Context,
    @LayoutRes private val layoutResource: Int,
    values: ArrayList<Signature>
) : ArrayAdapter<Signature>(context, layoutResource, values) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater: LayoutInflater = LayoutInflater.from(context)
        val cView = layoutInflater.inflate(layoutResource, parent, false)

        val imageView: ImageView = cView.findViewById(R.id.signatureImage)
        val location: TextView = cView.findViewById(R.id.lblSignatureLocation)
        val date: TextView = cView.findViewById(R.id.lblSignatureDate)
        val locationModel: Location = getItem(position)?.location as Location

        imageView.setImageBitmap(getItem(position)?.decodeSignature())
        location.text = getItem(position)?.location?.address
        date.text = getItem(position)?.date

        if (layoutResource == R.layout.signature_row) {
            val studentName: TextView = cView.findViewById(R.id.lblSignatureStudentName)
            FirebaseHelper().fetchStudentNameById(
                getItem(position)?.studentId!!,
                object : FirebaseCallback.ItemCallback {
                    override fun onItemCallback(value: Any) {
                        if ((value as String).isNotEmpty()) {
                            studentName.text = value
                        }
                    }
                })
        }

        // Modify styling if the location is marked as suspicious
        if (LocationHelper.locationIsSuspicious(locationModel)) {
            location.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            location.setTypeface(null, Typeface.BOLD)
        }

        return cView
    }
}