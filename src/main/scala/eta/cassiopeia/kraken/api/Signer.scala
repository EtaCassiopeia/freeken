package eta.cassiopeia.kraken.api

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

import org.apache.commons.codec.binary.Base64

object Signer {

  def getSignature(path: String,
                   nonce: Long,
                   postData: String,
                   apiSecret: String) = {
    // Message signature using HMAC-SHA512 of (URI path + SHA256(nonce + POST data)) and base64 decoded secret API key
    val md = MessageDigest.getInstance("SHA-256")
    md.update((nonce + postData).getBytes)
    val mac = Mac.getInstance("HmacSHA512")
    mac.init(new SecretKeySpec(Base64.decodeBase64(apiSecret), "HmacSHA512"))
    mac.update(path.getBytes)
    new String(Base64.encodeBase64(mac.doFinal(md.digest())))
  }

}
