import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import rsa.RSA;
import rsa.RsaKeyPair;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class TestRSA {
  @Test
  public void TestSignatureVerification() {
    String message = "Hello, x86_64!";
    RsaKeyPair keys = RSA.GenerateKeys(256);
    byte[] signature = RSA.Sign(message.getBytes(StandardCharsets.US_ASCII), keys.privateKey);
    boolean verified0 = RSA.Verify(message.getBytes(StandardCharsets.US_ASCII), signature, keys.publicKey);

    Assertions.assertTrue(verified0);

    message = "Hello, ARM!";
    boolean verified1 = RSA.Verify(message.getBytes(StandardCharsets.US_ASCII), signature, keys.publicKey);
    Assertions.assertFalse(verified1);
  }
}
