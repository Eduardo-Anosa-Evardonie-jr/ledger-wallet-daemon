package co.ledger.wallet.daemon.database

import java.nio.ByteBuffer
import java.util

import co.ledger.core.{PreferencesBackend, PreferencesChange, PreferencesChangeType, RandomNumberGenerator}
import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import com.redis._
import com.redis.serialization.Parse.Implicits.parseByteArray
import com.twitter.inject.Logging
import org.apache.commons.codec.binary.Hex

import scala.jdk.CollectionConverters.collectionAsScalaIterableConverter

case class RedisClientConfiguration(
                                     poolName: String,
                                     host: String = "localhost",
                                     port: Int = 6379,
                                     password: Option[String] = None,
                                     db: Option[Int] = None,
                                     connectionTimeout: Option[Int] = Some(10)
                                   ) {
  val prefix: String = "core:preferences:" ++ poolName ++ ":"
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

  private val redis: RedisClient = new RedisClient(
    host = conf.host,
    port = conf.port,
    secret = conf.password,
    database = conf.db.getOrElse(0),
    timeout = conf.connectionTimeout.getOrElse(0),
    sslContext = None
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
    redis.get(prepareKey(key))
  }

  override def commit(arrayList: util.ArrayList[PreferencesChange]): Boolean = {
    if (!arrayList.isEmpty) {
      arrayList.asScala.map(pref => prepareKey(pref.getKey))
        .foreach(redis.watch(_))
      redis.pipeline { p =>
        arrayList.asScala
          .filter(_.getType == PreferencesChangeType.PUT_TYPE)
          .foreach(preference => {
            val key = prepareKey(preference.getKey)

            p.set(key, preference.getValue)
          })
        arrayList.asScala
          .filter(_.getType == PreferencesChangeType.DELETE_TYPE)
          .foreach(preference => {
            val key = prepareKey(preference.getKey)

            p.del(key)
          })
      }

      arrayList.forEach(p => prefCache.invalidate(ByteBuffer.wrap(p.getKey)))
      true
    } else true
  }

  override def setEncryption(randomNumberGenerator: RandomNumberGenerator, s: String): Unit = Unit

  override def unsetEncryption(): Unit = Unit

  override def resetEncryption(randomNumberGenerator: RandomNumberGenerator, s: String, s1: String): Boolean = true

  override def getEncryptionSalt: String = ""

  override def clear(): Unit = {
    val batchSize = 1000
    var cursor: Option[Int] = None
    do {
      val Some((maybeCursor, maybeData)) = redis.scan(cursor.getOrElse(0), conf.prefix ++ "*", count = batchSize)
      maybeData.foreach(maybeKeys => {
        redis.del(maybeKeys.flatten)
      })
      cursor = maybeCursor
    } while (cursor.isDefined)
  }
}
