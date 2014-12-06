package models

import org.apache.pdfbox.pdmodel._
import org.apache.pdfbox.pdmodel.edit._

class HandbookBuilder(fonts: BookFont, columnWidth: Int, lineHeight: Int, bufferHeight: Int) {
  val document = new PDDocument()
  var page: Option[PDPage] = None
  var pageStream: Option[PDPageContentStream] = None
  var currentFont: Option[Font] = None
  
  val borderXSize = 30
  val borderYSize = 50
  val pageHeight = 820
  val rightColumnX = 330
  var x = 0
  var y = 0
  var onLeft = true
  
  def stream = pageStream.get
  def font = currentFont.get
  
  /**
   * The builder has to be started before anything can be added
   */
  def start(): Unit = {
    freshPage()
  }
  
  private def freshPage(): Unit = {
    closePage()
    val p = new PDPage()
    page = Some(p)
    val s = new PDPageContentStream(document, p)
    s.beginText()
    pageStream = Some(s)
    currentFont.foreach(setFont)
    x = 0
    y = 0
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
    setFont(fonts.title)
    stream.setNonStrokingColor(125, 0, 0)
    stream.drawString(name)
    stream.setNonStrokingColor(0, 0, 0)
  }
  
  private def subTitleLine(name: String): Unit = {
    setFont(fonts.sub)
    newline()
    stream.drawString(name)
  }
  
  private def headerLine(title: String, value: String): Unit = {
    setFont(fonts.header)
    newline()
    val titleBlock = title + ": "
    stream.drawString(titleBlock)
    setFont(fonts.body)
    drawString(value, titleBlock.size)  
  }
  
  private def bodySection(text: String): Unit = {
    setFont(fonts.body)
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
    moveDown(height)
  }
  
  private def buffer(height: Int = bufferHeight): Unit = {
    moveDown(height)
  }  
  
  private def moveDown(deltaY: Int): Unit = {
    if (y - deltaY < borderYSize) {
      if (onLeft) pageTopRight()
      else freshPage()
    } else {
      stream.moveTextPositionByAmount(0, -deltaY)
      y -= deltaY
    }
  }
  
  private def setFont(font: Font): Unit = {
    stream.setFont(font.style, font.size)
    currentFont = Some(font)
  }
  
  private def drawString(s: String, initialOffset: Int = 0): Unit = {
    val words = s.split(" ")
    /*
     * This first line needs to account for the initialOffset
     */
    val paragraph = words.foldLeft(Seq[String]()) { case (acc, word) =>
      if (acc.size == 1 && (acc.head.size + word.size < columnWidth - initialOffset)) (acc.head + " " + word) +: acc.tail
      else if (acc.size > 1 && acc.head.size + word.size < columnWidth) (acc.head + " " + word) +: acc.tail
      else word +: acc
    }
    val lines = paragraph.reverse
    lines.headOption.foreach(stream.drawString(_))
    for (line <- lines.tail) {
      newline()
      stream.drawString(line)
    }
  }
  
  private def closePage(): Unit = {
    for {
      p <- page
      s <- pageStream
    } {
      document.addPage(p)
      s.endText()
      s.close()
    }
  }
  
  def toPDF(): PDDocument = {
    closePage()
    document
  }
}