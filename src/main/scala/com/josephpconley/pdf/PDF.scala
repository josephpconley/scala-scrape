package com.josephpconley.pdf

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.{SimpleTextExtractionStrategy, PdfReaderContentParser}

/**
 * User: joe
 * Date: 10/21/13
 */
object PDF extends App{
  val reader = new PdfReader("simpsons_schedule.pdf")
  val parser = new PdfReaderContentParser(reader)


  parser.processContent(1, new SimpleTextExtractionStrategy()).getResultantText.split("\n").foreach{ line =>
    println(line)
  }
}