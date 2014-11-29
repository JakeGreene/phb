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
  
  val fonts = BookFont(Font(PDType1Font.TIMES_BOLD, 14), Font(PDType1Font.TIMES_ITALIC, 12), Font(PDType1Font.TIMES_BOLD, 12), Font(PDType1Font.TIMES_ROMAN, 12))
  val builder = new HandbookBuilder(fonts, 55, 13, 2)
  val acid = Spell("Acid Splash", "Conjuration cantrip", "1 action", "60 feet", "V, S", "Instantaneous", """You hurl a bubble of acid. Choose one creature within range, or choose two creatures within range that are within 5 feet of each other. A target must succeed on a Dexterity saving throw or take 1d6 acid damage. This spell's damage increases by 1d6 when you reach 5th level (2d6), 11th level (3d6), and 17th level (4d6).""")
  builder.start()
  for (i <- (1 to 30)) {
    builder.addSpell(acid)  
  }
  val document = builder.toPDF()
  
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