# ddd-sample

## ドメイン駆動設計とは

- ソフトウェアを「業務のルール（ドメイン）」を中心に設計する考え方
- ドメインとはそのシステムが解決したい業務領域

## DDDにおける構成要素

- Application:
    - application service: ドメインの処理を行う
- Domain: 「知識」を表す
    - Value Object: 変化することのない値、属性が同じなら同一
    - Entity: 変化しうる値、属性が同じでも一緒とは限らない (e.g. 人名)
    - Domain Service: ドメインを操作するもの。valueやentity内に含めるのがおかしい場合に作成
    - factory: ドメインを生成するための役割をもったもの。ドメインの生成が複雑な場合に利用する。
    - specification: オブジェクトの評価を行うオブジェクト。serviceなどに書くと流れがわからなくなる場合に利用。
- InfraStructure:
    - repository: データに関する処理を行う
    - O/R mapper: データベースとobjectの相互マッピング


## DDD を実現するためのアーキテクチャ


### Layered Architecture

データの処理体系を以下の4層に分割し、依存関係が上から下になるようにする。

- Presentation: 入出力。viewやcontroller、表示のためのHelperなど。
- Application: ドメインを利用して実際の処理を駆動する。
- Domain: ドメイン
- InfraStructure: データベースへのアクセス、メッセージ送信など


### Clean Architecture

ベーシックなLayered ArchitectureにDIP (c.f. SOLID原則) を適用すれば、以下の依存関係になる。
こんなイメージ -> https://terasolunaorg.github.io/guideline/current/ja/Overview/ApplicationLayering.html

依存関係を逆転させる上で、IntraStructureのインターフェースが、Domainに設定されることになる。
すなわち、  
domain -> Infrastructure  
だったものが、  
domain -> repository (in Domain) <- repositoryImpl (in Infrastructure)  
となる。

## DDDのレポジトリ構成の例

```
java/
├── presentation/
│   ├── controller/
│   ├── request/
│   ├── response/
│   └── exception/
│
├── application/
│   ├── usecase/ 
│   └── dto/ # domainをpresentationに渡すためにdtoを利用
│
├── domain/
│   ├── model/
│   │   ├── entity/
│   │   ├── valueobject/
│   │   ├── aggregate/
│   │   └── event/
│   ├── repository/ # interface
│   ├── client/ # interface
│   ├── exception/ # ドメインに関わるexception
│   ├── service/ # domain service
│   ├── factory/
│   └── specification/
│
├── infrastructure/
│   ├── db/ #database用    
│   │   ├── record/ #entityでもいいが、domainと紛らわしいため
│   │   ├── repository/ # repositoryImpl
│   │   ├── mapper/ # Mybatis想定でmapper
│   │   └── dao/ # mapperをラップする場合などに使う
│   └── client/ #APIリクエスト用    
│       ├── dto/ 
│       └── clientImpl
│
└── config/ # 設定クラス（DDDのレイヤーに属さない横断的な設定）
```

※ データの詰め替え

- infra層で record/dto <-> domain
- Application層で domain <-> dto
- Presentation層で dto -> Response, request -> dto
