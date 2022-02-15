# Scoreboard

![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/okocraft/Scoreboard)
![GitHub](https://img.shields.io/github/license/okocraft/Scoreboard)

master:
![Java CI](https://github.com/okocraft/Scoreboard/workflows/Java%20CI/badge.svg?branch=master)

develop:
![Java CI](https://github.com/okocraft/Scoreboard/workflows/Java%20CI/badge.svg?branch=develop)

ゲーム画面の右側にカスタマイズしたスコアボードを表示する Spigot プラグインです。

## Requirements

- Java 16+
- Paper 1.17+
- (Optional) [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)

## Usage

サーバーディレクトリの `/plugins/` に配置し、サーバーを再起動する。

`config.yml` で表示される文字数の制限、`default.yml` でボードの表示設定ができます。

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
