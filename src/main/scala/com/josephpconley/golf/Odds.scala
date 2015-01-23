package com.josephpconley.golf

import scalax.io.Resource
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.{HtmlTable, HtmlPage}
import scala.collection.JavaConversions._
import java.io.File

/**
  * User: joe
  * Date: 10/21/13
  */
object Odds extends App{

  val columns = Seq("year", "index", "tournament", "name", "odds", "won", "payoff")

  new File("odds.csv").delete()
  val output = Resource.fromFile("odds.csv")
  output.write(columns.mkString(",") + "\n")

  val webClient = new WebClient()
  val opts = webClient.getOptions
  opts.setCssEnabled(false)
  opts.setJavaScriptEnabled(false)

  var tourneyIndex = 0
  val years = 2008 to 2013
//    val years = 2010 to 2010
  years.foreach{ y =>
    val page: HtmlPage = webClient.getPage(s"http://www.golfodds.com/archive${y}.html")
    val table: HtmlTable = page.getByXPath("//table[@width='261']").toArray.head.asInstanceOf[HtmlTable]
    val rows = table.getRows.toList
    var index = 0
    while(index < rows.size){
      val tourneyName = rows(index).getCell(0).getFirstChild.getFirstChild.asText()
      tourneyIndex += 1

      //skip non-standard events
      if(y == 2012 && tourneyName.contains("Ryder")){
        index += 21
      }else{
        while(!rows(index).getTextContent.contains("Odds")){
          index += 1
        }
        index += 1
        while(rows(index).getCell(0).getTextContent.trim.length > 2){
          val (player, odds) = rows(index).getCell(0).getTextContent.replace("\u00A0", "").replace("*Winner*", "").replace("*Winners*", "").replace(",", "").trim -> rows(index).getCell(1).getTextContent

          index += 1

          val payoff = if(odds.contains("/")) odds.split("/")(0).toDouble / odds.split("/")(1).toDouble else if(odds == "EVEN") 1 else -1

          val row: Seq[String] = Seq(y.toString, tourneyIndex.toString, tourneyName, player, odds, rows(index-1).getCell(0).getTextContent.contains("*Winner").toString, payoff.toString)

          val badNames = Seq("tournament reduced", "54 holes", "inclement weather")
          val badTourneys = Seq("Ryder", "Royal Trophy")

          if(!badNames.exists(player.contains(_)) && !badTourneys.exists(tourneyName.contains(_))){
            output.write(row.mkString(",") + "\n")
          }
        }

        //skip empty rows and move to next tournament
        while(index < rows.size && rows(index).getCell(0).getTextContent.trim.length < 2){
          index += 1
        }
      }
    }
  }
}