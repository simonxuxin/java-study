package study.tij.io;

import java.io.*;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by xixu on 2/14/14.
 */
public class MappedIO {
  private static int numOfInts = 4000000;
  private abstract static class Tester {
    private String name;
    public Tester(String name) { this.name = name; }
    public void runTest() {
      System.out.println(name + ": ");
      try {
        long start = System.nanoTime();
        test();
        double duration = System.nanoTime() - start;
        System.out.printf("%.2f\n", duration / 1.0e9);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    public abstract  void test() throws IOException;
  }

  private static Tester[] tests = {
    new Tester("Stream Write")  {
      @Override
      public void test() throws IOException {
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File("temp.tmp"))));
        for (int i = 0; i < numOfInts; i++)
          dos.writeInt(i);
        dos.close();
      }
    },
    new Tester("Mapped Write") {
      @Override
      public void test() throws IOException {
        FileChannel fc = new RandomAccessFile("temp.tmp", "rw").getChannel();
        IntBuffer ib = fc.map(FileChannel.MapMode.READ_WRITE, 0, fc.size()).asIntBuffer();
        for (int i = 0; i < numOfInts; i++)
          ib.put(i);
        fc.close();
      }
    },
    new Tester("Stream Read") {
      @Override
      public void test() throws IOException {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream("temp.tmp")));
        for (int i = 0; i< numOfInts; i++)
          dis.readInt();
        dis.close();
      }
    },
    new Tester("Mapped Write") {
      @Override
      public void test() throws IOException {
        FileChannel fc = new FileInputStream(new File("temp.tmp")).getChannel();
        IntBuffer ib = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size()).asIntBuffer();
        while(ib.hasRemaining())
          ib.get();
        fc.close();
      }
    }


  };

  public static void main(String[] args) {
    for (Tester t : tests)
      t.runTest();
  }
}
