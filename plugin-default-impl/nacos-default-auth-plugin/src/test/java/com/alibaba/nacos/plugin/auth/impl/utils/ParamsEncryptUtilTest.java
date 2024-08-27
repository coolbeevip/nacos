package com.alibaba.nacos.plugin.auth.impl.utils;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParamsEncryptUtilTest {
  @Test
  public void test() {
    String encrypted = ParamsEncryptUtil.getInstance().encrypAES("nacos");
    assertEquals(encrypted, "q2Uya6oSC2xUyPaTh95AOg==");
    String plaintext = ParamsEncryptUtil.getInstance().decryptAES(encrypted);
    assertEquals(plaintext, "nacos");
  }
}