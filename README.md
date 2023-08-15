# WEB API テンプレート

Scalaとhttp4sを使ったWEB APIのテンプレートです。

## 環境構築

以下を実行してください。(結構時間かかります。)

```bash
make dev/setup
```

statusがhealthyになったら、http://localhost:8080/ にアクセス

```bash
make dev/ps
```

```text
NAME                     COMMAND                  SERVICE             STATUS              PORTS
web-api-template-db-1    "docker-entrypoint.s…"   db                  running (healthy)   0.0.0.0:5432->5432/tcp
web-api-template-web-1   "sbt ~reStart"           web                 running (healthy)   0.0.0.0:8080->8080/tcp
```
