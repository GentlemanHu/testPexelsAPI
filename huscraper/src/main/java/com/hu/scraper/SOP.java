package com.hu.scraper;
/* 
 Test synchronized this keyword means
 
*/
public class SOP {
      public static void print(String s) {
            System.out.println(s + "\n");
      }
}

class TestThread extends Thread {
      String name;
      TheDemo theDemo;

      public TestThread(String name, TheDemo theDemo) {
            this.theDemo = theDemo;
            this.name = name;
            start();
      }

      @Override
      public void run() {
            theDemo.test(name);
      }
}

class TheDemo {
      public synchronized void test(String name) {
            for (int i = 0; i < 10; i++) {
                  SOP.print(name + " :: " + i);
                  try {
                        Thread.sleep(500);
                  } catch (Exception e) {
                        SOP.print(e.getMessage());
                  }
            }
      }

      public static void main(String[] args) {
            TheDemo theDemo = new TheDemo();
            new TestThread("THREAD 1", theDemo);
            new TestThread("THREAD 2", theDemo);
            new TestThread("THREAD 3", theDemo);
      }
}