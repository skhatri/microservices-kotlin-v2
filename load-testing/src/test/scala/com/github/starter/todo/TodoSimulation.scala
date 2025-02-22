package com.github.starter.todo

import io.gatling.core.Predef._
import io.gatling.core.structure.PopulationBuilder
import io.gatling.http.Predef._

import scala.concurrent.duration._

object Todo {
  val server = Option(System.getenv("HOST")).getOrElse("localhost")
  val port = Option(System.getenv("PORT")).map(_.toInt).getOrElse(8080)
  val ssl = Option(System.getenv("SSL")).map(_.toBoolean).getOrElse(true)
  val http2Enabled = Option(System.getenv("HTTP2")).map(_.toBoolean).getOrElse(true)

  val protocol = if (ssl) "https" else "http"
  val base = s"$protocol://$server:$port"
  val randomIndex = Iterator.continually(Map("index" -> (Math.random() * 2).toInt))

  private val httpProtocolBase = http
    .baseUrl(base)
    .warmUp(s"$base/")
    .doNotTrackHeader("1")
    .acceptHeader("application/json;q=0.9,*/*;q=0.8")
    .contentTypeHeader("application/json")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36")

  val httpProtocol = if (http2Enabled) {
    httpProtocolBase.enableHttp2.http2PriorKnowledge(Map(s"$server:$port" -> true)).copy(useOpenSsl = true)
  } else {
    httpProtocolBase
  }


  val feeder = csv("tasks.csv").random

  val search = exec(http("Search - Home").get("/"))
    .pause(1)
    .exec(http("Search - List")
      .get("/todo/search"))
    .pause(2)
    .exec(http("Search - Find")
      .get("/todo/1"))
    .pause(1)

  val add = exec(http("Add - Home").get("/"))
    .pause(1)
    .feed(feeder)
    .exec(http("Add Post")
      .post("/todo/").body(StringBody(
      """{
        |      "description": "${task}",
        |      "action_by": "user1",
        |      "created": "2020-03-20T13:00:00",
        |      "status": "NEW",
        |      "updated": "2020-03-20T13:00:00"
        |}""".stripMargin)).check(status.is(200)))
    .pause(2)
    .exec(http("Add - Select")
      .get("/todo/1").check(status.is(200)))
    .pause(1)

  val edit = feed(randomIndex).exec(http("Edit - Search")
    .get("/todo/search")
    .check(jsonPath("$.data[0].id").saveAs("taskId"))
  ).pause(1)
    .feed(feeder)
    .exec(http("Edit - Post")
      .post("/todo/${taskId}").body(StringBody(
      """{
        |      "id": "${taskId}",
        |      "description": "${task}",
        |      "action_by": "user1",
        |      "created": "2020-03-20T13:00:00",
        |      "status": "NEW",
        |      "updated": "2020-03-20T13:00:00"
        |}""".stripMargin)).check(status.is(200)))

    .pause(2)

}

class TodoSimulation extends Simulation {

  val searchScenario = scenario("Todo Search").during(2 minutes) {
    exec(Todo.search)
  }
  val addScenario = scenario("Todo Add").during(2 minutes) {
    exec(Todo.add)
  }

  val editScenario = scenario("Todo Edit").during(1 minutes) {
    exec(Todo.edit)
  }

  setUp(
    searchScenario.inject(rampUsers(10) during (10 seconds)),
    addScenario.inject(atOnceUsers(4)),
    editScenario.inject(nothingFor(10 seconds), rampUsers(2) during (10 seconds))
  ).protocols(Todo.httpProtocol).maxDuration(2 minutes)
}


class ListSimulation extends Simulation {

  val searchScenarioClosed: PopulationBuilder = scenario("Todo Search").during(5 minutes) {
    exec(Todo.search)
  }.inject(
    constantConcurrentUsers(5) during (30 seconds),
    rampConcurrentUsers(2) to (8) during (10 seconds)
  )

  val searchScenarioOpen: PopulationBuilder = scenario("Todo Search").during(5 minutes) {
    exec(Todo.search)
  }.inject(
    nothingFor(10 seconds),
    rampUsersPerSec(0) to 10 during (5 seconds),
    constantUsersPerSec(5) during (30 seconds)
  )



  setUp(searchScenarioOpen).protocols(Todo.httpProtocol).maxDuration(10 minutes)
}

