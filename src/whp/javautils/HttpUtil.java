package whp.javautils;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * @author haipeng.wang, haipeng.wang@leyantech.com
 * @date 2019-03-09.
 */
public class HttpUtil {

  private static final int DEFAULT_TIMEOUT = 30000;
  private static final String DEFAULT_CONTENT_TYPE = "application/json";
  private static final String UTF8 = "UTF-8";
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  private static final HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();

  public static XHttpBaseResponse get(String url) {
    return get(url, DEFAULT_TIMEOUT);
  }

  public static XHttpBaseResponse get(String url, int timeout) {
    return executeHttpMethod(HttpMethod.GET, url, null, null, timeout, null, null);
  }

  public static XHttpBaseResponse post(String url, String data) {
    return post(url, data, DEFAULT_CONTENT_TYPE, DEFAULT_TIMEOUT);
  }

  public static XHttpBaseResponse post(String url, String data, int timeout) throws IOException {
    return post(url, data, DEFAULT_CONTENT_TYPE, timeout);
  }

  public static XHttpBaseResponse post(String url, String data, String contentType) {
    return post(url, data, contentType, DEFAULT_TIMEOUT);
  }

  /**
   * @param data json string
   * @param timeout in ms.
   */
  public static XHttpBaseResponse post(String url, String data, String contentType, int timeout) {
    return executeHttpMethod(HttpMethod.POST, url, data, contentType, timeout, null, null);
  }

  /**
   * HttpMethods
   *
   * @param method @see HttpMethods
   */
  public static XHttpBaseResponse executeHttpMethod(HttpMethod method, String url, String data,
      String contentType, Integer timeout, String proxyUrl, Integer proxyPort) {
    HttpResponse httpResponse = null;
    XHttpBaseResponse xHttpBaseResponse = new XHttpBaseResponse();

    HttpRequestFactory factory = requestFactory;
    long startMS = System.currentTimeMillis();

    try {
      //需要时设置代理
      if (StringUtils.isNotBlank(proxyUrl) && (proxyPort != null && proxyPort > 0)) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyUrl, proxyPort));
        HttpTransport httpTransport = new NetHttpTransport.Builder().setProxy(proxy).build();
        factory = httpTransport.createRequestFactory();
      }

      HttpContent httpContent = null;
      if (contentType == null) {
        contentType = DEFAULT_CONTENT_TYPE;
      }
      if (timeout == null) {
        timeout = DEFAULT_TIMEOUT;
      }

      if (data != null) {
        httpContent = new ByteArrayContent(contentType, data.getBytes(UTF8));
      }

      GenericUrl genericUrl = new GenericUrl(url);
      HttpRequest httpRequest = factory.buildRequest(method.name(), genericUrl, httpContent);
      httpRequest.setReadTimeout(timeout);

      httpResponse = httpRequest.execute();
      if (httpResponse != null) {
        xHttpBaseResponse.setStatusCode(httpResponse.getStatusCode());
        xHttpBaseResponse.setStringContent(httpResponse.parseAsString());
      }
      System.out.println(
          String.format(" XhttpUtil.%s  %s   data:%s  result:%s  time:%d ms ",
              method.name(), url, data,
              xHttpBaseResponse, System.currentTimeMillis() - startMS
          ));
    } catch (HttpResponseException ex) {
      xHttpBaseResponse.setStatusCode(ex.getStatusCode());
      xHttpBaseResponse.setStringContent(ex.getContent());
      System.out.println(
          String.format(" XhttpUtil.%s  %s   data:%s  result:%s  time:%d ms  error: %s ",
              method.name(), url, data,
              xHttpBaseResponse, System.currentTimeMillis() - startMS,
              ex.getMessage()));
    } catch (Exception ex) {
      System.out.println(
          String.format(" XhttpUtil.%s  %s   data:%s  result:%s  time:%d ms  error: %s ",
              method.name(), url, data,
              xHttpBaseResponse, System.currentTimeMillis() - startMS,
              ex.getMessage()));
    }

    return xHttpBaseResponse;
  }

  // private static XHttpBaseResponse execute(HttpRequest httpRequest) throws IOException {
  //   HttpResponse httpResponse = null;
  //   XHttpBaseResponse xHttpBaseResponse = new XHttpBaseResponse();
  //
  //   try {
  //   httpResponse = httpRequest.execute();
  //   if (httpResponse != null) {
  //     xHttpBaseResponse.setStatusCode(httpResponse.getStatusCode());
  //     xHttpBaseResponse.setStringContent(httpResponse.parseAsString());
  //   }
  //   }
  //   catch (HttpResponseException ex) {
  //     logger.error(ex.getMessage(), ex);
  //     xHttpBaseResponse.setStatusCode(ex.getStatusCode());
  //     xHttpBaseResponse.setStringContent(ex.getContent());
  //   }
  //   logger.info("result: " + xHttpBaseResponse.getStringContent());
  //   return xHttpBaseResponse;
  // }

  public static enum HttpMethod {
    CONNECT, DELETE, HEAD, TRACE, OPTIONS,

    GET, POST, PUT, PATCH
  }

  public static class XHttpBaseResponse {

    private int statusCode = -1;
    private String stringContent = "";

    public XHttpBaseResponse() {
    }

    public int getStatusCode() {
      return this.statusCode;
    }

    public void setStatusCode(int statusCode) {
      this.statusCode = statusCode;
    }

    public String getStringContent() {
      return this.stringContent;
    }

    public void setStringContent(String stringContent) {
      this.stringContent = stringContent;
    }

    @Override
    public String toString() {
      return "XHttpBaseResponse{" +
          "statusCode=" + statusCode +
          ", stringContent='" + stringContent + '\'' +
          '}';
    }
  }
}

