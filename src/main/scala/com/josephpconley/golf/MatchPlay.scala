package com.josephpconley.golf

import scalax.io.Resource
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.{HtmlDivision, HtmlTable, HtmlPage}
import scala.collection.JavaConversions._
import java.io.File

/**
  * User: joe
  * Date: 2/19/14
  */
object MatchPlay extends App{

//  val columns = Seq("year", "index", "tournament", "name", "odds", "won", "payoff")
//
//  new File("odds.csv").delete()
//  val output = Resource.fromFile("odds.csv")
//  output.write(columns.mkString(",") + "\n")

  val webClient = new WebClient()
  val opts = webClient.getOptions
  opts.setCssEnabled(false)
  opts.setJavaScriptEnabled(false)

  var tourneyIndex = 0
  val years = Seq(2005, 2007, 2009, 2010, 2011, 2012)
//    val years = 2010 to 2010
  val rounds = 1 to 5

  for{
    y <- years
    r <- rounds
  } yield{
    val page: HtmlPage = webClient.getPage(s"http://www.golfchannel.com/tours/pga-tour/$y/wgc-accenture-match-play-championship/#?round=$r")
    val table: HtmlTable = page.getByXPath("//table[@class='gc_leaderboard_matchplay']").toArray.head.asInstanceOf[HtmlTable]
    val holesPlayed = table.getByXPath("//div[@class='score']").toList.asInstanceOf[List[HtmlDivision]].map{ scoreText =>
      scoreText.getTextContent.trim match {
        case h: String if(h.contains("holes")) => h.split(" ")(0).toInt
        case h: String if(h.contains("up")) => 18
        case h: String if(h.contains("and")) => 18 - h.split(" ")(2).toInt
        case _ => -1
      }
    }
    holesPlayed.filter(_ > 0).foreach(println)
  }
}