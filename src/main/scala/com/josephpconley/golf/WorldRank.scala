package com.josephpconley.golf

import com.itextpdf.text.pdf.parser.{SimpleTextExtractionStrategy, PdfReaderContentParser}
import com.itextpdf.text.pdf.PdfReader

import java.io.{File, PrintWriter, FileOutputStream}
import java.net.URL
import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.io.Source
import scala.util.Try
import scalax.io.Resource

/**
 * User: joe
 * Date: 10/21/13
 */
object WorldRank extends App{
  val years = 2014 to 2014
  val weeks = 1 to 52

  def getUrl(week: Int, year: Int) = {
    val weekToStr = if(week < 10) "0" + week else week.toString
    s"http://dps.endavadigital.net/owgr/doc/content/archive/$year/owgr${weekToStr}f$year.pdf"
  }

  val columns = Seq("year", "week", "rank", "name_country", "avg", "tot", "events_played", "points_lost", "points_gained")

  new File("owgr.csv").delete()
  val output = Resource.fromFile("owgr.csv")
  output.write(columns.mkString(",") + "\n")

  for{
    y <- years
    w <- weeks
  } yield {
    Try(new URL(getUrl(w, y))).map{ url =>
      val reader = new PdfReader(url)
      val parser = new PdfReaderContentParser(reader)

      println(y + " Week " + w)
      for(i <- 1 to reader.getNumberOfPages){
        val strategy = parser.processContent(i, new SimpleTextExtractionStrategy())
        strategy.getResultantText().split("\n").foreach{ line =>
          val fields = line.split(" ")
          Try(fields(0).toInt).map{ rank =>
            val (nameCountry, avg, tot, events, pointsLost, pointsGained) =
              if( (y + w/100) <= 2011.15){
                val n = fields.size
                val len = n - 5
                (fields.slice(3, len).mkString(" "), fields(n - 5),fields(n - 4),fields(n - 3),fields(n - 2),fields(n - 1))
              }else{
                val n = fields.size
                val len = n - 6
                (fields.slice(3, len).mkString(" "), fields(n - 6),fields(n - 5),fields(n - 1),fields(n - 3),fields(n - 2))
              }
            val row:Seq[String] = Seq(y.toString, w.toString, fields(0), nameCountry, avg, tot, events, pointsLost, pointsGained)
            output.write(row.mkString(",") + "\n")
          }
        }
      }
    }.getOrElse{
      println("No rankings for " + y + " week " + w)
    }
  }
}