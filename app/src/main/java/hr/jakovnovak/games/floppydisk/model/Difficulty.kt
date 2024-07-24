package hr.jakovnovak.games.floppydisk.model

/**
 * @param num number of cds that can spawn at any given time
 */
enum class Difficulty(val num : Int) {
    EASY(1),
    MEDIUM(3),
    HARD(5)
}