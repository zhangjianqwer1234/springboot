package com.zhj.springboot.util;

import com.zhj.springboot.entity.TranBean;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
    public static void main(String args[]) throws Exception {
       // TranSpider  tranSpider=new TranSpider();
       // TranBean transBean=tranSpider.TranProcess("我不想活了");
       // System.out.println("翻译前内容："+transBean.getWord()+"---翻译后内容："+transBean.getTrans().get(0)+"---其他意思："+transBean.getTrans()+"---翻译时间："+transBean.getTimestamp());

        String uri="https://www.baidu.com/s?ie=utf-8&f=3&rsv_bp=1&tn=25017023_10_pg&wd=org.jsoup.Jsoup%20maven%20pom%20%E9%85%8D%E7%BD%AE&oq=org.jsoup.Jsoup&rsv_pq=814d3cfe000064a2&rsv_t=4c3eAlBbEx8voSb%2BXKiegHDSSUwmbYFef1952Hj7zCAze6pDjhzwemm6BnGcau2GY%2FPiLUk&rqlang=cn&rsv_enter=0&inputT=6974&rsv_sug3=146&rsv_sug2=0&prefixsug=org.jsoup.Jsoup%2520maven%2520pom%2520%25E9%2585%258D%25E7%25BD%25AE&rsp=0&rsv_sug4=7010";
        //Util.DownLoadPage(uri);
        List list=Test.getUrl(uri);
        Iterator it=list.iterator();
        while(it.hasNext()){
            uri=(String)it.next();
            System.out.println(uri);
        }

    }

    public static List getUrl(String uri) throws Exception{
        List list=new ArrayList<>();//用list来存放地址
        URL url=new URL(uri);
        String protocol=url.getProtocol();//获取协议
        String host=url.getHost();//获取域名
        Document doc=Jsoup.connect(uri).get();//dom解析html
        Elements ele=doc.getElementsByTag("a");//获取网页中的a标签
        for(Element a:ele){//遍历
            String href=a.attr("href");
            String nr=a.text();

            /**
             * a标签中有四种值，需要判断，例如：
             * 1.只有路径：/citylist.html
             * 2.含有js代码：javascript:void(0)
             * 3.网址全称：http://www.xuecheyi.com/Info/List-83.html
             * 4.没有后缀/Info
             *
             */
            String reg="[a-zA-z]+://[^\\s]*";
            Pattern p=Pattern.compile(reg);
            Matcher m=p.matcher(href);
            list.add(nr);
            if(m.find()){//通过正则表达式匹配了第三种http://jx.xuecheyi.com/member/login/index
                list.add(href);
            }else if(href.indexOf("/")==0){//匹配第一四两种
                /**
                 * /login/ind
                 * 0123456789
                 * 匹配出来的地址需要在前面加上协议和域名
                 */
                list.add(protocol+"://"+host+href);
            }
        }
        return list;
    }


}
