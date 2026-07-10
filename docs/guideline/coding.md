# コーディング規約

## nullの取扱いについて

- メソッドや引数には、必ず、JSpecifyを使ったannotationを付すこと
    - nullable, nonnull, nullMarkedなど
- できる限り、nonnullとなるようにすること

## 変数の扱いについて
 
- 値の再代入はできるだけ避け、"final" を付与する 
- 型推論 (var)はテスト以外では用いないこと

## メソッドの扱いについて

- Javadocを必ず書くこと
- できるだけ、メソッドは関数型になるようにすること
- アクセス修飾子を適切に管理すること
- テスト用にどうしても修飾子を変更する必要がある場合は、
   - package private にすること
   - @VisibleForTestingを付加する
  
