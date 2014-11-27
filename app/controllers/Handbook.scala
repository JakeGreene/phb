package controllers

import play.api.mvc._
import org.apache.pdfbox.pdmodel._
import org.apache.pdfbox.pdmodel.edit._
import org.apache.pdfbox.pdmodel.font._
import models._
import play.api.libs.iteratee.Enumerator
import play.api.libs.concurrent.Execution.Implicits._

object HandBook extends Controller {
  
  val races = Seq("Tiefling", "Dwarf").map(Race)
  val classes = Seq("Warlock", "Barbarian").map(DnDClass)
  
  val document = new PDDocument()
  val page = new PDPage()
  val contentStream = new PDPageContentStream(document, page)
  contentStream.beginText()
  contentStream.setFont(PDType1Font.TIMES_ROMAN, 12)
  contentStream.moveTextPositionByAmount( 100, 700 )
  contentStream.drawString( "Hello World" )
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
  
}