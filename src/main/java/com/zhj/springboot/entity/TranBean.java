package com.zhj.springboot.entity;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TranBean {

        String word=null;
        public Timestamp timestamp=null;
        List<String> trans=null;


        public TranBean() {
            word="";
            String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//将时间格式转换成符合Timestamp要求的格式.
            timestamp=Timestamp.valueOf(nowTime);
            trans=new ArrayList<String>();
        }




//以下为自动生成的get+set+tostring


        public String getWord() {
            return word;
        }


        public void setWord(String word) {
            this.word = word;
        }


        public List<String> getTrans() {
            return trans;
        }


        public void setTrans(List<String> trans) {
            this.trans = trans;
        }

        public void addTrans(String tran) {
            this.trans.add(tran);
        }


        public Timestamp getTimestamp() {
            return timestamp;
        }


        public void setTimestamp(Timestamp timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return "TransBean [word=" + word + ", timestamp=" + timestamp + ", trans=" + trans + "]";
        }




}
