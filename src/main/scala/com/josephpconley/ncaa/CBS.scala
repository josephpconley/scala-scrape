package com.josephpconley.ncaa

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.{HtmlTable, HtmlAnchor, HtmlPage}

/**
 * User: jconley
 * Date: 3/19/2014
 */
object CBS extends App{
  val webClient = new WebClient()
  val opts = webClient.getOptions
  opts.setCssEnabled(false)
  opts.setJavaScriptEnabled(false)

  val page: HtmlPage = webClient.getPage("http://www.cbssports.com/collegebasketball/gametracker/live/NCAAB_20140318_MOUNT@ALBANY")
  val score = page.getFirstByXPath("//table[@id = 'sbLinescore']").asInstanceOf[HtmlTable]
  println(score)
}
