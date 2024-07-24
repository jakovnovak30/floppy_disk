package hr.jakovnovak.games.floppydisk.model

import hr.jakovnovak.games.floppydisk.R

/**
 * @param resId resource id connected to the bitmap of the possible player icon
 */
enum class PlayerIcon(val resId : Int) {
    RED(R.drawable.floppy_disk_red),
    GREEN(R.drawable.floppy_disk_green),
    BLUE(R.drawable.floppy_disk)
}