package com.zhj.springboot.util;



import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zhj.springboot.entity.TranBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * java实现爬虫
 */
public class TranSpider {
    URL targetUrl=null;

    public  TranSpider() throws MalformedURLException {
            targetUrl=new URL("http://fanyi.youdao.com/translate_o?smartresult=dict&smartresult=rule&sessionFrom=");

    }
    //获取即将发送的message
    public List<NameValuePair> getMessage(String word) throws Exception {

        //获取复杂加密结构------时间戳结构
        long timestamp=(new Date().getTime());
        int random=(int)(Math.random()*10);
        String  salt=String.valueOf(timestamp+random);



        //获取复杂加密结构----sign
        String u = "fanyideskweb";
        String d = word;
        String f = salt;
        String c = "rY0D^0\'nM0}g5Mm1z%1G4";
        String info=(u+d+f+c);
        byte[] infobyte=info.getBytes();


        MessageDigest messageDigest=MessageDigest.getInstance("MD5");
        messageDigest.digest(info.getBytes("utf-8"));
        // 使用指定的字节更新摘要
        messageDigest.update(infobyte);
        // 获得密文
        byte[] md = messageDigest.digest();

        // 把密文转换成十六进制的字符串形式
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        int j = md.length;
        char endSign[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
            byte byte0 = md[i];
            endSign[k++] = hexDigits[byte0 >>> 4 & 0xf];
            endSign[k++] = hexDigits[byte0 & 0xf];
        }

        //获取16进制  md5加密  的sign
        String sign=new String(endSign).toLowerCase();


        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();


        nameValuePairs.add(new BasicNameValuePair("i",word));
        nameValuePairs.add(new BasicNameValuePair("from","AUTO"));
        nameValuePairs.add(new BasicNameValuePair("to","AUTO"));
        nameValuePairs.add(new BasicNameValuePair("smartresult","dict"));
        nameValuePairs.add(new BasicNameValuePair("client","fanyideskweb"));
        nameValuePairs.add(new BasicNameValuePair("salt",salt));
        nameValuePairs.add(new BasicNameValuePair("sign",sign));
        nameValuePairs.add(new BasicNameValuePair("doctype","json"));
        nameValuePairs.add(new BasicNameValuePair("version","2.1"));
        nameValuePairs.add(new BasicNameValuePair("keyfrom","fanyi.web"));
        nameValuePairs.add(new BasicNameValuePair("action","FY_BY_CLlCKBUTTON"));
        nameValuePairs.add(new BasicNameValuePair("typoResult","true"));


        return nameValuePairs;




    }


    public  Header[] getHeader() {

        Header[] headers=new Header[11];
        headers[0]=new BasicHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        //headers[1]=new BasicHeader("Accept-Encoding", "gzip, deflate");
        headers[1]=new BasicHeader("Accept-Encoding", "deflate");
        headers[2]=new BasicHeader("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
        headers[3]=new BasicHeader("Connection", "keep-alive");
        headers[4]=new BasicHeader("X-Requested-With", "XMLHttpRequest");
        headers[5]=new BasicHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers[6]=new BasicHeader("Cookie", "JSESSIONID=aaalHNVSigPD8-hsnhf3v; SESSION_FROM_COOKIE=fanyiweb; OUTFOX_SEARCH_USER_ID=526401539@113.16.65.153; _ntes_nnid=1892114ba72ae7f868a29a4db02914a0,1502250589343; _dict_cpm_show=1502250589350; _dict_cpm_close=1; OUTFOX_SEARCH_USER_ID_NCOO=1688640113.572293; ___rl__test__cookies=1502251640921");
        headers[7]=new BasicHeader("Host", "fanyi.youdao.com");
        headers[8]=new BasicHeader("Origin", "http://fanyi.youdao.com");
        headers[9]=new BasicHeader("Referer", "http://fanyi.youdao.com");
        headers[10]=new BasicHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36");
        //headers[11]=new BasicHeader("Content-Length", "205");

        return headers;
    }



    //格式整理
    public TranBean getForm(String info) {
        TranBean  transBean=new TranBean();

        //string-->json化
        JSONObject  jsonObject=JSONObject.fromObject(info);

        //主要爵士格式转化
        JSONArray maininfo=JSONArray.fromObject(jsonObject.getString("translateResult"));
        maininfo=((JSONArray) maininfo.get(0));
        JSONObject mainTran =JSONObject.fromObject(maininfo.get(0).toString());

        transBean.setWord(mainTran.getString("src"));
        transBean.addTrans(mainTran.getString("tgt"));


        //次要格式转化
        JSONObject smarkinfo= JSONObject.fromObject(jsonObject.getString("smartResult"));
        JSONArray smarkTran= JSONArray.fromObject(smarkinfo.get("entries"));

        for(Object object:smarkTran) {
            if(object!=""||object!=null) {
                transBean.addTrans(object.toString().trim());
            }
        }


        return transBean;
    }



    //用于流程操作 :  传入单词-->单词相关的注解+time
    public TranBean  TranProcess(String word) throws Exception {

        //发送-接收服务器
        //创建httpclient对象
        /*HttpClient httpClient=new DefaultHttpClient();*/
        HttpClient httpClient = HttpClients.createDefault();
        //创建post方式请求对象
        HttpPost httpPost  =new HttpPost();
        //设置发送message  执行发送
        httpPost.setURI(this.targetUrl.toURI());
        //设置参数到请求对象中
        List<NameValuePair>  nameValuePairs=this.getMessage(word);
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"utf-8"));
        // 构造消息头
        httpPost.setHeaders(this.getHeader());
        //执行发送
        HttpResponse httpResponse =httpClient.execute(httpPost);
        //获取实体返回结果
        HttpEntity entity=httpResponse.getEntity();
        String info = EntityUtils.toString(entity, "utf-8");


        //将结果进行格式转换
        TranBean  transBean=this.getForm(info);

        return  transBean;
    }

}

