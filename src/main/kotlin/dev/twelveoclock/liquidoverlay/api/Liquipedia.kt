package dev.twelveoclock.liquidoverlay.api

// TODO: Make sure it follows all api rules, maybe use a queue
object Liquipedia {

    data class Player(
        val userID: String,
        val realName: String,
        val teamID: String,
        val socialLinks: List<String>,
    )

    object Dota {

        /**
         * TODO
         *
         * @return The players for Dota
         */
        fun getPlayers(): Map<String, Player> {

        }

        fun getTeams() {

        }

    }

    object CounterStrike

}