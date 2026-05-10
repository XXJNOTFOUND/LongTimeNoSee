# LongTimeNoSee ⏰

一个《我的世界》Spigot/Paper 插件，记录玩家最后一次下线时间，并在重新登录时显示离线时长。

> **好久不见，欢迎回来！**

## 功能

- 首次登录时发送欢迎消息（可配置）
- 再次登录时显示精确的离线时间差（天、小时、分钟、秒）
- 使用 SQLite 数据库自动保存玩家数据
- 支持 `/ltns check <玩家名>` 查询任意玩家的离线时间
- 所有消息均可在 `config.yml` 中自定义
- 支持重载配置（`/ltns reload`）

## 指令

| 指令 | 权限 | 说明 |
|------|------|------|
| `/ltns reload` | `ltns.admin` (默认 OP) | 重载插件配置并重新连接数据库 |
| `/ltns check <玩家名>` | `ltns.check` (默认所有人) | 查询指定玩家的离线时长 |

## 配置

```yaml
messages:
  prefix: "§7[§bLongTimeNoSee§7] "
  first-join: "§a欢迎来到服务器！这是你的第一次登录！"
  return-join: "§e欢迎回来！你已经离线了: §c%days%天 %hours%小时 %minutes%分钟 %seconds%秒"
  no-permission: "§c你没有权限执行此指令！"
  reloaded: "§a插件配置已重载！"
  check-usage: "§c用法: /ltns check <玩家名>"
  player-not-found: "§c数据库中未找到该玩家的记录。"
  check-result: "§e玩家 §a%player% §e最后一次下线是在 §c%time% §e前。"
```

## 安装

1. 下载 `LongTimeNoSee-1.4.0.jar`
2. 将文件放入服务器的 `plugins` 文件夹
3. 重启服务器

## 构建

```bash
mvn clean package
```

## 依赖

- Spigot / Paper 1.13+
- Java 21+

## 作者

- **iXiangPro**
- **Little-Xiang-cookie**
