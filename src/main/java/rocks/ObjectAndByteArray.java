package rocks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ObjectAndByteArray {
  private ObjectAndByteArray() {
  }

  public static byte[] serialize(Object obj) throws IOException {
    try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
      try (ObjectOutputStream o = new ObjectOutputStream(b)) {
        o.writeObject(obj);
        byte[] bytes = b.toByteArray();
        o.close();
        b.close();
        return bytes;
      }
    }
  }

  public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
    try (ByteArrayInputStream b = new ByteArrayInputStream(bytes)) {
      try (ObjectInputStream o = new ObjectInputStream(b)) {
        Object object = o.readObject();
        o.close();
        b.close();
        return object;
      }
    }
  }

  public static byte[] compress(byte[] data) throws IOException {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    GZIPOutputStream gzip = new GZIPOutputStream(bos);
    gzip.write(data);
    gzip.finish();
    gzip.close();
    byte[] ret = bos.toByteArray();
    bos.close();
    return ret;
  }

  public static byte[] uncompress(byte[] data) throws IOException {
    ByteArrayInputStream bis = new ByteArrayInputStream(data);
    GZIPInputStream gzip = new GZIPInputStream(bis);
    byte[] buf = new byte[1024];
    int num = -1;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    while ((num = gzip.read(buf, 0, buf.length)) != -1) {
      bos.write(buf, 0, num);
    }
    gzip.close();
    bis.close();
    byte[] ret = bos.toByteArray();
    bos.flush();
    bos.close();
    return ret;
  }

  public static byte[] bytesXOR(byte[] first, byte[] second) {
    int len = Math.min(first.length, second.length);
    byte[] byteXOR = new byte[len];
    for (int i = 0; i < len; i++) {
      byteXOR[i] = (byte) (first[i] ^ second[i]);
    }
    return byteXOR;
  }

  public static byte[] getMD5(String msg) {
    MessageDigest md = null;
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    md.reset();
    md.update(msg.getBytes(Charset.defaultCharset()));
    byte[] bytes = md.digest();
    return bytes;
  }

  public static String byteArrayToHexStr(byte[] byteArray) {
    if (byteArray == null) {
      return null;
    }
    char[] hexArray = "0123456789abcdef".toCharArray();
    char[] hexChars = new char[byteArray.length * 2];
    for (int j = 0; j < byteArray.length; j++) {
      int v = byteArray[j] & 0xFF;
      hexChars[j * 2] = hexArray[v >>> 4];
      hexChars[j * 2 + 1] = hexArray[v & 0x0F];
    }
    return new String(hexChars);
  }

  public static String getStringArrayCode(List<String> stringList) {
    if (stringList == null || stringList.size() == 0) {
      return null;
    }

    byte[] result = null;
    for (String str : stringList) {
      if (result == null) {
        result = getMD5(str);
      } else {
        result = bytesXOR(result, getMD5(str));
      }
    }

    return byteArrayToHexStr(result);
  }
}
