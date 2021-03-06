package com.hu.scraper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;

/**
 * XmlParser
 */
public class XmlParser {
      static String xmlString;
      static SAXReader reader;
      static Calendar calendar = Calendar.getInstance();
      static String filepath = "";
      static String key, apikey = "", hostname = "";

      public XmlParser() {
      }

      // read api config
      public static void readConfig() {
            try {
                  try {
                        FileInputStream fis = new FileInputStream("config");
                        InputStreamReader reader = new InputStreamReader(fis);
                        BufferedReader buf = new BufferedReader(reader);

                        String tmp;
                        while ((tmp = buf.readLine()) != null) {
                              if (tmp.contains("hostname")) {
                                    String[] tmpStrings = tmp.split("=");
                                    hostname = tmpStrings[1].replaceAll("\"", "");
                              }
                              if (tmp.contains("api")) {
                                    String[] tmpStrings = tmp.split("=");
                                    apikey = tmpStrings[1].replaceAll("\"", "");
                              }
                              System.out.println(tmp);
                        }
                  } catch (Exception e) {
                        // TODO: handle exception
                  }
                  //System.out.println(apikey);
                  //System.out.println(hostname);
            } catch (Exception e) {
            }
      }

      public static String getXMLData(String key) {
            XmlParser.key = key;
            try {
                  xmlString = Jsoup
                              .connect(("http://" + hostname + "/api/v2.0/indexers/rarbg/results/torznab/api?apikey="
                                          + apikey + "&t=search&cat=&q=" + key))
                              .ignoreContentType(true).execute().body();
                  Document document = DocumentHelper.parseText(xmlString);
                  filepath = "../urls/" + calendar.getTime() + ".xml";
                  FileWriter writer = new FileWriter(filepath);
                  document.write(writer);
                  writer.close();
            } catch (IOException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
            } catch (DocumentException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
            }
            // System.out.println(xmlString);
            return xmlString;
      }

      public static void parseXML(String path) {
            int times = 0;
            String saveString = "";
            SAXReader reader = new SAXReader();
            try {
                  Document document = reader.read(path);
                  Element root = document.getRootElement();
                  Iterator<Element> it = root.elementIterator();

                  while (it.hasNext()) {
                        Element e1 = it.next();
                        System.out.println(e1.getName());

                        Iterator<Element> esu = e1.elementIterator();
                        while (esu.hasNext()) {
                              Element ee = esu.next();
                              // System.out.println(ee.getName());

                              if ("item".equals(ee.getName())) {
                                    if (ee.elementText("guid").matches("(.*)1080(.*)")) {
                                          // System.out.println(ee.elementText("guid"));
                                          saveString = saveString + (ee.elementText("guid") + "\n");
                                          if (++times > 5) {
                                                times = 0;
                                                System.out.println(saveString);
                                                FileOutputStream fOutputStream = new FileOutputStream(
                                                            "../urls/rarbg/" + key + calendar.get(Calendar.DAY_OF_MONTH)
                                                                        + " " + calendar.get(Calendar.HOUR_OF_DAY) + " "
                                                                        + calendar.get(Calendar.MINUTE) + " "
                                                                        + calendar.get(Calendar.SECOND) + ".txt");
                                                fOutputStream.write(saveString.getBytes());
                                                fOutputStream.close();
                                                System.out.println("生成成功");
                                                break;
                                          }

                                    }

                              }
                        }
                  }
            } catch (Exception e) {
                  // TODO: handle exception
            }
      }
}