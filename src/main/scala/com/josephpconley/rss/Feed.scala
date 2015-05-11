package com.josephpconley.rss

import scala.xml.Elem

case class Item(title: String, description: String, link: String, guid: String){
  def xml = {
    <item>
      <title>{title}</title>
      <description>{
        scala.xml.PCData(description)
      }
      </description>
      <link>{link}</link>
      <guid>{guid}</guid>
    </item>
  }
}

abstract trait Feed{

  val name: String
  val title: String
  val description: String
  val link: String
  val atomLink: String

  def items(lastBook: Option[String] = None): Seq[Item]

  def xml(items: Seq[Item]): Elem = {
    <rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
      <channel>
        <title>{title}</title>
        <description>{description}</description>
        <link>{link}</link>
        <atom:link href={atomLink} rel="self" type="application/rss+xml" />{
          items.map(_.xml)
        }
      </channel>
    </rss>
  }
}