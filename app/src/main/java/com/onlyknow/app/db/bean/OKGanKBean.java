package com.onlyknow.app.db.bean;

import java.util.List;

public class OKGanKBean {
    private boolean error;
    private List<Results> results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<Results> getResults() {
        return results;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }

    public class Results {
        public final String KEY_id = "_id";
        private String _id;

        public final String KEY_createdAt = "createdAt";
        private String createdAt;

        public final String KEY_desc = "desc";
        private String desc;

        public final String KEY_publishedAt = "publishedAt";
        private String publishedAt;

        public final String KEY_source = "source";
        private String source;


        public final String KEY_type = "type";
        private String type;


        public final String KEY_url = "url";
        private String url;

        public final String KEY_used = "used";
        private boolean used;

        public final String KEY_who = "who";
        private String who;

        public final String KEY_itemHeight = "itemHeight";
        private int itemHeight;

        public final String KEY_images = "images";
        private List<String> images;

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public void setPublishedAt(String publishedAt) {
            this.publishedAt = publishedAt;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isUsed() {
            return used;
        }

        public void setUsed(boolean used) {
            this.used = used;
        }

        public String getWho() {
            return who;
        }

        public void setWho(String who) {
            this.who = who;
        }

        public int getItemHeight() {
            return itemHeight;
        }

        public void setItemHeight(int itemHeight) {
            this.itemHeight = itemHeight;
        }

        public String toString() {
            return "GankEntity{" +
                    "_id='" + _id + '\'' +
                    ", createdAt='" + createdAt + '\'' +
                    ", desc='" + desc + '\'' +
                    ", publishedAt='" + publishedAt + '\'' +
                    ", source='" + source + '\'' +
                    ", type='" + type + '\'' +
                    ", url='" + url + '\'' +
                    ", used=" + used +
                    ", who='" + who + '\'' +
                    ", itemHeight=" + itemHeight +
                    ", images=" + images +
                    '}';
        }
    }
}
