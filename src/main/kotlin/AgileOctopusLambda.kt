package cloud.drakon.agileoctopuslambda

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val ktorClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}

@Serializable
private class Results(val results: List<StandardUnitRate>)

@Serializable
private class StandardUnitRate(
    @SerialName("value_inc_vat") val valueIncVat: Double,
    @SerialName("valid_from") val validFrom: Instant,
    @SerialName("valid_to") val validTo: Instant,
)

private val timeZone = TimeZone.of("Europe/London")
private val datePeriod = DatePeriod(days = 1)

suspend fun main() {
    // TODO: Are we working out the start and end times correctly? https://developer.octopus.energy/docs/api/#agile-octopus
    val tomorrow = Clock.System.now()
        .toLocalDateTime(timeZone)
        .date
        .plus(datePeriod)

    val standardUnitRates = ktorClient.get("https://api.octopus.energy/v1/products/AGILE-18-02-21/electricity-tariffs/E-1R-AGILE-18-02-21-C/standard-unit-rates/") {
        parameter("period_from", tomorrow.atStartOfDayIn(timeZone))

        parameter(
            "period_to", tomorrow.plus(datePeriod)
                .atStartOfDayIn(timeZone)
        )
    }.body<Results>()
        .results
        .reversed()

    // TODO: Send SMS
    println(buildMap {
        for (i in 0..44) {
            put(
                "${standardUnitRates[i].validFrom.toLocalDateTime(timeZone).time} - ${standardUnitRates[i + 1].validTo.toLocalDateTime(timeZone).time}",
                standardUnitRates[i].valueIncVat + standardUnitRates[i + 1].valueIncVat
            )
        }
    }.minBy {
        it.value
    }.key)
}
