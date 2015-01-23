package com.josephpconley.nfl

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.xml.XmlPage
import scala.io.Source
import scala.xml.XML

/**
 * User: jconley
 * Date: 2/10/14
 */
object NFL extends App {
  val xml = XML.loadString(Source.fromURL("http://www.nfl.com//liveupdate/scorestrip/ss.xml").mkString)
  (xml \ "gms" \ "g").foreach{ game =>
    println(game.attribute("vnn").get + " " + game.attribute("vs").get)
    println(game.attribute("hnn").get + " " + game.attribute("hs").get)
    println()
  }
}
