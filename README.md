# WEB API テンプレート

Scalaとhttp4sを使ったWEB APIのテンプレートです。

## 環境構築

- 開発したい人 → webサーバはローカルで構築、その他はdockerで構築
- 動作させたい人 → dockerで構築

### ローカルで構築

#### 依存サービスの構築

```bash
make dev/setup
```

#### webサーバの構築

sbtのインストール

https://www.scala-sbt.org/1.x/docs/ja/Setup.html

テスト実行

```bash
sbt test
```

起動

```bash
sbt ~reStart
```

http://localhost:8000/ にアクセス

### dockerで構築

```bash
make setup
```
