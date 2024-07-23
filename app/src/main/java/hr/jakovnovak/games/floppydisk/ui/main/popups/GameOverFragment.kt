package hr.jakovnovak.games.floppydisk.ui.main.popups

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import hr.jakovnovak.games.floppydisk.GameActivity
import hr.jakovnovak.games.floppydisk.MainActivity

class GameOverFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        return builder
            .setMessage("Game Over")
            .setPositiveButton("New game") {dialog, id ->
                val intent = Intent(context, GameActivity::class.java)
                startActivity(intent)
            }
            .setNegativeButton("Main menu") {dialog, id ->
                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
            }
            .create()
    }
}