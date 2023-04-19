package com.alibaba.nacos.plugin.auth.impl.utils;


import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class ParamsEncryptUtilTest {
  @Test
  public void test() {
    String encrypted = ParamsEncryptUtil.getInstance().encrypAES("nacos");
    assertThat(encrypted, org.hamcrest.Matchers.is("q2Uya6oSC2xUyPaTh95AOg=="));
    String plaintext = ParamsEncryptUtil.getInstance().decryptAES(encrypted);
    assertThat(plaintext, org.hamcrest.Matchers.is("nacos"));
  }
}