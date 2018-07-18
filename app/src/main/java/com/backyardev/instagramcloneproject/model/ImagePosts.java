package com.backyardev.instagramcloneproject.model;

public class ImagePosts {


        private String  thumbnailUrl;

        public ImagePosts() {
        }

        public ImagePosts( String thumbnailUrl) {

            this.thumbnailUrl = thumbnailUrl;

        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }


    }