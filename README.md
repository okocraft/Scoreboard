![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/okocraft/Scoreboard)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/okocraft/Scoreboard/Java%20CI)
![GitHub](https://img.shields.io/github/license/okocraft/Scoreboard)

# Scoreboard

ゲーム画面の右側にカスタマイズしたスコアボードを表示する Spigot プラグインです。

## Requirements

- Java 11+
- Spigot 1.15+
- (Optional) [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
- (Optional) [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/)

## Usage

サーバーディレクトリの `/plugins/` に配置し、サーバーを再起動する。

`config.yml` で表示される文字数の制限と ProtocolLib の使用、`default.yml` でボードの表示設定ができます。

```yaml
# default.yml 設定例

title: # 最上部に表示される
  interval: 0 # 更新間隔 (tick), 0 以下で更新されない
  list: # 更新するごとの表示順序
    - "&8------ &bServer &8------"

line: # 表示する行設定
  empty-1: # 一意な名前
    interval: 0
    # list を設定しない場合、空の行となる

  server-status:
    interval: 60
    list:
      - " %player_ping%ms&7"
      - " %Imperatrix_tps% TPS"
```

## License

このプロジェクトは GPL-3.0 のもとで公開しています。詳しくは [ライセンスファイル](LICENSE) をお読みください。

This project is licensed under the permissive GPL-3.0 license. Please see [LICENSE](LICENSE) for more info.

Copyright © 2019-2020, Siroshun09
