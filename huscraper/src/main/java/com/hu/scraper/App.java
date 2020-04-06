package com.hu.scraper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

/**
 * Hello world!
 *
 */
public class App {
    Downloader downloader;
    int hitsNum = 1, pageNum = 1, photoSize = 0;
    ArrayList<String> alist;
    ArrayList<String> blist = new ArrayList<String>();
    Data dt;
    PData pData;
    String data, jdata, fword = "sexy", sword = "blonde", tword = "blonde";
    static ArrayList<String> keylist = new ArrayList<String>();

    public static void main(String[] args) {
        System.out.println("Hello World!");
        XmlParser.readConfig();
        // App ap = new App();
        if (args.length != 0)
            switch (args[0]) {
            case "-k":
                long start = System.currentTimeMillis();
                XmlParser.getXMLData(args[1]);
                XmlParser.parseXML(XmlParser.filepath);
                long stop = System.currentTimeMillis();
                System.out.println("大概耗时:" + (stop - start) + "ms");
                break;
            case "-m":
                getMultiFromFile(args[1]);
                parMultiKey(keylist);
                break;
            default:
                System.out.println("指令格式:\n-k [keywords] \n-m [multiKeywords txt file path]");
                break;
            }
        if (args.length == 0) {
            System.out.println("指令格式:\n-k [keywords] \n-m [multiKeywords txt file path]");
        }
        // ap.getMany(2, 1, 10);

    }

    public static void parMultiKey(ArrayList<String> list) {
        System.out.println("总共解析:" + list.size() + "个关键字，请耐心等待，喝杯咖啡~");
        int index = 1;
        long totaltime = 0;
        for (String string : list) {
            long start = System.currentTimeMillis();
            XmlParser.getXMLData(string);
            XmlParser.parseXML(XmlParser.filepath);
            long stop = System.currentTimeMillis();
            System.out.println("第" + index + "个解析完毕--" + "大概耗时:" + (stop - start) + "ms");
            totaltime += (stop - start);
            index++;
            try {
                Thread.sleep(300);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        System.out.println("-----------\n-----------");
        System.out.println("全部解析完毕，共耗时"+(totaltime/1000)+"s,可以可以666~~~");
    }

    public static ArrayList<String> getMultiFromFile(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            InputStreamReader reader = new InputStreamReader(fis);
            BufferedReader buf = new BufferedReader(reader);

            String tmp;
            while ((tmp = buf.readLine()) != null) {
                keylist.add(tmp);
                System.out.println(tmp);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        return keylist;
    }

    // Another pexels API to get json
    public void getPexelsJson(int pageNum) {
        try {
            long startTime = System.currentTimeMillis();
            // alist每次都创建新对象，只输出每页
            alist = new ArrayList<String>();
            // 获取排行 (https://api.pexels.com/v1/curated?)替换到 tword 为止.每页80个
            // 搜索(https://api.pexels.com/v1/search?query=" + fword + "+" + sword + "+" +
            // tword)
            jdata = Jsoup
                    .connect("https://api.pexels.com/v1/search?query=" + fword + "+" + sword + "+" + tword + "+"
                            + "&per_page=80&page=" + pageNum)
                    .header("Authorization", "563492ad6f9170000100000133bec6a2be57436388af74359bb9dad3")
                    .ignoreContentType(true).execute().body();
            Gson gson = new Gson();
            pData = gson.fromJson(jdata.toString(), PData.class);

            System.out.println("开始——————");
            alist.add("第" + pageNum + "页");
            for (PData.photo photo : pData.photos) {
                System.out.println(photo.id);
                System.out.println(replaceUrl(photo.url));
                System.out.println(photo.url);
                System.out.println(photo.src.original);
                // 链接保存在alist中，输出每页链接
                alist.add(photo.src.original);
                // 每页链接都保存在blist中
                blist.add(photo.src.original);
                System.out.println("\n");
            }
            System.out.println("\n");
            System.out.println("本页共" + pData.photos.size() + "条");
            // output txt
            downloader = new Downloader();
            downloader.outUrl2Txt(alist, pageNum, 1);

            System.out.println("url输出文件为:" + "\"" + pageNum + ".txt" + "\"");
            long stopTime = System.currentTimeMillis();
            System.out.println("本页耗时" + (stopTime - startTime) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }
    }

    // 通过API获取Json数据
    public void getJson(int pageNum) {
        try {

            long startTime = System.currentTimeMillis();
            alist = new ArrayList<String>();
            data = Jsoup
                    .connect("https://pixabay.com/api/?key=4749918-8b58676efa31632352d97594d&q=" + fword + "+" + sword
                            + "+" + tword + "&image_type=photo&per_page=100&page=" + pageNum)
                    .ignoreContentType(true).execute().body();

            Gson gson = new Gson();
            dt = gson.fromJson(data, Data.class);
            System.out.println(dt.totalHits);

            for (Data.hit hit : dt.hits) {
                System.out.println("   " + hit.largeImageURL);
                alist.add(hit.largeImageURL);
            }
            System.out.println("\n");
            System.out.println("本页共" + dt.hits.size() + "条");
            downloader = new Downloader();
            downloader.outUrl2Txt(alist, pageNum, 2);
            long stopTime = System.currentTimeMillis();
            System.out.println("本页耗时" + (stopTime - startTime) + "ms");
        } catch (IOException e) {
            // TODO: specific HttpExceptions..
            System.out.println("API请求达到限制,请减少抓取页数");
            // TODO: handle exception
        }

    }

    public int getHitsNum() {
        return dt.hits.size();
    }

    public void clearHitsNum() {
        dt.hits = null;
    }

    public int getPhotoSize() {
        return pData.photos.size();
    }

    // 获取多页内容
    public void getMany(int what, int start, int stop) {
        long startTime = 0;
        int realPageStart = start;
        switch (what) {
        case 1: {
            startTime = System.currentTimeMillis();
            // From page start to stop page
            for (; start <= stop; start++) {
                getJson(start);
                hitsNum += getHitsNum();
                System.out.println("第" + start + "页");
                System.out.println("\n");
                System.out.println("\n");
                clearHitsNum();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            System.out.println("\n");
            System.out.println("总共" + hitsNum + "条");
            long stopTime = System.currentTimeMillis();
            System.out.println("总共耗时" + (stopTime - startTime) + "ms");
        }
            break;
        case 2: {
            startTime = System.currentTimeMillis();
            // From page start to stop page
            for (; start <= stop; start++) {
                getPexelsJson(start);
                photoSize += getPhotoSize();
                System.out.println("第" + start + "页");
                System.out.println("\n");
                System.out.println("\n");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            // 输出所有链接在一个文件
            downloader = new Downloader();
            downloader.outUrl2Txt(blist, 999, 1, realPageStart + "-" + stop + "页最终文件");
            System.out.println("总的url输出文件为:" + "\"" + realPageStart + "-" + stop + "最终文件" + ".txt" + "\"");
            System.out.println("\n");
            System.out.println("总共" + photoSize + "条");
            long stopTime = System.currentTimeMillis();
            System.out.println("总共耗时" + (stopTime - startTime) + "ms");
        }
            break;
        default:
            break;
        }

    }

    // pixabay API data structure
    static class Data {
        private String totalHits;
        private List<hit> hits;

        static class hit {
            private String tags;
            private String largeImageURL;
            private String pageURL;
            private String webformatURL;
            private String userImageURL;
            private String previewURL;
        }

    }

    // pexels API data structure
    static class PData {
        private String total_results;
        private List<photo> photos;

        static class photo {
            private String id;
            private String photographer;
            private String url;
            private Object src;// Object 解决问题
            // String[] src;

        }

        static class Object {
            private String original;
            private String large2x;
        }
    }

    public String replaceUrl(String url) {
        String data;
        data = url.replace("https://www.pexels.com/photo/", "");
        data = data.replace("/", "");
        return data;
    }

    // 不利用API的抓取,不完美
    public void getPixabay() {
        try {
            long startTime = System.currentTimeMillis();
            // Here we create a document object and use JSoup to fetch the website
            Document doc = Jsoup.connect("https://pixabay.com/").get();

            // With the document fetched, we use JSoup's title() method to fetch the title
            System.out.printf("Title: %s\n", doc.title());

            // Get the list of repositories
            Elements repositories = doc.getElementsByClass("flex_grid credits");
            Elements path = repositories.select("img");

            for (Element repository : path) {
                // Select src
                String url = repository.attr("src");

                System.out.println("\t" + url);
                // System.out.println("\t" + imgPath);
                System.out.println("\n");
            }
            long stopTime = System.currentTimeMillis();
            System.out.println("耗时" + (stopTime - startTime) + "ms");
            // In case of any IO errors, we want the messages written to the console
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
