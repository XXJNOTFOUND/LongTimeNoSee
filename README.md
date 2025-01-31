# LongTimeNoSee
一个《我的世界》插件，记录玩家最后一次下线时间，并在重新登录时显示离线时长。

## 功能
- 首次登录时发送欢迎消息
- 再次登录时显示精确的离线时间差（天、小时、分钟、秒）
- 自动保存数据到 SQLite 数据库

## 安装
1. 下载 `LongTimeNoSee-1.2-SNAPSHOT.jar`  <!-- 版本更新 -->
2. 将文件放入服务器的 `plugins` 文件夹
3. 重启服务器

## 配置
编辑 `plugins/LongTimeNoSee/config.yml`：
```yaml
# 是否启用首次登录提醒
enable-first-login-message: true

# 是否启用再次登录提醒
enable-rejoin-message: true

# 消息模板（支持 & 颜色代码）
first-login-message: "&a欢迎回来，{player}！这是你的首次登录！"
rejoin-message: "&b欢迎回来，{player}！你上次在线是 {last_online}，距今 {days} 天 {hours} 小时 {minutes} 分钟 {seconds} 秒。"
