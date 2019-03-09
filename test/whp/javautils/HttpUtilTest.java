package whp.javautils;

import org.junit.Test;

/**
 * @author haipeng.wang, haipeng.wang@leyantech.com
 * @date 2019-03-09.
 */
public class HttpUtilTest {

  @Test
  public void testGet() throws Exception {
    HttpUtil.get("http://www.baidu.com", 1000);
  }
}
