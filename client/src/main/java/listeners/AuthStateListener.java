package listeners;

import network.AuthService;

public interface AuthStateListener {
  void OnAuthStateChanged(AuthService service);
}
