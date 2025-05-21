package network.payload;

import md2.MD2;
import rsa.RSA;
import rsa.RsaPrivateKey;
import rsa.RsaPublicKey;
import utils.ByteUtils;

import java.nio.charset.StandardCharsets;

public final class Token {
  private final String m_jsonData;
  private final byte[] m_signature;

  private Token(String jsonData, byte[] signature) {
    m_jsonData = jsonData;
    m_signature = signature;
  }

  public String GetJsonData() {
    return m_jsonData;
  }

  public boolean IsValid(RsaPublicKey key) {
    byte[] dataBytes = m_jsonData.getBytes(StandardCharsets.UTF_8);
    byte[] hash = MD2.Hash(dataBytes);
    return RSA.Verify(hash, m_signature, key);
  }

  @Override
  public String toString() {
    return "Token{" +
      "m_jsonData='" + m_jsonData + '\'' +
      ", m_signature=" + ByteUtils.BytesToStringHex(m_signature, false) +
      '}';
  }

  public static Token Create(String jsonData, RsaPrivateKey key) {
    byte[] dataBytes = jsonData.getBytes(StandardCharsets.UTF_8);
    byte[] hash = MD2.Hash(dataBytes);
    byte[] signature = RSA.Sign(hash, key);
    return new Token(jsonData, signature);
  }
}
