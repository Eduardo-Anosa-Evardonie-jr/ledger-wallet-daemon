package co.ledger.wallet.daemon.api

import co.ledger.wallet.daemon.{ErrorCode, ErrorResponseBody, models}
import co.ledger.wallet.daemon.models.Pool
import co.ledger.wallet.daemon.utils.APIFeatureTest
import com.twitter.finagle.http.Status

class WalletPoolsApiTest extends APIFeatureTest {

  test("WalletPoolsApi#Create and list single pool") {
    createPool("my_pool")
    val pools = parse[List[models.Pool]](getPools())
    assert(pools == List(Pool("my_pool", 0)))
    deletePool("my_pool")
  }

  test("WalletPoolsApi#Create and list multiple pool") {
    createPool("your_pool")
    createPool("this_pool")
    val pools = parse[List[models.Pool]](getPools())
    assert(pools == List(Pool("your_pool", 0), Pool("this_pool", 0)))
    deletePool("your_pool")
    deletePool("this_pool")
    val pools2 = parse[List[models.Pool]](getPools())
    assert(pools2.size == 0)
  }

  test("WalletPoolsApi#Get single pool") {
    val response = createPool("anotha_pool")
    assert(server.mapper.objectMapper.readValue[models.Pool](response.contentString) == Pool("anotha_pool", 0))
    val pool = parse[models.Pool](getPool("anotha_pool"))
    assert(pool == Pool("anotha_pool", 0))
    deletePool("anotha_pool")
  }

  test("WalletPoolsApi#Get and delete non-exist pool return not found") {
    assert(
      server.mapper.objectMapper.readValue[ErrorResponseBody](getPool("not_exist_pool", Status.NotFound).contentString)
        == ErrorResponseBody(ErrorCode.Not_Found, "not_exist_pool is not a pool"))
    assert(
      server.mapper.objectMapper.readValue[ErrorResponseBody](deletePool("another_not_exist_pool").contentString)
        == ErrorResponseBody(ErrorCode.Invalid_Request, "Attempt deleting another_not_exist_pool request is ignored"))
  }

  test("WalletPoolsApi#Create same pool twice return ok") {
    assert(
      server.mapper.objectMapper.readValue[models.Pool](createPool("same_pool").contentString)
        == Pool("same_pool", 0))
    assert(
      server.mapper.objectMapper.readValue[ErrorResponseBody](createPool("same_pool").contentString)
        == ErrorResponseBody(ErrorCode.Duplicate_Request, "Attempt creating same_pool request is ignored"))
    deletePool("same_pool")
  }

}
