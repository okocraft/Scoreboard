# Scoreboard

![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/okocraft/Scoreboard)
![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/okocraft/Scoreboard/maven.yml?branch=master)
![GitHub](https://img.shields.io/github/license/okocraft/Scoreboard)

ゲーム画面の右側にカスタマイズしたスコアボードを表示する Paper プラグインです。

## Requirements

- Java 21+
- Paper 1.20.4+
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

lines: # 表示する行設定
  empty-1: # 一意な名前
    interval: 0
    # list を設定しない場合、空の行となる

  server-status:
    interval: 60
    list:
      - " %player_ping%ms&7"
      - " %server_tps% TPS"
```

また、`./plugins/Scoreboard/boards/` に上記のように設定した Yaml ファイルを置くと、
`/sb show <拡張子なしファイル名>` でボードを表示できます。

### コマンド

- `/sb reload` - `config.yml`, `default.yml`, 言語ファイル, `boards` 下のファイルを再読み込み
- `/sb show {default/ボード名} {プレイヤー}` - 指定したプレイヤーまたは自分にボードを表示する
- `/sb hide {プレイヤー}` - 指定したプレイヤーまたは自分のボードを非表示にする

## 権限

- scoreoreboard.command - `/sb` の実行権限
- scoreoreboard.command.reload - `/sb reload` の実行権限
- scoreoreboard.command.hide - `/sb hide` の実行権限
- scoreoreboard.command.hide.other - `/sb hide {player}` の実行権限
- scoreoreboard.command.show - `/sb show` の実行権限
- scoreoreboard.command.show.other - `/sb show {board name} {player}` の実行権限
- scoreoreboard.show-on-join - サーバー参加時にデフォルトのボードを表示させる権限

## License

このプロジェクトは GPL-3.0 のもとで公開しています。詳しくは [ライセンスファイル](LICENSE) をお読みください。

This project is licensed under the permissive GPL-3.0 license. Please see [LICENSE](LICENSE) for more info.

Copyright © 2019-2024, Siroshun09
