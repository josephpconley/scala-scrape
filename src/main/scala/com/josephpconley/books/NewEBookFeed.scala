package com.josephpconley.books

import com.gargoylesoftware.htmlunit.html.{HtmlAnchor, HtmlImage, HtmlDivision, HtmlPage}
import com.gargoylesoftware.htmlunit.WebClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.HttpStatus
import org.apache.http.impl.client.HttpClients
import org.apache.http.protocol.BasicHttpContext
import org.apache.http.util.EntityUtils
import com.josephpconley.rss.{Feed, Item}
import scala.xml.Elem
import scalax.io.Resource
import java.io.{File, PrintWriter}

/**
 * User: joe
 * Date: 10/21/13
 */

class NewEBookFeed(val name: String, val title: String, val description: String, val link: String, baseUrl: String) extends Feed {
  val atomLink: String = link
  lazy val webClient = new WebClient()
  lazy val opts = webClient.getOptions
  opts.setCssEnabled(false)
  opts.setJavaScriptEnabled(false)

  def htmlDescription(title: String, author: String, url: String, imgSrc: String) =
    <div>
      <a href={url}>
        <h3>{title}</h3>
        <h4>by {author}</h4>
      </a>
      <img src={imgSrc}/>
    </div>

  //returns the latest 20 ebooks
  //passing a title will return all ebooks newer than the given title
  def items(lastItemTitle: Option[String] = None): Seq[Item] = {
    //go to homepage
    val page: HtmlPage = webClient.getPage(baseUrl)

    //grab the new ebooks page
    val newBook: HtmlPage = page.getByXPath("//a[starts-with(text(),'New eBooks')]").toArray().apply(1).asInstanceOf[HtmlAnchor].click()
    val newBookBaseUrl = newBook.getPage.getUrl.toString
    println(newBookBaseUrl)

    var stop = false
    var pageNum = 1
    var bookItems = collection.mutable.ListBuffer.empty[Item]

    while(!stop){
      val newBookPage:HtmlPage = webClient.getPage(newBookBaseUrl + "&Page=" + pageNum)
      pageNum += 1
      
      val titles = newBookPage.getByXPath("//div[@class='title-holder']").toArray
      val authors = newBookPage.getByXPath("//a[@class='tc-author']").toArray
      val images = newBookPage.getByXPath("//img[@class='wtil-cover lzld']").toArray

      val items = titles.indices.map{ i =>
        val titleAnchor = titles(i).asInstanceOf[HtmlDivision].getFirstChild
        val author = authors(i).asInstanceOf[HtmlAnchor]

        val title = titleAnchor.getTextContent
        val authorName = author.getTextContent
        val imgSrc = images(i).asInstanceOf[HtmlImage].getAttribute("data-original")
        val bookUrl = baseUrl + "/" + titleAnchor.getAttributes.getNamedItem("href").getNodeValue

        Item(title + " by " + authorName, htmlDescription(title, authorName, bookUrl, imgSrc).toString, bookUrl, bookUrl)
      }

      if(lastItemTitle.isEmpty || items.exists(_.title == lastItemTitle.get) || bookItems.size > 1000){
        stop = true
      }
      
      bookItems.append(items:_*)
    }

    bookItems
  }
}

object NewEBookFeed {

  lazy val delco = new NewEBookFeed("delco", "Delco New ebooks", "Notification of new ebooks for Delaware County Library members", "http://app.josephpconley.com/rss/delco.xml",
                                "http://delawareco.libraryreserve.com/")

  lazy val philly = new NewEBookFeed("philly", "Philly New ebooks", "Notification of new ebooks for Free Library of Philadelphia members", "http://app.josephpconley.com/rss/philly.xml",
                                "http://philadelphia.libraryreserve.com")
}

object NewEBooksApp extends App{
//  val items = NewEBookFeed.philly.items(Some("The Body Book by Cameron Diaz"))
//  println(items.size)

  NewEBookFeed.delco.items().foreach(println)
//  NewEBookFeed.philly.items().foreach(println)
}