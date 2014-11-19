package controllers

import play.api.mvc._

object HandBook extends Controller {
  def index() = Action(Ok(views.html.Handbook.index()))
}