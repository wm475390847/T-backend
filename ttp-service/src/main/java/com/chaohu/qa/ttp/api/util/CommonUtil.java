package com.chaohu.qa.ttp.api.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @author wangmin
 * @date 2023/4/6 16:43
 */
@Slf4j
public class CommonUtil {

    public static String getFileName(String ossPath, String folder) {
        String file = ossPath.split(folder + "/")[1];
        return file.split("\\.")[0];
    }

    /**
     * 删除文件夹
     *
     * @param folder 文件夹
     */
    public static void deleteFolder(File folder) {
        if (folder == null) {
            return;
        }
        // 如果是文件直接删除
        if (folder.isFile()) {
            log.info("删除文件: {} -> {}", folder.getAbsolutePath(), folder.delete());
            return;
        }
        // 如果是目录先删除目录下所有文件和子目录，再删除目录本身
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    log.info("删除文件: {} -> {}", folder.getAbsolutePath(), file.delete());
                }
            }
        }
        log.info("删除目录: {} -> {}", folder.getAbsolutePath(), folder.delete());
    }
}
