package hr.jakovnovak.games.floppydisk.ui.main.popups

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import hr.jakovnovak.games.floppydisk.R

class SettingsFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // TODO: difficulty + skins
        val builder = AlertDialog.Builder(activity)
        return builder
            .setView(R.layout.fragment_settings)
            .create()
    }
}