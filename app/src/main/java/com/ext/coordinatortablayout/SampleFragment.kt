package com.ext.coordinatortablayout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class SampleFragment : Fragment() {

    companion object {
        private const val ARG_TEXT = "arg_text"

        fun newInstance(text: String): SampleFragment {
            val fragment = SampleFragment()
            val args = Bundle()
            args.putString(ARG_TEXT, text)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sample, container, false)
        val textView = view.findViewById<TextView>(R.id.textView)

        val tabName = arguments?.getString(ARG_TEXT) ?: ""

        // Generate sample content list exactly like reference image
        val content = StringBuilder()
        for (i in 'A'..'Z') {
            content.append("$tabName $i\n\n")
        }

        textView.text = content.toString().trim()
        return view
    }
}