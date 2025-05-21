package utils;

public enum CipherId {
  DOUBLE_PERMUTATION(0, "Double permutation"),
  CAESAR_CIPHER(1, "Caesar cipher"),
  GALOIS_CONFIGURATION(2, "Galois configuration");

  private final long m_id;
  private final String m_name;

  CipherId(long id, String name) {
    m_id = id;
    m_name = name;
  }

  public long GetId() {
    return m_id;
  }

  public static CipherId FromId(long id) {
    for (CipherId cipherId : CipherId.values()) {
      if (cipherId.m_id == id) {
        return cipherId;
      }
    }

    return null;
  }

  @Override
  public String toString() {
    return m_name;
  }
}
