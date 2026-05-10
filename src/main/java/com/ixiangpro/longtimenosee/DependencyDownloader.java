package com.ixiangpro.longtimenosee;

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.List;

public class DependencyDownloader {

    private final LongTimeNoSee plugin;
    private final File libDir;
    private final String jarName = "mysql-connector-j-9.2.0.jar";

    // 下载源列表（按优先级排序）
    private final List<String> MIRRORS = List.of(
            "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.2.0/mysql-connector-j-9.2.0.jar",
            "https://maven.aliyun.com/repository/public/com/mysql/mysql-connector-j/9.2.0/mysql-connector-j-9.2.0.jar",
            "https://mirrors.cloud.tencent.com/nexus/repository/maven-public/com/mysql/mysql-connector-j/9.2.0/mysql-connector-j-9.2.0.jar"
    );

    public DependencyDownloader(LongTimeNoSee plugin) {
        this.plugin = plugin;
        this.libDir = new File(plugin.getDataFolder(), "lib");
    }

    /** 检查 MySQL 驱动是否已存在 */
    public boolean isDriverReady() {
        return new File(libDir, jarName).exists();
    }

    /** 下载 MySQL 驱动，返回是否成功 */
    public boolean downloadDriver() {
        if (isDriverReady()) {
            plugin.getLogger().info("MySQL 驱动已存在，跳过下载。");
            return true;
        }

        if (!libDir.exists()) {
            libDir.mkdirs();
        }

        plugin.getLogger().info("正在下载 MySQL 驱动...");

        for (String url : MIRRORS) {
            if (tryDownload(url)) {
                plugin.getLogger().info("MySQL 驱动下载成功！来源: " + extractHost(url));
                return true;
            }
            plugin.getLogger().warning("从 " + extractHost(url) + " 下载失败，尝试下一个镜像...");
        }

        plugin.getLogger().severe("所有镜像源均无法下载 MySQL 驱动！");
        plugin.getLogger().severe("请手动下载 " + jarName + " 放入 " + libDir.getAbsolutePath());
        return false;
    }

    private boolean tryDownload(String urlStr) {
        Path target = new File(libDir, jarName + ".tmp").toPath();
        try {
            URL url = new URL(urlStr);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);

            try (InputStream in = conn.getInputStream();
                 FileOutputStream out = new FileOutputStream(target.toFile());
                 ReadableByteChannel rbc = Channels.newChannel(in)) {
                out.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }

            // 下载完成，重命名为正式文件名
            Files.move(target, new File(libDir, jarName).toPath(), StandardCopyOption.ATOMIC_MOVE);
            return true;
        } catch (Exception e) {
            // 清理临时文件
            try { Files.deleteIfExists(target); } catch (IOException ignored) {}
            return false;
        }
    }

    /** 将 lib 目录下的 jar 添加到 classpath，返回 URLClassLoader */
    public URLClassLoader createLibClassLoader() {
        try {
            File[] jars = libDir.listFiles((dir, name) -> name.endsWith(".jar"));
            if (jars == null || jars.length == 0) {
                return null;
            }
            URL[] urls = new URL[jars.length];
            for (int i = 0; i < jars.length; i++) {
                urls[i] = jars[i].toURI().toURL();
                plugin.getLogger().info("加载依赖: " + jars[i].getName());
            }
            return new URLClassLoader(urls, getClass().getClassLoader());
        } catch (Exception e) {
            plugin.getLogger().severe("加载依赖库失败: " + e.getMessage());
            return null;
        }
    }

    private String extractHost(String url) {
        try {
            return new URI(url).getHost();
        } catch (Exception e) {
            return url;
        }
    }
}
