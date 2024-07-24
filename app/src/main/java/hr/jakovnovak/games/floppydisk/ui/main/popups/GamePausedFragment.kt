package hr.jakovnovak.games.floppydisk.ui.main.popups

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import hr.jakovnovak.games.floppydisk.GameActivity
import hr.jakovnovak.games.floppydisk.MainActivity
import hr.jakovnovak.games.floppydisk.databinding.FragmentGamepausedBinding

class GamePausedFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val binding = FragmentGamepausedBinding.inflate(activity?.layoutInflater
            ?: throw IllegalStateException("No activity?"))

        binding.resumeButton.setOnClickListener {
            val lock = (activity as GameActivity).gameSurfaceView.lock
            val condition = (activity as GameActivity).gameSurfaceView.condition

            lock.lock()
            condition.signalAll()
            lock.unlock()
            this@GamePausedFragment.dismiss()
        }

        binding.quitButton.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }

        return builder
            .setView(binding.root)
            .setCancelable(false)
            .create()
    }
}