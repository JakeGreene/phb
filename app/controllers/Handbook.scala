package controllers

import models._
import org.apache.pdfbox.pdmodel._
import org.apache.pdfbox.pdmodel.edit._
import org.apache.pdfbox.pdmodel.font._
import play.api.libs.iteratee.Enumerator
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.db.slick.DB
import play.api._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current

object HandBook extends Controller {
  
  implicit val spellReads = Json.reads[Spell]
  val Spells = TableQuery[SpellsTable]
  implicit val classReads = Json.reads[DnDClass]
  val DnDClasses = TableQuery[ClassTable]
  implicit val raceReads = Json.reads[Race]
  val Races = TableQuery[RacesTable]
  
  val fonts = BookFont(Font(PDType1Font.TIMES_BOLD, 14),
                       Font(PDType1Font.TIMES_ITALIC, 12),
                       Font(PDType1Font.TIMES_BOLD, 12),
                       Font(PDType1Font.TIMES_ROMAN, 12))
  
  def index() = Action {
    DB.withSession { implicit session => 
      val classes = DnDClasses.run
      val races = Races.run
      Ok(views.html.Handbook.index(races, classes))
    }
  }
  
  def phb() = DBAction { implicit rs =>
    val builder = new HandbookBuilder(fonts, 55, 13, 2)
    builder.start()
    Spells.foreach { spell =>
      builder.addSpell(spell)
    }  
    val document = builder.toPDF
    Ok.chunked(Enumerator.outputStream { os =>
      document.save(os)
      os.close()
    }).withHeaders(
      CONTENT_TYPE -> "application/pdf",
      CONTENT_DISPOSITION -> "filename=player-handbook.pdf"
    )
  }
  
  def getSpells() = DBAction { implicit rs =>
    val spells = Spells.list
    Ok(spells.mkString("\n"))
  }
  
  def addSpell() = Action(parse.json) { request =>
    val spell = request.body.asOpt[Spell]
    spell.fold(BadRequest("Not a valid spell")) { s =>
      DB.withSession { implicit session =>
        Spells.insert(s)
      }
      Ok(s"Stored Spell $s")      
    }
  }
  
  def getClasses() = DBAction { implicit rs =>
    val classes = DnDClasses.list
    Ok(classes.mkString("\n"))
  }
  
  def addClass() = Action(parse.json) { request =>
    val dndClass = request.body.asOpt[DnDClass]
    dndClass.fold(BadRequest("Not a valid class")) { c =>
      DB.withSession { implicit session =>
        DnDClasses.insert(c)  
      } 
      Ok(s"Stored Class $c")
    }
  }
  
  def getRaces() = DBAction { implicit rs =>
    val races = Races.list
    Ok(races.mkString("\n"))
  }
  
  def addRace() = Action(parse.json) { request =>
    val race = request.body.asOpt[Race]
    race.fold(BadRequest("Not a valid Race")) { r =>
      DB.withSession { implicit session =>
        Races.insert(r)  
      }
      Ok(s"Stored Race $r")
    }
  }
}