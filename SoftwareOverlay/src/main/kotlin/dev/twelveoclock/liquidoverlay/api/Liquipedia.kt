package dev.twelveoclock.liquidoverlay.api

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.io.path.Path
import kotlin.io.path.readText

// TODO: Make sure it follows all api rules, maybe use a queue
object Liquipedia {

    const val API_URL = "https://api.liquipedia.net/api/v2/"

    const val userAgent = "User-Agent: LiquidOverlay/1.0 (camdenorrb@me.com)"


    val httpClient = HttpClient()


    private fun HttpRequestBuilder.defaultHeaders() {
        header("accept", "application/json")
        header("authorization", "Apikey ${Path("liquidToken.txt").readText()}")
    }


    suspend fun broadcasters(
        wiki: Wiki,
        conditions: String = "",
        query: String = "",
        limit: Int = -1,
        offset: Int = -1,
        order: String = "",
        groupBy: String = "",
    ): List<Broadcaster> {

        return Json.decodeFromString(
            ListSerializer(Broadcaster.serializer()),
            """{"result":[{"pageid":100182,"pagename":"Brewmaster's_Cup","namespace":0,"objectname":"100182_broadcaster_Gareth_Caster","id":"Gareth","name":"Gareth Bateson","page":"Gareth","position":"Caster","language":"","flag":"united kingdom","weight":1118,"date":"2019-04-30","extradata":{"status":""},"wiki":"dota2"},{"pageid":100182,"pagename":"Brewmaster's_Cup","namespace":0,"objectname":"100182_broadcaster_KheZu_Caster","id":"KheZu","name":"Maurice Gutmann","page":"KheZu","position":"Caster","language":"","flag":"germany","weight":1118,"date":"2019-04-30","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_4liver_Caster","id":"4liver","name":"Anton Pavliukovets","page":"4liver","position":"Caster","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_DkPhobos_Analyst","id":"DkPhobos","name":"Alexander Kucheria","page":"DkPhobos","position":"Analyst","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_KVYZEE_Caster","id":"KVYZEE","name":"Vladislav Kovalchuk","page":"KVYZEE","position":"Caster","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_LittleP1g_Observer","id":"LittleP1g","name":"Stas Kashpruk","page":"LittleP1g","position":"Observer","language":"ru","flag":"ua","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_Mag~_Analyst","id":"Mag~","name":"Andrey Chipenko","page":"Mag~","position":"Analyst","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_SUNSfan_Observer","id":"SUNSfan","name":"Shannon Scotten","page":"SUNSfan","position":"Observer","language":"en","flag":"united states","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_Sunlight_Analyst","id":"Sunlight","name":"Kirill Kachinsky","page":"Sunlight","position":"Analyst","language":"ru","flag":"belarus","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_Tekcac_Caster","id":"Tekcac","name":"Yaroslav Petrushyn","page":"Tekcac","position":"Caster","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_TrentPax_Commentator","id":"TrentPax","name":"Trent MacKenzie","page":"TrentPax","position":"Commentator","language":"en","flag":"canada","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_WarLock_Host","id":"WarLock","name":"Anton Tokarev","page":"WarLock","position":"Host","language":"ru","flag":"ua","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_Zyori_Commentator","id":"Zyori","name":"Andrew Campbell","page":"Zyori","position":"Commentator","language":"en","flag":"united states","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_jenyashy_Observer","id":"jenyashy","name":"Eugene Shytikov","page":"Jenyashy","position":"Observer","language":"ru","flag":"ua","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_md_Observer","id":"md","name":"Dmitry Melnychenko","page":"Md","position":"Observer","language":"ru","flag":"ua","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100543,"pagename":"WePlay\/Tug_of_War\/Dire\/America","namespace":0,"objectname":"100543_broadcaster_4liver_Caster","id":"4liver","name":"Anton Pavliukovets","page":"4liver","position":"Caster","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100543,"pagename":"WePlay\/Tug_of_War\/Dire\/America","namespace":0,"objectname":"100543_broadcaster_DkPhobos_Analyst","id":"DkPhobos","name":"Alexander Kucheria","page":"DkPhobos","position":"Analyst","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100543,"pagename":"WePlay\/Tug_of_War\/Dire\/America","namespace":0,"objectname":"100543_broadcaster_KVYZEE_Caster","id":"KVYZEE","name":"Vladislav Kovalchuk","page":"KVYZEE","position":"Caster","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100543,"pagename":"WePlay\/Tug_of_War\/Dire\/America","namespace":0,"objectname":"100543_broadcaster_LittleP1g_Observer","id":"LittleP1g","name":"Stas Kashpruk","page":"LittleP1g","position":"Observer","language":"ru","flag":"ua","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100543,"pagename":"WePlay\/Tug_of_War\/Dire\/America","namespace":0,"objectname":"100543_broadcaster_Mag~_Analyst","id":"Mag~","name":"Andrey Chipenko","page":"Mag~","position":"Analyst","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"}]}"""
        )

        /*
        httpClient.get<String>("${API_URL}broadcasters") {

            parameter("wiki", wiki.apiName)
            if (conditions.isNotBlank()) parameter("conditions", conditions)
            if (query.isNotBlank()) parameter("query", query)
            if (limit > -1) parameter("limit", limit)
            if (offset > -1) parameter("offset", offset)
            if (order.isNotBlank()) parameter("order", order)
            if (groupBy.isNotBlank()) parameter("groupby", groupBy)

            userAgent(userAgent)
            defaultHeaders()
        }
        */


        return emptyList()
    }


    enum class Wiki(val apiName: String) {
        DOTA_2("dota2")
    }

    data class Player(
        val userID: String,
        val realName: String,
        val teamID: String,
        val socialLinks: List<String>,
    )

    @Serializable
    data class Broadcaster(
        val name: String,
        val id: String,
        val position: String,
    )

    /*

    object Dota {

        /**
         * TODO
         *
         * @return The players for Dota
         */
        fun getPlayers(): Map<String, Player> {
            TODO()
        }

        fun getTeams() {
            TODO()
        }

    }

    object CounterStrike
    */
}