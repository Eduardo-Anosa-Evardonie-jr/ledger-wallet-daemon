package co.ledger.wallet.daemon.controllers.requests

import co.ledger.wallet.daemon.services.AuthenticationService.AuthentifiedUserContext._
import com.twitter.finagle.http.Request

class RichRequest(request: Request) {
  def user = request.user.get
}