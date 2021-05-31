package com.example.android.politicalpreparedness.representative.adapter

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.GlideApp
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.representative.model.Representative
import timber.log.Timber

@BindingAdapter("profileImage")
fun fetchImage(view: ImageView, src: String?) {
    Timber.d("fetchImage for: $src")
    src?.let {
        val uri = src.toUri().buildUpon().scheme("https").build()
        // load image and circle crop using glide - user ic_profile as a placeholder and for errors
        GlideApp
            .with(view.context)
            .load(uri)
            .circleCrop()
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .into(view)
    }
}

@BindingAdapter("stateValue")
fun Spinner.setNewValue(value: String?) {
    val adapter = toTypedAdapter<String>(this.adapter as ArrayAdapter<*>)
    val position = when (adapter.getItem(0)) {
        is String -> adapter.getPosition(value)
        else -> this.selectedItemPosition
    }
    if (position >= 0) {
        setSelection(position)
    }
}

@InverseBindingAdapter(attribute = "stateValue")
fun Spinner.getNewValue(): String {
    val states: Array<String> = resources.getStringArray(R.array.states)
    return states[this.selectedItemPosition]
}

@BindingAdapter("stateValueAttrChanged")
fun setStateListener(spinner: Spinner, stateChange: InverseBindingListener?) {
    if (stateChange == null) {
        spinner.onItemSelectedListener = null
    } else {
        val listener: AdapterView.OnItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                stateChange.onChange()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                stateChange.onChange()
            }
        }
        spinner.onItemSelectedListener = listener
    }
}

inline fun <reified T> toTypedAdapter(adapter: ArrayAdapter<*>): ArrayAdapter<T> {
    return adapter as ArrayAdapter<T>
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Representative>?) {
    val adapter = recyclerView.adapter as RepresentativeListAdapter
    adapter.submitList(data)
}
