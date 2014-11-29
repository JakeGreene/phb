package models

import org.apache.pdfbox.pdmodel._
import org.apache.pdfbox.pdmodel.edit._

class HandbookBuilder(fonts: BookFont, columnWidth: Int, lineHeight: Int, bufferHeight: Int) {
  val document = new PDDocument()
  var page: Option[PDPage] = None
  var pageStream: Option[PDPageContentStream] = None
  
  val borderXSize = 30
  val borderYSize = 50
  val pageHeight = 820
  val rightColumnX = 330
  var x = 0
  var y = 0
  var onLeft = true
  
  freshPage()
  
  def stream = pageStream.get
  
  private def freshPage(): Unit = {
    for {
      p <- page
      s <- pageStream
    } {
      document.addPage(p)
      s.endText()
      s.close()
    }
    
    val p = new PDPage()
    page = Some(p)
    val s = new PDPageContentStream(document, p)
    x = 0
    y = 0
    onLeft = true
    s.beginText()
    pageStream = Some(s)
    pageTopLeft()
  }
  
  def addSpell(spell: Spell): Unit = {
    val headers = Seq("Casting Time" -> spell.time , "Range" -> spell.range , "Components" -> spell.comp , "Duration" -> spell.duration)
    headerSection(spell.name , spell.desc, headers)
    buffer()
    bodySection(spell.body)
    newline(2 * lineHeight)
  }
  
  private def headerSection(title: String, subTitle: String, headers: Seq[(String, String)]): Unit = {
    titleLine(title)
    buffer()
    subTitleLine(subTitle)
    buffer()
    headers.foreach { case (header, value) =>
      headerLine(header, value)  
    }
  }
  
  private def titleLine(name: String): Unit = {
    stream.setFont(fonts.title.style, fonts.title.size)
    stream.setNonStrokingColor(125, 0, 0)
    stream.drawString(name)
    stream.setNonStrokingColor(0, 0, 0)
  }
  
  private def subTitleLine(name: String): Unit = {
    stream.setFont(fonts.sub.style, fonts.sub.size)
    newline()
    stream.drawString(name)
  }
  
  private def headerLine(title: String, value: String): Unit = {
    stream.setFont(fonts.header.style, fonts.header.size)
    newline()
    stream.drawString(title + ": ")
    stream.setFont(fonts.body.style, fonts.body.size)
    stream.drawString(value)  
  }
  
  private def bodySection(text: String): Unit = {
    stream.setFont(fonts.body.style, fonts.body.size)
    val words = text.split(" ")
    val paragraph = words.foldLeft(Seq[String]()) { case (acc, word) =>
      if (acc.size > 0 && acc.head.size + word.size < columnWidth) (acc.head + " " + word) +: acc.tail
      else word +: acc
    }
    for (line <- paragraph.reverse) {
      newline()
      stream.drawString(line)     
    }
  }
  
  private def pageTopLeft(): Unit = {
    stream.moveTextPositionByAmount(borderXSize - x, pageHeight - borderYSize - y)
    x = borderXSize
    y = pageHeight - borderYSize
    onLeft = true
  }
  
  private def pageTopRight(): Unit = {
    stream.moveTextPositionByAmount(rightColumnX - x, pageHeight - borderYSize - y)
    x = rightColumnX
    y = pageHeight - borderYSize
    onLeft = false
  }
  
  private def newline(height: Int = lineHeight): Unit = {
    //stream.moveTextPositionByAmount(0, -height)
    moveDown(height)
  }
  
  private def buffer(height: Int = bufferHeight): Unit = {
    //stream.moveTextPositionByAmount(0, -height)
    moveDown(height)
  }  
  
  private def moveDown(deltaY: Int): Unit = {
    if (y - deltaY < borderYSize) {
      pageTopRight()
    } else {
      stream.moveTextPositionByAmount(0, -deltaY)
      y -= deltaY
    }
  }
  
  def toPDF(): PDDocument = {
    for {
      p <- page
      s <- pageStream
    } {
      document.addPage(p)
      s.endText()
      s.close()
    }
    document
  }
}