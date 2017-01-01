package com.marktony.translator.model;

import java.util.ArrayList;

/**
 * Created by lizhaotailang on 2017/1/1.
 */

// sample
// {
// "word": "hi",
//  "pronunciation": {
//      "AmE": "haɪ",
//      "AmEmp3": "https://dictionary.blob.core.chinacloudapi.cn/media/audio/tom/f6/23/F623F7E637597678EB59B9A92D064234.mp3",
//      "BrE": "haɪ",
//      "BrEmp3": "https://dictionary.blob.core.chinacloudapi.cn/media/audio/george/f6/23/F623F7E637597678EB59B9A92D064234.mp3"
//   },
//   "defs":
//   [
//    {
//       "pos": "int.",
//       "def": "〈非正式〉嗨"
//    },
//    {
//      "pos": "abbr.",
//      "def": "夏威夷群岛的书面缩写(=high intensity)高强度"
//    },
//    {
//      "pos": "Web",
//      "def": "你好；血凝抑制(hemagglutination inhibition)；打招呼"
//    }
//   ],
//   "sams":
//   [
//      {
//         "eng": "Hi, buddy, he said he likes Peking duck, How about you?",
//         "chn": "嘿，伙计，他说他喜欢北京烤鸭，你呢？",
//         "mp3Url": "https://dictionary.blob.core.chinacloudapi.cn/media/audio/tom/66/28/662826A26E031E6DC75C29883B369509.mp3",
//         "mp4Url": "https://dictionary.blob.core.chinacloudapi.cn/media/video/cissy/66/28/662826A26E031E6DC75C29883B369509.mp4"
//      }
//   ]
// }

public class BingModel {

    private String word;
    private Pronunciation pronunciation;
    private ArrayList<Definition> defs;
    private ArrayList<Sample> sams;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Pronunciation getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(Pronunciation pronunciation) {
        this.pronunciation = pronunciation;
    }

    public ArrayList<Definition> getDefs() {
        return defs;
    }

    public void setDefs(ArrayList<Definition> defs) {
        this.defs = defs;
    }

    public ArrayList<Sample> getSams() {
        return sams;
    }

    public void setSams(ArrayList<Sample> sams) {
        this.sams = sams;
    }

    public class Pronunciation {

        private String AmE;
        private String AmEmp3;
        private String BrE;
        private String BrEmp3;

        public String getAmE() {
            return AmE;
        }

        public void setAmE(String amE) {
            AmE = amE;
        }

        public String getAmEmp3() {
            return AmEmp3;
        }

        public void setAmEmp3(String amEmp3) {
            AmEmp3 = amEmp3;
        }

        public String getBrE() {
            return BrE;
        }

        public void setBrE(String brE) {
            BrE = brE;
        }

        public String getBrEmp3() {
            return BrEmp3;
        }

        public void setBrEmp3(String brEmp3) {
            BrEmp3 = brEmp3;
        }
    }

    public class Definition {

        private String pos;
        private String def;

        public String getPos() {
            return pos;
        }

        public void setPos(String pos) {
            this.pos = pos;
        }

        public String getDef() {
            return def;
        }

        public void setDef(String def) {
            this.def = def;
        }
    }

    public class Sample {

        private String eng;
        private String chn;
        private String mp3Url;
        private String mp4Url;

        public String getEng() {
            return eng;
        }

        public void setEng(String eng) {
            this.eng = eng;
        }

        public String getChn() {
            return chn;
        }

        public void setChn(String chn) {
            this.chn = chn;
        }

        public String getMp3Url() {
            return mp3Url;
        }

        public void setMp3Url(String mp3Url) {
            this.mp3Url = mp3Url;
        }

        public String getMp4Url() {
            return mp4Url;
        }

        public void setMp4Url(String mp4Url) {
            this.mp4Url = mp4Url;
        }
    }

}
