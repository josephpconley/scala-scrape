package com.josephpconley.io

import java.io.File
import scalax.io.Resource

/**
 * User: jconley
 * Date: 3/10/14
 */
object CSV {
  def write(fileName: String, columns: Seq[String], rows: Seq[Seq[String]]) = {
    new File(fileName).delete()
    val output = Resource.fromFile(fileName)
    output.write(columns.mkString(",") + "\n")

    rows.foreach{ row =>
      output.write(row.mkString(",") + "\n")
    }
  }
}
