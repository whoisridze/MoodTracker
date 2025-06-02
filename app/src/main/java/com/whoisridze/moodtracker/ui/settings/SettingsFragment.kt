package com.whoisridze.moodtracker.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.whoisridze.moodtracker.R
import com.whoisridze.moodtracker.data.repository.MoodRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.net.toUri

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateDeleteButtonState()

        view.findViewById<MaterialButton>(R.id.btnViewJson).setOnClickListener {
            showJsonContents()
        }

        view.findViewById<MaterialButton>(R.id.btnDeleteJson).setOnClickListener {
            confirmDeleteJson()
        }

        view.findViewById<TextView>(R.id.tvDeveloperName).setOnClickListener {
            val uri = "https://linktr.ee/r1dze".toUri()
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    private fun showJsonContents() {
        viewLifecycleOwner.lifecycleScope.launch {
            val repository = MoodRepositoryImpl(requireContext())
            val moods = withContext(Dispatchers.IO) {
                repository.getMoods()
            }

            val jsonContent = if (moods.isEmpty()) {
                getString(R.string.noDataFound)
            } else {
                buildString {
                    moods.forEachIndexed { index, mood ->
                        append("Entry ${index + 1}:\n")
                        append("Date: ${mood.date}\n")
                        append("Time: ${mood.time}\n")
                        append("Mood: ${mood.moodType}\n")
                        if (mood.reason != null) {
                            append("Reason: ${mood.reason}\n")
                        }
                        append("\n")
                    }
                }
            }

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.moodData)
                .setMessage(jsonContent)
                .setPositiveButton(R.string.close, null)
                .show()
        }
    }

    private fun confirmDeleteJson() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.deleteMoodDataButton)
            .setMessage(R.string.confirmDelete)
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes) { _, _ ->
                deleteJsonFile()
            }
            .show()
    }

    private fun deleteJsonFile() {
        val fileName = "mood_entries.json"
        requireContext().deleteFile(fileName)

        updateDeleteButtonState()

        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.dataDeleted)
            .setPositiveButton(R.string.close, null)
            .show()
    }

    private fun updateDeleteButtonState() {
        viewLifecycleOwner.lifecycleScope.launch {
            val repository = MoodRepositoryImpl(requireContext())
            val moods = withContext(Dispatchers.IO) {
                repository.getMoods()
            }

            view?.findViewById<MaterialButton>(R.id.btnDeleteJson)?.isEnabled = moods.isNotEmpty()
        }
    }
}