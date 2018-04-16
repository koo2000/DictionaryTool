# DictionaryTool
辞書を使ったデータベースの日本語名→英語名変換ツール

## 使い方

下記2種類のファイルを入力し、Excelに書いた日本語カラム名を英語カラム名に変換します。
簡単な使い方は下記のとおりです。

|ファイル|サンプル|
|-----------|------------|
|辞書ファイル|src/test/com/github/koo2000/dictionarytool/util/dictionary.xlsx|
|入力ファイル|src/test/com/github/koo2000/dictionarytool/util/input.xlsx|

## 実行
下記コマンドで実行できます。

    mvn package
    java -jar target/dictionarytool-1.0-SNAPSHOT-jar-with-dependencies.jar <辞書ファイル> <入力ファイル> <出力ファイル>

## カスタマイズ
クラス com.github.koo2000.dictionarytool.excel.ExcelTranslationTool のプロパティを設定することで、辞書ファイル、入力ファイル、出力ファイルの形式をある程度変更可能です。
