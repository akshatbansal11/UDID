package com.udid.download_manager

interface Downloader {
    fun downloadFile(url: String, fileName: String): Long
}