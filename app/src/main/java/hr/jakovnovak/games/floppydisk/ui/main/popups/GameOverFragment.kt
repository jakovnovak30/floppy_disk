package hr.jakovnovak.games.floppydisk.ui.main.popups

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import hr.jakovnovak.games.floppydisk.GameActivity
import hr.jakovnovak.games.floppydisk.MainActivity
import hr.jakovnovak.games.floppydisk.databinding.FragmentGameoverBinding

class GameOverFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val binding = FragmentGameoverBinding.inflate(activity?.layoutInflater ?: throw IllegalStateException("Missing activity!"))

        binding.newGameButton.setOnClickListener {
            val intent = Intent(context, GameActivity::class.java)
            startActivity(intent)
        }
        binding.mainMenuButton.setOnClickListener {
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }

        val animator1 = ObjectAnimator.ofFloat(binding.playText, "translationX", 5f)
            .apply {
                duration = 800
                repeatMode = ObjectAnimator.REVERSE
                repeatCount = ObjectAnimator.INFINITE
                start()
            }

        val animator2 = animator1.clone()
        animator2.apply {
            target = binding.againText
            start()
        }

        return builder
            .setView(binding.root)
            .setCancelable(false)
            .create()
    }
}