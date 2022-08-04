package rocks;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksIterator;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class Main {
  /**
   * java -jar rocksdb-1.0-jar-with-dependencies.jar ./rocksdb/columnPolicies 0 key
   * java -jar rocksdb-1.0-jar-with-dependencies.jar ./rocksdb/columnPolicies 1
   * java -jar rocksdb-1.0-jar-with-dependencies.jar ./rocksdb/columnPolicies 2; more columnPolicies
   *
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    String path = args[0];
    // 0 match key
    // 1 all to file
    String mode = args[1];
    String key = null;
    if (args.length > 2) {
      key = args[2];
    }
    System.out.println("=== === === Start === === === ");

    Options options = new Options();
    options.setCreateIfMissing(true);
    ///data3/rocksdb/tablePolicies
    RocksDB rocksDB = RocksDB.open(options, path);
    RocksIterator rocksIterator = rocksDB.newIterator();

    if (mode.equals("0")) {
      for (rocksIterator.seekToFirst(); rocksIterator.isValid(); rocksIterator.next()) {
        Object deKey = ObjectAndByteArray.deserialize(rocksIterator.key());
        if (null != key && ((String) deKey).contains(key)) {
          Object val = ObjectAndByteArray.deserialize(rocksIterator.value());
          System.out.println("key:" + deKey + ",value:" + val);
        }
      }
    } else if (mode.equals("1")) {
      for (rocksIterator.seekToFirst(); rocksIterator.isValid(); rocksIterator.next()) {
        System.out.println("key:" + new String(rocksIterator.key(), StandardCharsets.UTF_8) +
            ",value:" + new String(rocksIterator.value(), StandardCharsets.UTF_8) + "\n");
        /*System.out.println("key:" + ObjectAndByteArray.deserialize(rocksIterator.key()) +
            ",value:" + ObjectAndByteArray.deserialize(rocksIterator.value()) + "\n");*/
      }
    } else if (mode.equals("2")) {
      String[] paths = path.split(File.separator);
      String fileName = paths[paths.length - 1];
      File file = new File(fileName);
      try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
        for (rocksIterator.seekToFirst(); rocksIterator.isValid(); rocksIterator.next()) {
          byte[] key1 = rocksIterator.key();
          String line = "key:" + ObjectAndByteArray.deserialize(rocksIterator.key()) +
              ",value:" + ObjectAndByteArray.deserialize(rocksIterator.value()) + "\n";
          fileOutputStream.write(line.getBytes(StandardCharsets.UTF_8));
        }
        fileOutputStream.flush();
      }
    }
    System.out.println("=== === === Over === === === ");
  }
}
