package dev.twelveoclock.liquidoverlay.api

import dev.twelveoclock.liquidoverlay.serializer.LocalDateSerializer
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.time.LocalDate
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.readText

// TODO: Make sure it follows all api rules, maybe use a queue
// TODO: Figure out webhooks
// https://api.liquipedia.net/
// https://api.liquipedia.net/documentation/api/v2/openapi
class Liquipedia {



    private fun HttpRequestBuilder.defaultHeaders() {
        header("accept", "application/json")
        header("authorization", "Apikey ${Path("liquidToken.txt").readText()}")
    }

    suspend fun broadcasters(
        wikis: List<Wiki>,
        conditions: String = "",
        query: String = "",
        limit: Int = -1,
        offset: Int = -1,
        order: String = "",
        groupBy: String = "",
    ): BroadcastersResult {

        // Example result
        return json.decodeFromString(
            BroadcastersResult.serializer(),
            """{"result":[{"pageid":100182,"pagename":"Brewmaster's_Cup","namespace":0,"objectname":"100182_broadcaster_Gareth_Caster","id":"Gareth","name":"Gareth Bateson","page":"Gareth","position":"Caster","language":"","flag":"united kingdom","weight":1118,"date":"2019-04-30","extradata":{"status":""},"wiki":"dota2"},{"pageid":100182,"pagename":"Brewmaster's_Cup","namespace":0,"objectname":"100182_broadcaster_KheZu_Caster","id":"KheZu","name":"Maurice Gutmann","page":"KheZu","position":"Caster","language":"","flag":"germany","weight":1118,"date":"2019-04-30","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_4liver_Caster","id":"4liver","name":"Anton Pavliukovets","page":"4liver","position":"Caster","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_DkPhobos_Analyst","id":"DkPhobos","name":"Alexander Kucheria","page":"DkPhobos","position":"Analyst","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_KVYZEE_Caster","id":"KVYZEE","name":"Vladislav Kovalchuk","page":"KVYZEE","position":"Caster","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_LittleP1g_Observer","id":"LittleP1g","name":"Stas Kashpruk","page":"LittleP1g","position":"Observer","language":"ru","flag":"ua","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_Mag~_Analyst","id":"Mag~","name":"Andrey Chipenko","page":"Mag~","position":"Analyst","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_SUNSfan_Observer","id":"SUNSfan","name":"Shannon Scotten","page":"SUNSfan","position":"Observer","language":"en","flag":"united states","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_Sunlight_Analyst","id":"Sunlight","name":"Kirill Kachinsky","page":"Sunlight","position":"Analyst","language":"ru","flag":"belarus","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_Tekcac_Caster","id":"Tekcac","name":"Yaroslav Petrushyn","page":"Tekcac","position":"Caster","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_TrentPax_Commentator","id":"TrentPax","name":"Trent MacKenzie","page":"TrentPax","position":"Commentator","language":"en","flag":"canada","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_WarLock_Host","id":"WarLock","name":"Anton Tokarev","page":"WarLock","position":"Host","language":"ru","flag":"ua","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_Zyori_Commentator","id":"Zyori","name":"Andrew Campbell","page":"Zyori","position":"Commentator","language":"en","flag":"united states","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_jenyashy_Observer","id":"jenyashy","name":"Eugene Shytikov","page":"Jenyashy","position":"Observer","language":"ru","flag":"ua","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_md_Observer","id":"md","name":"Dmitry Melnychenko","page":"Md","position":"Observer","language":"ru","flag":"ua","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100543,"pagename":"WePlay\/Tug_of_War\/Dire\/America","namespace":0,"objectname":"100543_broadcaster_4liver_Caster","id":"4liver","name":"Anton Pavliukovets","page":"4liver","position":"Caster","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100543,"pagename":"WePlay\/Tug_of_War\/Dire\/America","namespace":0,"objectname":"100543_broadcaster_DkPhobos_Analyst","id":"DkPhobos","name":"Alexander Kucheria","page":"DkPhobos","position":"Analyst","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100543,"pagename":"WePlay\/Tug_of_War\/Dire\/America","namespace":0,"objectname":"100543_broadcaster_KVYZEE_Caster","id":"KVYZEE","name":"Vladislav Kovalchuk","page":"KVYZEE","position":"Caster","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100543,"pagename":"WePlay\/Tug_of_War\/Dire\/America","namespace":0,"objectname":"100543_broadcaster_LittleP1g_Observer","id":"LittleP1g","name":"Stas Kashpruk","page":"LittleP1g","position":"Observer","language":"ru","flag":"ua","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100543,"pagename":"WePlay\/Tug_of_War\/Dire\/America","namespace":0,"objectname":"100543_broadcaster_Mag~_Analyst","id":"Mag~","name":"Andrey Chipenko","page":"Mag~","position":"Analyst","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"}]}"""
        )

        /*
        return json.decodeFromString(
            httpClient.get("${API_URL}broadcasters") {
                parameter("wiki", wikis.joinToString("|") { it.apiName })
                if (conditions.isNotBlank()) parameter("conditions", conditions)
                if (query.isNotBlank()) parameter("query", query)
                if (limit > -1) parameter("limit", limit)
                if (offset > -1) parameter("offset", offset)
                if (order.isNotBlank()) parameter("order", order)
                if (groupBy.isNotBlank()) parameter("groupby", groupBy)

                userAgent(userAgent)
                defaultHeaders()
            }
        )
        */
    }

    suspend fun companies(
        wikis: List<Wiki>,
        conditions: String = "",
        query: String = "",
        limit: Int = -1,
        offset: Int = -1,
        order: String = "",
        groupBy: String = "",
    ): CompaniesResult {

        // Example result
        /*
        return json.decodeFromString(
            BroadcastersResult.serializer(),
            """{"result":[{"pageid":100182,"pagename":"Brewmaster's_Cup","namespace":0,"objectname":"100182_broadcaster_Gareth_Caster","id":"Gareth","name":"Gareth Bateson","page":"Gareth","position":"Caster","language":"","flag":"united kingdom","weight":1118,"date":"2019-04-30","extradata":{"status":""},"wiki":"dota2"},{"pageid":100182,"pagename":"Brewmaster's_Cup","namespace":0,"objectname":"100182_broadcaster_KheZu_Caster","id":"KheZu","name":"Maurice Gutmann","page":"KheZu","position":"Caster","language":"","flag":"germany","weight":1118,"date":"2019-04-30","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_4liver_Caster","id":"4liver","name":"Anton Pavliukovets","page":"4liver","position":"Caster","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_DkPhobos_Analyst","id":"DkPhobos","name":"Alexander Kucheria","page":"DkPhobos","position":"Analyst","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_KVYZEE_Caster","id":"KVYZEE","name":"Vladislav Kovalchuk","page":"KVYZEE","position":"Caster","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_LittleP1g_Observer","id":"LittleP1g","name":"Stas Kashpruk","page":"LittleP1g","position":"Observer","language":"ru","flag":"ua","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_Mag~_Analyst","id":"Mag~","name":"Andrey Chipenko","page":"Mag~","position":"Analyst","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_SUNSfan_Observer","id":"SUNSfan","name":"Shannon Scotten","page":"SUNSfan","position":"Observer","language":"en","flag":"united states","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_Sunlight_Analyst","id":"Sunlight","name":"Kirill Kachinsky","page":"Sunlight","position":"Analyst","language":"ru","flag":"belarus","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_Tekcac_Caster","id":"Tekcac","name":"Yaroslav Petrushyn","page":"Tekcac","position":"Caster","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_TrentPax_Commentator","id":"TrentPax","name":"Trent MacKenzie","page":"TrentPax","position":"Commentator","language":"en","flag":"canada","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_WarLock_Host","id":"WarLock","name":"Anton Tokarev","page":"WarLock","position":"Host","language":"ru","flag":"ua","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_Zyori_Commentator","id":"Zyori","name":"Andrew Campbell","page":"Zyori","position":"Commentator","language":"en","flag":"united states","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_jenyashy_Observer","id":"jenyashy","name":"Eugene Shytikov","page":"Jenyashy","position":"Observer","language":"ru","flag":"ua","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100542,"pagename":"WePlay\/Tug_of_War\/Dire\/Asia","namespace":0,"objectname":"100542_broadcaster_md_Observer","id":"md","name":"Dmitry Melnychenko","page":"Md","position":"Observer","language":"ru","flag":"ua","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100543,"pagename":"WePlay\/Tug_of_War\/Dire\/America","namespace":0,"objectname":"100543_broadcaster_4liver_Caster","id":"4liver","name":"Anton Pavliukovets","page":"4liver","position":"Caster","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100543,"pagename":"WePlay\/Tug_of_War\/Dire\/America","namespace":0,"objectname":"100543_broadcaster_DkPhobos_Analyst","id":"DkPhobos","name":"Alexander Kucheria","page":"DkPhobos","position":"Analyst","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100543,"pagename":"WePlay\/Tug_of_War\/Dire\/America","namespace":0,"objectname":"100543_broadcaster_KVYZEE_Caster","id":"KVYZEE","name":"Vladislav Kovalchuk","page":"KVYZEE","position":"Caster","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100543,"pagename":"WePlay\/Tug_of_War\/Dire\/America","namespace":0,"objectname":"100543_broadcaster_LittleP1g_Observer","id":"LittleP1g","name":"Stas Kashpruk","page":"LittleP1g","position":"Observer","language":"ru","flag":"ua","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"},{"pageid":100543,"pagename":"WePlay\/Tug_of_War\/Dire\/America","namespace":0,"objectname":"100543_broadcaster_Mag~_Analyst","id":"Mag~","name":"Andrey Chipenko","page":"Mag~","position":"Analyst","language":"ru","flag":"ukraine","weight":30000,"date":"2019-06-02","extradata":{"status":""},"wiki":"dota2"}]}"""
        )
        */


        /*
        return json.decodeFromString(
            httpClient.get("${API_URL}broadcasters") {

                parameter("wiki", wiki.apiName)
                if (conditions.isNotBlank()) parameter("conditions", conditions)
                if (query.isNotBlank()) parameter("query", query)
                if (limit > -1) parameter("limit", limit)
                if (offset > -1) parameter("offset", offset)
                if (order.isNotBlank()) parameter("order", order)
                if (groupBy.isNotBlank()) parameter("groupby", groupBy)

                userAgent(USER_AGENT)
                defaultHeaders()
            }
        )
        */
        TODO()
    }



    companion object {

        const val API_URL = "https://api.liquipedia.net/api/v2/"

        const val USER_AGENT = "User-Agent: LiquidOverlay/1.0 (camdenorrb@me.com)"


        val httpClient = HttpClient()

        val json = Json {
            ignoreUnknownKeys = true
        }
    }

    enum class Wiki(val apiName: String) {

        DOTA_2("dota2"),
        COUNTER_STRIKE("counterstrike"),
        VALORANT("valorant"),
        PUBG("pubg"),
        ROCKET_LEAGUE("rocketleague"),
        STAR_CRAFT_2("starcraft2"),
        RAINBOW_SIX("rainbowsix"),
        APEX_LEGENDS("apexlegends"),
        LEAGUE_OF_LEGENDS("leagueoflegends"),
        OVERWATCH("overwatch"),
        AGE_OF_EMPIRES("ageofempires"),
        SMASH("smash"),
        WAR_CRAFT_3("warcraft"),
        WILD_RIFT("wildrift"),
        BROOD_WAR("starcraft"),
        HEARTHSTONE("hearthstone"),
        HEROES("heroes"),
        ARTIFACT("artifact"),
        ;

    }

    @Serializable
    data class BroadcastersResult(
        val result: List<Broadcaster>
    )
    @Serializable
    data class CompaniesResult(
        val result: List<Company>
    )


    @Serializable
    data class Broadcaster(
        @SerialName("pageid") val pageID: Int,
        @SerialName("pagename") val pageName: String,
        val namespace: Int,
        @SerialName("objectname") val objectName: String,
        val id: String,
        val name: String,
        val page: String,
        val position: String,
        val language: String,
        val flag: String,
        val weight: Int,
        @Serializable(LocalDateSerializer::class) val date: LocalDate,
        @SerialName("extradata") val extraData: JsonObject
    )

    @Serializable
    data class Company(
        @SerialName("pageid") val pageID: Int,
        @SerialName("pagename") val pageName: String,
        val namespace: Int,
        @SerialName("objectname") val objectName: String,
        val name: String,
        val image: String,
        @SerialName("imageurl") val imageURL: String,
        val location: String,
        @SerialName("headquarterslocation") val headquartersLocation: String,
        @SerialName("parentcompany") val parentCompany: String,
        @SerialName("sistercompany") val sisterCompany: String,
        val industry: String,
        @SerialName("foundeddate") @Serializable(LocalDateSerializer::class) val foundedDate: LocalDate,
        @SerialName("defunctdate") @Serializable(LocalDateSerializer::class) val defunctDate: LocalDate,
        @SerialName("numberofemployees") val numberOfEmployees: String,
        val links: JsonObject,
        @SerialName("extradata") val extraData: JsonObject,
    )

    @Serializable
    data class Datapoint(
        @SerialName("pageid") val pageID: Int,
        @SerialName("pagename") val pageName: String,
        val namespace: Int,
        @SerialName("objectname") val objectName: String,
        val type: String,
        val name: String,
        val information: String,
        val image: String,
        @SerialName("imageurl") val imageURL: String,
        @Serializable(LocalDateSerializer::class) val date: LocalDate,
        @SerialName("extradata") val extraData: JsonObject,
    )

    @Serializable
    data class ExternalMediaLink(
        @SerialName("pageid") val pageID: Int,
        @SerialName("pagename") val pageName: String,
        val namespace: Int,
        @SerialName("objectname") val objectName: String,
        val title: String,
        @SerialName("translatedtitle") val translatedTitle: String,
        val link: String,
        @Serializable(LocalDateSerializer::class) val date: LocalDate,
        val authors: JsonObject,
        val language: String,
        val publisher: String,
        val type: String,
        @SerialName("extradata") val extraData: JsonObject,
    )

    @Serializable
    data class Gear(
        @SerialName("pageid") val pageID: Int,
        @SerialName("pagename") val pageName: String,
        val namespace: Int,
        @SerialName("objectname") val objectName: String,
        @Serializable(LocalDateSerializer::class) val date: LocalDate,
        val reference: String,
        val input: JsonObject,
        val display: JsonObject,
        val audio: JsonObject,
        val chair: JsonObject,
        @SerialName("extradata") val extraData: JsonObject,
    )

    @Serializable
    data class Match(
        @SerialName("pageid") val pageID: Int,
        @SerialName("pagename") val pageName: String,
        val namespace: Int,
        @SerialName("objectname") val objectName: String,
        @SerialName("match2id") val matchToID: String,
        @SerialName("match2bracketid") val matchToBracketID: String,
        val winner: String,
        @SerialName("walkover") val walkOver: String,
        @SerialName("resulttype") val resultType: String,
        val finished: Boolean,
        val mode: String,
        val type: String,
        val game: String,
        val links: JsonObject,
        @SerialName("bestof") val bestOf: String,
        @Serializable(LocalDateSerializer::class) val date: LocalDate,
        @SerialName("dateexact") val dateExact: Boolean,
        val stream: JsonObject,
        @SerialName("lrthread") val lrThread: String,
        val vod: String,
        val tournament: String,
        val parent: String,
        @SerialName("parentname") val parentName: String,
        @SerialName("tickername") val tickerName: String,
        @SerialName("shortname") val shortName: String,
        val series: String,
        val icon: String,
        @SerialName("iconurl") val iconUrl: String,
        @SerialName("liquipediatier") val liquipediaTier: String,
        @SerialName("liquipediatiertype") val liquipediaTierType: String,
        @SerialName("publishertier") val publisherTier: String,
        @SerialName("extradata") val extraData: JsonObject,
        @SerialName("match2bracketdata") val matchToBracketData: JsonObject,
    )

    @Serializable
    data class Organization(
        @SerialName("pageid") val pageID: Int,
        @SerialName("pagename") val pageName: String,
        val namespace: Int,
        @SerialName("objectname") val objectName: String,
        val id: String,
        val page: String,
        val team: String,
        @SerialName("teampage") val teamPage: String,
        val position: String,
        val active: String,
    )

    @Serializable
    data class Placement(
        @SerialName("pageid") val pageID: Int,
        @SerialName("pagename") val pageName: String,
        val namespace: Int,
        @SerialName("objectname") val objectName: String,
        val tournament: String,
        val series: String,
        val parent: String,
        @SerialName("parentname") val parentName: String,
        @SerialName("shortname") val shortName: String,
        val image: String,
        @SerialName("imageurl") val imageURL: String,
        @SerialName("startdate") @Serializable(LocalDateSerializer::class) val startDate: LocalDate,
        @Serializable(LocalDateSerializer::class) val date: LocalDate,
        val participant: String,
        @SerialName("participantflag") val participantFlag: String,
        val players: JsonObject,
        val placement: String,
        @SerialName("prizemoney") val prizeMoney: Float,
        val weight: Int,
        val mode: String,
        val type: String,
        @SerialName("liquipediatier") val liquipediaTier: String,
        @SerialName("liquipediatiertype") val liquipediaTierType: String,
        @SerialName("publishertier") val publisherTier: String,
        val icon: String,
        val game: String,
        @SerialName("lastscore") val lastScore: String,
        @SerialName("lastvs") val lastVS: String,
        @SerialName("lastvsscore") val lastVSScore: String,
        @SerialName("groupscore") val groupScore: String,
        val qualified: Boolean,
        @SerialName("extradata") val extraData: JsonObject,
    )

    @Serializable
    data class Player(
        @SerialName("pageid") val pageID: Int,
        @SerialName("pagename") val pageName: String,
        val namespace: Int,
        @SerialName("objectname") val objectName: String,
        val id: String,
        @SerialName("alternateid") val alternateID: String,
        val name: String,
        @SerialName("romanizedname") val romanizedName: String,
        @SerialName("localizedname") val localizedName: String,
        val type: String,
        val nationality: String,
        val nationality2: String,
        val nationality3: String,
        val region: String,
        @SerialName("birthdate") @Serializable(LocalDateSerializer::class) val birthDate: LocalDate,
        @SerialName("deathdate") @Serializable(LocalDateSerializer::class) val deathDate: LocalDate,
        val team: String,
        val links: JsonObject,
        val status: String,
        val earnings: Float,
        @SerialName("extradata") val extraData: JsonObject,
    )

}