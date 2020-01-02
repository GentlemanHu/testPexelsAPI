package com.hu.scraper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * downloader
 */
public class Downloader {

      public void outUrl2Txt(ArrayList<String> list, int pageNum, int which, String... name) {
            try {
                  String filename, path;
                  File file;
                  // 选择是哪个网站
                  switch (which) {
                  // pexels路径
                  case 1:
                        // 判断文件名称是否输入
                        if (name.length == 0) {
                              filename = Integer.toString(pageNum);
                              path = "/Users/gentlemanhu/Documents/Codes/Java/scraper/huscraper/urls/pexels/分页/";
                        } else {
                              // 最终文件
                              filename = name[0].toString();
                              path = "/Users/gentlemanhu/Documents/Codes/Java/scraper/huscraper/urls/pexels/总/";
                        }
                        break;
                  // pixabay路径
                  case 2:
                        // 判断文件名称是否输入
                        if (name.length == 0) {
                              filename = Integer.toString(pageNum);
                              path = "/Users/gentlemanhu/Documents/Codes/Java/scraper/huscraper/urls/pixabay/分页/";
                        } else {
                              // 最终文件
                              filename = name[0].toString();
                              path = "/Users/gentlemanhu/Documents/Codes/Java/scraper/huscraper/urls/pixabay/总/";
                        }
                        break;
                  default:
                        if (name.length == 0) {
                              filename = Integer.toString(pageNum);
                              path = "/Users/gentlemanhu/Documents/Codes/Java/scraper/huscraper/huscraper/urls/分页/";
                        } else {
                              // 最终文件
                              filename = name[0].toString();
                              path = "/Users/gentlemanhu/Documents/Codes/Java/scraper/huscraper/huscraper/urls/总/";
                        }
                        break;
                  }

                  //TODO:create dirs
                  

                  // TODO: 希望能相对路径代替，可选路径
                  FileWriter writer = new FileWriter(path + filename +"you"+ ".txt", true);
                  for (String str : list) {
                        writer.write(str + System.lineSeparator());
                  }
                  writer.close();
            } catch (IOException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
            }
      }
}