package controllers

import play.api.mvc._
import org.apache.pdfbox.pdmodel._
import org.apache.pdfbox.pdmodel.edit._
import org.apache.pdfbox.pdmodel.font._
import models._
import play.api.libs.iteratee.Enumerator
import play.api.libs.concurrent.Execution.Implicits._

object HandBook extends Controller {
  
  val races = Seq("Tiefling", "Dwarf", "Human", "Halfling", "Half-Elf", "Half-Orc", "Elf", "Dragonborn").sorted.map(Race)
  val classes = Seq("Warlock", "Barbarian", "Fighter", "Wizard", "Sorcerer", "Cleric").sorted.map(DnDClass)
  
  val document = new PDDocument()
  val page = new PDPage()
  val contentStream = new PDPageContentStream(document, page)
  contentStream.beginText()
  contentStream.moveTextPositionByAmount(50, 750)
  val size = 12
  val newLine = -15
  val maxLineLength = 55
  
  val body = """You hurl a bubble of acid. Choose one creature within range, or choose two creatures within range that are within 5 feet of each other. A target must succeed on a Dexterity saving throw or take 1d6 acid damage. This spell's damage increases by 1d6 when you reach 5th level (2d6), 11th level (3d6), and 17th level (4d6)."""
  val headers = Seq("Casting Time" -> "1 action", "Range" -> "60 feet", "Components" -> "V, S", "Duration" -> "Instantaneous")
  
  headerSection("Acid Splash", "Conjuration cantrip", headers, contentStream)  
  bodySection(body, contentStream)
  contentStream.endText()
  contentStream.close()
  document.addPage(page)
  
  def index() = Action(Ok(views.html.Handbook.index(races, classes)))
  
  def phb() = Action {
    Ok.chunked(Enumerator.outputStream { os =>
      document.save(os)
      os.close()
    }).withHeaders(
      CONTENT_TYPE -> "application/pdf",
      CONTENT_DISPOSITION -> "filename=player-handbook.pdf"
    )
  }
  
  private def headerSection(title: String, subTitle: String, headers: Seq[(String, String)], stream: PDPageContentStream): Unit = {
    titleLine(title, stream)
    subTitleLine(subTitle, stream)
    headers.foreach { case (header, value) =>
      headerLine(header, value, stream)  
    }
  }
  
  private def titleLine(name: String, stream: PDPageContentStream): Unit = {
    stream.setFont(PDType1Font.TIMES_BOLD, 14)
    stream.setNonStrokingColor(125, 0, 0)
    stream.drawString(name)
    stream.setNonStrokingColor(0, 0, 0)
  }
  
  private def subTitleLine(name: String, stream: PDPageContentStream): Unit = {
    stream.setFont(PDType1Font.TIMES_ITALIC, size)
    stream.moveTextPositionByAmount(0, newLine)
    stream.drawString(name)
  }
  
  private def headerLine(title: String, value: String, stream: PDPageContentStream): Unit = {
    stream.setFont(PDType1Font.TIMES_BOLD, size)
    stream.moveTextPositionByAmount(0, newLine)
    stream.drawString(title + ": ")
    stream.setFont(PDType1Font.TIMES_ROMAN, size)
    stream.drawString(value)  
  }
  
  private def bodySection(text: String, stream: PDPageContentStream): Unit = {
    stream.setFont(PDType1Font.TIMES_ROMAN, size)
    val words = text.split(" ")
    val paragraph = words.foldLeft(Seq[String]()) { case (acc, word) =>
      if (acc.size > 0 && acc.head.size + word.size < maxLineLength) (acc.head + " " + word) +: acc.tail
      else word +: acc
    }
    for (line <- paragraph.reverse) {
      stream.moveTextPositionByAmount(0, newLine)
      stream.drawString(line)     
    }
 
  }
  
}