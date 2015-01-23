package com.josephpconley.ncaa

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html._
import scala.collection.JavaConversions._
import com.josephpconley.io.CSV

/**
  * User: joe
  * Date: 2/19/14
 *
 * http://www.cbssports.com/collegebasketball/ncaa-tournament/history/yearbyyear/index
  */
object MarchMadnessScores {

  def parseRow(row: HtmlTableRow) = {
    val cells = row.getCells
    (cells.apply(1).getTextContent.toInt, cells.apply(2).getTextContent.toInt, cells.last.getTextContent.toInt)
  }

  def fetch = {
    val webClient = new WebClient()
    val opts = webClient.getOptions
    opts.setCssEnabled(false)
    opts.setJavaScriptEnabled(false)

    val years = 1950 to 2012

    years.map{ y =>
      val page: HtmlPage = webClient.getPage(s"http://www.sports-reference.com/cbb/postseason/$y-ncaa.html")
      val urls = page.getByXPath("//a[@class = 'bold_text']").toArray
                 .map(_.asInstanceOf[HtmlAnchor].getHrefAttribute).toSet.toSeq
                 .filter(_.startsWith("/cbb/boxscores"))

      println(page.getUrl.toString)

      val games = urls.map(url => webClient.getPage(s"http://www.sports-reference.com$url").asInstanceOf[HtmlPage])

      for{
        game <- games if(!game.getUrl.toString.contains("error"))
      } yield {
        val boxScore: HtmlTable = game.getByXPath("//table[@class = 'nav_table stats_table']").head.asInstanceOf[HtmlTable]
        val topRow = parseRow(boxScore.getRow(2))
        val bottomRow = parseRow(boxScore.getRow(3))

        val (w, l) = if(topRow._3 > bottomRow._3) topRow -> bottomRow else bottomRow -> topRow
        (y, w._1 + w._2, l._1 + l._2, w._3 + l._3)
      }
    }.flatten
  }
}

object MarchMadnessApp extends App{
  val scores = MarchMadnessScores.fetch

  val columns = Seq("year", "winner", "loser", "total")
  val rows = scores.map(score => Seq(score._1.toString, score._2.toString, score._3.toString, score._4.toString))

  CSV.write("scores.csv", columns, rows)
}