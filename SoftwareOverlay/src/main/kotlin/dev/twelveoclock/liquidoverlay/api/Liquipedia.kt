package dev.twelveoclock.liquidoverlay.api

import dev.twelveoclock.liquidoverlay.serializer.LocalDateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.LocalDate
import java.util.zip.GZIPInputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// TODO: Make sure it follows all api rules, maybe use a queue
// TODO: Figure out webhooks
// https://api.liquipedia.net/
// https://api.liquipedia.net/documentation/api/v2/openapi
// https://api.liquipedia.net/documentation/api/v2
class Liquipedia(private val apiKey: String) {

    suspend fun broadcasters(
        wikis: List<Wiki>,
        conditions: String = "",
        query: String = "",
        limit: Int = -1,
        offset: Int = -1,
        order: String = "",
        groupBy: String = "",
    ): Result.BroadcasterResult {
        return json.decodeFromString(
            Result.BroadcasterResult.serializer(),
            apiRequest("broadcasters", wikis, conditions, query, limit, offset, order, groupBy)
        )
    }

    suspend fun companies(
        wikis: List<Wiki>,
        conditions: String = "",
        query: String = "",
        limit: Int = -1,
        offset: Int = -1,
        order: String = "",
        groupBy: String = "",
    ): Result.CompanyResult {
        return json.decodeFromString(
            Result.CompanyResult.serializer(),
            apiRequest("company", wikis, conditions, query, limit, offset, order, groupBy)
        )
    }

    suspend fun datapoint(
        wikis: List<Wiki>,
        conditions: String = "",
        query: String = "",
        limit: Int = -1,
        offset: Int = -1,
        order: String = "",
        groupBy: String = "",
    ): Result.DatapointResult {
        return json.decodeFromString(
            Result.DatapointResult.serializer(),
            apiRequest("datapoint", wikis, conditions, query, limit, offset, order, groupBy)
        )
    }

    suspend fun externalMediaLink(
        wikis: List<Wiki>,
        conditions: String = "",
        query: String = "",
        limit: Int = -1,
        offset: Int = -1,
        order: String = "",
        groupBy: String = "",
    ): Result.ExternalMediaLinkResult {
        return json.decodeFromString(
            Result.ExternalMediaLinkResult.serializer(),
            apiRequest("externalmedialink", wikis, conditions, query, limit, offset, order, groupBy)
        )
    }

    suspend fun gear(
        wikis: List<Wiki>,
        conditions: String = "",
        query: String = "",
        limit: Int = -1,
        offset: Int = -1,
        order: String = "",
        groupBy: String = "",
    ): Result.GearsResult {
        return json.decodeFromString(
            Result.GearsResult.serializer(),
            apiRequest("gear", wikis, conditions, query, limit, offset, order, groupBy)
        )
    }

    suspend fun match(
        wikis: List<Wiki>,
        conditions: String = "",
        query: String = "",
        limit: Int = -1,
        offset: Int = -1,
        order: String = "",
        groupBy: String = "",
    ): Result.MatchResult {
        return json.decodeFromString(
            Result.MatchResult.serializer(),
            apiRequest("match", wikis, conditions, query, limit, offset, order, groupBy)
        )
    }

    suspend fun organization(
        wikis: List<Wiki>,
        conditions: String = "",
        query: String = "",
        limit: Int = -1,
        offset: Int = -1,
        order: String = "",
        groupBy: String = "",
    ): Result.OrganizationResult {
        return json.decodeFromString(
            Result.OrganizationResult.serializer(),
            apiRequest("organization", wikis, conditions, query, limit, offset, order, groupBy)
        )
    }

    suspend fun placement(
        wikis: List<Wiki>,
        conditions: String = "",
        query: String = "",
        limit: Int = -1,
        offset: Int = -1,
        order: String = "",
        groupBy: String = "",
    ): Result.PlacementResult {
        return json.decodeFromString(
            Result.PlacementResult.serializer(),
            apiRequest("placement", wikis, conditions, query, limit, offset, order, groupBy)
        )
    }

    suspend fun player(
        wikis: List<Wiki>,
        conditions: String = "",
        query: String = "",
        limit: Int = -1,
        offset: Int = -1,
        order: String = "",
        groupBy: String = "",
    ): Result.PlayerResult {
        return json.decodeFromString(
            Result.PlayerResult.serializer(),
            apiRequest("player", wikis, conditions, query, limit, offset, order, groupBy)
        )
    }

    suspend fun series(
        wikis: List<Wiki>,
        conditions: String = "",
        query: String = "",
        limit: Int = -1,
        offset: Int = -1,
        order: String = "",
        groupBy: String = "",
    ): Result.SeriesResult {
        return json.decodeFromString(
            Result.SeriesResult.serializer(),
            apiRequest("series", wikis, conditions, query, limit, offset, order, groupBy)
        )
    }

    suspend fun settings(
        wikis: List<Wiki>,
        conditions: String = "",
        query: String = "",
        limit: Int = -1,
        offset: Int = -1,
        order: String = "",
        groupBy: String = "",
    ): Result.SettingsResult {
        return json.decodeFromString(
            Result.SettingsResult.serializer(),
            apiRequest("settings", wikis, conditions, query, limit, offset, order, groupBy)
        )
    }

    suspend fun squadPlayer(
        wikis: List<Wiki>,
        conditions: String = "",
        query: String = "",
        limit: Int = -1,
        offset: Int = -1,
        order: String = "",
        groupBy: String = "",
    ): Result.SquadPlayerResult {
        return json.decodeFromString(
            Result.SquadPlayerResult.serializer(),
            apiRequest("squadplayer", wikis, conditions, query, limit, offset, order, groupBy)
        )
    }

    suspend fun team(
        wikis: List<Wiki>,
        conditions: String = "",
        query: String = "",
        limit: Int = -1,
        offset: Int = -1,
        order: String = "",
        groupBy: String = "",
    ): Result.TeamResult {
        return json.decodeFromString(
            Result.TeamResult.serializer(),
            apiRequest("team", wikis, conditions, query, limit, offset, order, groupBy)
        )
    }

    suspend fun tournament(
        wikis: List<Wiki>,
        conditions: String = "",
        query: String = "",
        limit: Int = -1,
        offset: Int = -1,
        order: String = "",
        groupBy: String = "",
    ): Result.TournamentResult {
        return json.decodeFromString(
            Result.TournamentResult.serializer(),
            apiRequest("tournament", wikis, conditions, query, limit, offset, order, groupBy)
        )
    }

    suspend fun transfer(
        wikis: List<Wiki>,
        conditions: String = "",
        query: String = "",
        limit: Int = -1,
        offset: Int = -1,
        order: String = "",
        groupBy: String = "",
    ): Result.TransferResult {
        return json.decodeFromString(
            Result.TransferResult.serializer(),
            apiRequest("transfer", wikis, conditions, query, limit, offset, order, groupBy)
        )
    }

    private suspend fun apiRequest(
        path: String,
        wikis: List<Wiki>,
        conditions: String = "",
        query: String = "",
        limit: Int = -1,
        offset: Int = -1,
        order: String = "",
        groupBy: String = "",
    ): String {
        return request("$API_URL$path",
            "wiki" to wikis.joinToString("|") { it.apiName },
            ("conditions" to conditions).takeIf { conditions.isNotBlank() },
            ("query" to query).takeIf { query.isNotBlank() },
            ("limit" to limit).takeIf { limit > -1 },
            ("offset" to offset).takeIf { offset > -1 },
            ("order" to order).takeIf { order.isNotBlank() },
            ("groupby" to groupBy).takeIf { order.isNotBlank() },
        )//.also { println("$it\n\n\n\n\n\n\n\n\n") }
    }


    private suspend fun request(url: String, vararg parameters: Pair<String, Any?>?): String {

        val urlWithParameters = buildString {

            append(url)

            if (parameters.isNotEmpty()) {

                append('?')

                append(parameters.filterNotNull().joinToString("&") { (key, value) ->
                    "$key=${URLEncoder.encode("$value", StandardCharsets.UTF_8)}"
                })
            }
        }


        println(urlWithParameters)

        val httpRequest = HttpRequest.newBuilder()
            .uri(URI(urlWithParameters))
            .setHeader("User-Agent", USER_AGENT)
            .setHeader("accept", "application/json")
            .setHeader("Accept-Encoding", "gzip")
            .setHeader("authorization", "Apikey $apiKey")
            .timeout(Duration.ofSeconds(30))
            .GET()
            .build()

        return suspendCoroutine { continuation ->
            httpClient
                .sendAsync(httpRequest) {
                    HttpResponse.BodySubscribers.ofInputStream()
                }
                .thenAccept {
                    @Suppress("BlockingMethodInNonBlockingContext")
                    continuation.resume(GZIPInputStream(it.body()).readAllBytes().decodeToString())
                }
        }

    }


    companion object {

        const val API_URL = "https://api.liquipedia.net/api/v2/"

        const val USER_AGENT = "LiquidOverlay/1.0 (liquidoverlay@12oclock.dev)"


        val httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build()!!

        val json = Json {

            //isLenient = true
            //coerceInputValues = true
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

    sealed class Result {

        @Serializable
        data class BroadcasterResult(
            val result: List<Broadcaster>
        ) : Result()

        @Serializable
        data class CompanyResult(
            val result: List<Company>
        ) : Result()

        @Serializable
        data class DatapointResult(
            val result: List<Datapoint>
        ) : Result()

        @Serializable
        data class ExternalMediaLinkResult(
            val result: List<ExternalMediaLink>
        ) : Result()

        @Serializable
        data class GearsResult(
            val result: List<Gear>
        ) : Result()

        @Serializable
        data class MatchResult(
            val result: List<Match>
        ) : Result()

        @Serializable
        data class OrganizationResult(
            val result: List<Organization>
        ) : Result()

        @Serializable
        data class PlacementResult(
            val result: List<Placement>
        ) : Result()

        @Serializable
        data class PlayerResult(
            val result: List<Player>
        ) : Result()

        @Serializable
        data class SeriesResult(
            val result: List<Series>
        ) : Result()

        @Serializable
        data class SettingsResult(
            val result: List<Settings>
        ) : Result()

        @Serializable
        data class SquadPlayerResult(
            val result: List<SquadPlayer>
        ) : Result()

        @Serializable
        data class TeamResult(
            val result: List<Team>
        ) : Result()

        @Serializable
        data class TournamentResult(
            val result: List<Tournament>
        ) : Result()

        @Serializable
        data class TransferResult(
            val result: List<Transfer>
        ) : Result()

    }

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
        val weight: Float,
        @Serializable(LocalDateSerializer::class) val date: LocalDate,
        @SerialName("extradata") val extraData: JsonObject?,
        val wiki: String,
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
        val links: JsonObject?,
        @SerialName("extradata") val extraData: JsonObject?,
        val wiki: String,
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
        @SerialName("extradata") val extraData: JsonObject?,
        val wiki: String,
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
        val authors: JsonObject?,
        val language: String,
        val publisher: String,
        val type: String,
        @SerialName("extradata") val extraData: JsonObject?,
        val wiki: String,
    )

    @Serializable
    data class Gear(
        @SerialName("pageid") val pageID: Int,
        @SerialName("pagename") val pageName: String,
        val namespace: Int,
        @SerialName("objectname") val objectName: String,
        @Serializable(LocalDateSerializer::class) val date: LocalDate,
        val reference: String,
        val input: JsonObject?,
        val display: JsonObject?,
        val audio: JsonObject?,
        val chair: JsonObject?,
        @SerialName("extradata") val extraData: JsonObject?,
        val wiki: String,
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
        val finished: Byte,
        val mode: String,
        val type: String,
        val game: String,
        val links: JsonElement?,
        @SerialName("bestof") val bestOf: String,
        @Serializable(LocalDateSerializer::class) val date: LocalDate,
        @SerialName("dateexact") val dateExact: Byte,
        val stream: JsonElement?,
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
        @SerialName("extradata") val extraData: JsonObject?,
        @SerialName("match2bracketdata") val matchToBracketData: JsonObject?,
        val wiki: String,
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
        val wiki: String,
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
        val players: JsonElement,
        val placement: String,
        @SerialName("prizemoney") val prizeMoney: Float,
        val weight: Float,
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
        val qualified: Byte,
        @SerialName("extradata") val extraData: JsonObject?,
        val wiki: String,
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
        val links: JsonObject?,
        val status: String,
        val earnings: Float,
        @SerialName("extradata") val extraData: JsonObject?,
        val wiki: String,
    )

    @Serializable
    data class Series(
        @SerialName("pageid") val pageID: Int,
        @SerialName("pagename") val pageName: String,
        val namespace: Int,
        @SerialName("objectname") val objectName: String,
        val name: String,
        val abbreviation: String,
        val image: String,
        @SerialName("imageurl") val imageURL: String,
        val icon: String,
        @SerialName("iconurl") val iconURL: String,
        val game: String,
        val type: String,
        val previous: String,
        val previous2: String,
        val next: String,
        val next2: String,
        val organizers: JsonElement?,
        val location: String,
        @SerialName("prizepool") val prizePool: Float,
        @SerialName("liquipediatier") val liquipediaTier: String,
        @SerialName("liquipediatiertype") val liquipediaTierType: String,
        @SerialName("publishertier") val publisherTier: String,
        //@SerialName("foundeddate") @Serializable(LocalDateSerializer::class) val foundedDate: LocalDate,
        @SerialName("defunctdate") @Serializable(LocalDateSerializer::class) val defunctDate: LocalDate,
        val sponsors: JsonElement?,
        val links: JsonElement?,
        @SerialName("extradata") val extraData: JsonElement?,
        val wiki: String,
    )

    @Serializable
    data class Settings(
        @SerialName("pageid") val pageID: Int,
        @SerialName("pagename") val pageName: String,
        val namespace: Int,
        @SerialName("objectname") val objectName: String,
        val name: String,
        val reference: String,
        @SerialName("lastupdated") @Serializable(LocalDateSerializer::class) val lastUpdated: LocalDate,
        val keys: JsonObject?,
        @SerialName("gamesettings") val gameSettings: JsonObject?,
        @SerialName("viewsettings") val viewSettings: JsonObject?,
        val type: String,
        val wiki: String,
    )

    @Serializable
    data class SquadPlayer(
        @SerialName("pageid") val pageID: Int,
        @SerialName("pagename") val pageName: String,
        val namespace: Int,
        @SerialName("objectname") val objectName: String,
        val id: String,
        val link: String,
        val name: String,
        val nationality: String,
        val position: String,
        val role: String,
        @SerialName("newteam") val newTeam: String,
        @SerialName("joindate") @Serializable(LocalDateSerializer::class) val joinDate: LocalDate,
        @SerialName("joindateref") val joinDateRef: String,
        @SerialName("leavedate") @Serializable(LocalDateSerializer::class) val leaveDate: LocalDate,
        @SerialName("leavedateref") val leaveDateRef: String,
        @SerialName("inactivedate") @Serializable(LocalDateSerializer::class) val inactiveDate: LocalDate,
        @SerialName("inactivedateref") val inactiveDateRef: String,
        @SerialName("extradata") val extraData: JsonObject?,
        val wiki: String,
    )

    @Serializable
    data class Team(
        @SerialName("pageid") val pageID: Int,
        @SerialName("pagename") val pageName: String,
        val namespace: Int,
        @SerialName("objectname") val objectName: String,
        val name: String,
        val location: String,
        val location2: String,
        val region: String,
        val logo: String,
        @SerialName("logourl") val logoURL: String,
        @SerialName("createdate") @Serializable(LocalDateSerializer::class) val createDate: LocalDate,
        @SerialName("disbanddate") @Serializable(LocalDateSerializer::class) val disbandDate: LocalDate,
        val earnings: Float,
        val coach: String,
        val manager: String,
        val sponsors: String,
        @SerialName("extradata") val extraData: JsonElement?,
        val wiki: String,
    )

    @Serializable
    data class Tournament(
        @SerialName("pageid") val pageID: Int,
        @SerialName("pagename") val pageName: String,
        val namespace: Int,
        @SerialName("objectname") val objectName: String,
        val name: String,
        @SerialName("shortname") val shortName: String,
        @SerialName("tickername") val tickerName: String,
        val banner: String,
        @SerialName("bannerurl") val bannerURL: String,
        val icon: String,
        @SerialName("iconurl") val iconURL: String,
        val series: String,
        val previous: String,
        val previous2: String,
        val next: String,
        val next2: String,
        val game: String,
        val patch: String,
        @SerialName("endpatch") val endPatch: String,
        val type: String,
        val organizers: String,
        @SerialName("startdate") @Serializable(LocalDateSerializer::class) val startDate: LocalDate,
        @SerialName("enddate") @Serializable(LocalDateSerializer::class) val endDate: LocalDate,
        @SerialName("sortdate") @Serializable(LocalDateSerializer::class) val sortDate: LocalDate,
        val location: String,
        val location2: String,
        val venue: String,
        @SerialName("prizepool") val prizePool: Float,
        @SerialName("participantsnumber") val participantsNumber: Int,
        @SerialName("liquipediatier") val liquipediaTier: String,
        @SerialName("liquipediatiertype") val liquipediaTierType: String,
        @SerialName("publishertier") val publisherTier: String,
        val status: String,
        val maps: String,
        val format: String,
        val sponsors: String,
        @SerialName("extradata") val extraData: JsonObject?,
        val wiki: String,
    )

    @Serializable
    data class Transfer(
        @SerialName("pageid") val pageID: Int,
        @SerialName("pagename") val pageName: String,
        val namespace: Int,
        @SerialName("objectname") val objectName: String,
        val player: String,
        val nationality: String,
        @SerialName("fromteam") val fromTeam: String,
        @SerialName("toteam") val toTeam: String,
        val role1: String,
        val role2: String,
        val reference: JsonObject,
        @Serializable(LocalDateSerializer::class) val date: LocalDate,
        @SerialName("wholeteam") val wholeTeam: Byte,
        @SerialName("extradata") val extraData: JsonObject?,
        val wiki: String,
    )

}