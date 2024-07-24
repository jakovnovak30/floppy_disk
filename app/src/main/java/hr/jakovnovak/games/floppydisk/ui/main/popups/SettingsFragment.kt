package hr.jakovnovak.games.floppydisk.ui.main.popups

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity.MODE_PRIVATE
import hr.jakovnovak.games.floppydisk.R
import hr.jakovnovak.games.floppydisk.databinding.FragmentSettingsBinding
import hr.jakovnovak.games.floppydisk.model.Difficulty
import hr.jakovnovak.games.floppydisk.model.PlayerIcon

class SettingsFragment : DialogFragment() {
    private val cancelListener = View.OnClickListener { this@SettingsFragment.dismiss() }
    private val confirmListener = View.OnClickListener {
        val sharedPreferences = activity?.getSharedPreferences("floppy_disk", MODE_PRIVATE)
            ?: throw IllegalStateException("No context?!")

        sharedPreferences.edit()
            .putInt("difficulty", difficulty.num)
            .putInt("playerIcon", playerIcon.resId)
            .apply()

        this@SettingsFragment.dismiss()
    }

    private lateinit var difficulty : Difficulty
    private lateinit var playerIcon : PlayerIcon
    private lateinit var binding: FragmentSettingsBinding
    private fun loadPreferences() {
        val sharedPreferences = activity?.getSharedPreferences("floppy_disk", MODE_PRIVATE)
            ?: throw IllegalStateException("No context?!")

        val diffNum = sharedPreferences.getInt("difficulty", Difficulty.MEDIUM.num)
        difficulty = when(diffNum) {
            1 -> Difficulty.EASY
            3 -> Difficulty.MEDIUM
            5 -> Difficulty.HARD
            else -> throw IllegalStateException()
        }

        val iconId = sharedPreferences.getInt("playerIcon", PlayerIcon.BLUE.resId)
        playerIcon = when(iconId) {
            R.drawable.floppy_disk -> PlayerIcon.BLUE
            R.drawable.floppy_disk_green -> PlayerIcon.GREEN
            R.drawable.floppy_disk_red -> PlayerIcon.RED
            else -> throw IllegalStateException()
        }
    }

    private fun updateButtons() {
        binding.cdEasy.clearColorFilter()
        binding.cdMedium.clearColorFilter()
        binding.cdHard.clearColorFilter()

        val color = ContextCompat.getColor(activity?.baseContext
            ?: throw IllegalStateException("Context?"), R.color.green)
        val colorFilter = BlendModeColorFilter(color, BlendMode.MODULATE)

        when(difficulty) {
            Difficulty.EASY -> binding.cdEasy.colorFilter = colorFilter
            Difficulty.MEDIUM -> binding.cdMedium.colorFilter = colorFilter
            Difficulty.HARD -> binding.cdHard.colorFilter = colorFilter
        }

        binding.floppyRed.clearColorFilter()
        binding.floppyBlue.clearColorFilter()
        binding.floppyGreen.clearColorFilter()

        when(playerIcon) {
            PlayerIcon.RED -> binding.floppyRed.colorFilter = colorFilter
            PlayerIcon.GREEN -> binding.floppyGreen.colorFilter = colorFilter
            PlayerIcon.BLUE -> binding.floppyBlue.colorFilter = colorFilter
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // TODO: difficulty + skins
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = it.layoutInflater
            binding = FragmentSettingsBinding.inflate(inflater)

            binding.cancelSettingsButton.setOnClickListener(cancelListener)
            binding.confirmSettingsButton.setOnClickListener(confirmListener)

            binding.cdEasy.setOnClickListener {
                difficulty = Difficulty.EASY
                updateButtons()
            }
            binding.cdMedium.setOnClickListener {
                difficulty = Difficulty.MEDIUM
                updateButtons()
            }
            binding.cdHard.setOnClickListener {
                difficulty = Difficulty.HARD
                updateButtons()
            }

            binding.floppyBlue.setOnClickListener {
                playerIcon = PlayerIcon.BLUE
                updateButtons()
            }
            binding.floppyRed.setOnClickListener {
                playerIcon = PlayerIcon.RED
                updateButtons()
            }
            binding.floppyGreen.setOnClickListener {
                playerIcon = PlayerIcon.GREEN
                updateButtons()
            }

            loadPreferences()
            updateButtons()

            builder
                .setView(binding.root)
                .create()
        } ?: throw IllegalStateException("No activity reference!")
    }
}