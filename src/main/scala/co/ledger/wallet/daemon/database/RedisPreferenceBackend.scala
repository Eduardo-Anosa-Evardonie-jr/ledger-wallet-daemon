package co.ledger.wallet.daemon.database

import java.nio.ByteBuffer
import java.util

import akka.actor.ActorSystem
import akka.util.ByteString
import co.ledger.core.{PreferencesBackend, PreferencesChange, PreferencesChangeType, RandomNumberGenerator}
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import com.twitter.inject.Logging
import org.apache.commons.codec.binary.Hex
import redis.{ByteStringFormatter, RedisClient}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.jdk.CollectionConverters.collectionAsScalaIterableConverter
import scala.util.Success

case class RedisClientConfiguration(
                                     poolName: String,
                                     host: String = "localhost",
                                     port: Int = 6379,
                                     password: Option[String] = None,
                                     db: Option[Int] = None,
                                     name: String = "RedisClient",
                                     connectionTimeout: Option[FiniteDuration] = None
                                   ) {
  val prefix: String = "core|preferences|" ++ poolName ++ "|"
}

class RedisPreferenceBackend(conf: RedisClientConfiguration) extends PreferencesBackend with Logging {
  type KeyPref = ByteBuffer
  type ValuePref = ByteBuffer
  val prefCache: LoadingCache[KeyPref, Option[ValuePref]] = CacheBuilder.newBuilder()
    .maximumSize(2000)
    .build[KeyPref, Option[ValuePref]](new CacheLoader[KeyPref, Option[ValuePref]] {
      override def load(key: KeyPref): Option[ValuePref] = {
        loadPrefEntry(key.array()).map[ValuePref](ByteBuffer.wrap)
      }
    })
  private implicit val akkaSystem: ActorSystem = akka.actor.ActorSystem()

  private implicit val byteArrayToByteString: ByteStringFormatter[Array[Byte]] =
    new ByteStringFormatter[Array[Byte]] {
      override def serialize(data: Array[Byte]): ByteString = {
        ByteString(data)
      }

      override def deserialize(bs: ByteString): Array[Byte] = {
        bs.toArray
      }
    }

  private val redis: RedisClient = RedisClient(
    conf.host, conf.port, conf.password, conf.db, conf.name, conf.connectionTimeout
  )

  def init(): Unit = {
  }

  override def get(bytes: Array[Byte]): Array[Byte] = {
    prefCache.get(ByteBuffer.wrap(bytes)).map(_.array).orNull
  }

  /**
    * Encodes an array of bytes into a hex string prefixed with self.prefix
    *
    * Note: Since Redis is a full text protocol, hex strings is used to make sure no
    * weird characters text gets sent.
    */
  private def prepareKey(a: Array[Byte]): String = {
    conf.prefix ++ Hex.encodeHexString(a)
  }

  private def loadPrefEntry(key: Array[Byte]): Option[Array[Byte]] = {
    val f = redis.get(prepareKey(key))
    Await.result(f, 10.seconds)
  }

  override def commit(arrayList: util.ArrayList[PreferencesChange]): Boolean = {
    if (!arrayList.isEmpty) {
      val redisTransaction = redis.multi()

      arrayList.asScala
        .filter(_.getType == PreferencesChangeType.PUT_TYPE)
        .foreach(preference => {
          val key = prepareKey(preference.getKey)

          redisTransaction.watch(key)
          redisTransaction.set(key, preference.getValue)
        })
      arrayList.asScala
        .filter(_.getType == PreferencesChangeType.DELETE_TYPE)
        .foreach(preference => {
          val key = prepareKey(preference.getKey)

          redisTransaction.watch(key)
          redisTransaction.del(key)
        })

      val invalidation = redisTransaction.exec().andThen {
        case Success(_) => arrayList.forEach(p => prefCache.invalidate(ByteBuffer.wrap(p.getKey)))
      }.map(_ => true)
        .recover {
          case e => logger.error("failed to commit preference changes", e)
            false
        }
      Await.result(invalidation, 10.seconds)
    } else true
  }

  override def setEncryption(randomNumberGenerator: RandomNumberGenerator, s: String): Unit = Unit

  override def unsetEncryption(): Unit = Unit

  override def resetEncryption(randomNumberGenerator: RandomNumberGenerator, s: String, s1: String): Boolean = true

  override def getEncryptionSalt: String = ""

  override def clear(): Unit = {
    val batchSize = 1000
    var cleared = false
    do {
      val f = redis.scan(matchGlob = Some(conf.prefix ++ "*"), count = Some(batchSize))
      val cursor = Await.result(f, 10.seconds)
      val redisTransaction = redis.multi()
      cursor.data.foreach(redisTransaction.del(_))
      Await.result(redisTransaction.exec(), 10.seconds)
      cleared = cursor.data.length < batchSize
    } while (!cleared)
  }
}
