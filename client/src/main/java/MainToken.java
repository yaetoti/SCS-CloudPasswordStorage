import com.google.gson.Gson;
import network.payload.AccessTokenData;
import network.payload.Token;
import rsa.RSA;
import rsa.RsaKeyPair;

public class MainToken {
  public static void main(String[] args) {
    Gson gson = new Gson();

    AccessTokenData data = new AccessTokenData();
    data.userId = 123456789;
    data.issuedAt = 1616239020;
    data.expiresAt = 1616239020 + 60 * 60 * 24 * 30;
    data.isAdmin = true;

    RsaKeyPair keys = RSA.GenerateKeys(128);
    Token token = Token.Create(gson.toJson(data), keys.privateKey);
    System.out.println("Token: " + token);
    System.out.println("IsValid: " + token.IsValid(keys.publicKey));
  }
}
