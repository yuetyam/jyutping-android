README in [粵語(Cantonese)](README.md) | [English](README-en.md)

粵拼輸入法
======

<a href="https://t.me/jyutping">
        <img src="images/badge-telegram.png" alt="Telegram" width="150"/>
</a>
<a href="https://www.instagram.com/jyutping_app">
        <img src="images/badge-instagram.png" alt="Instagram" width="150"/>
</a>
<a href="https://www.threads.net/@jyutping_app">
        <img src="images/badge-threads.png" alt="Threads" width="150"/>
</a>
<a href="https://x.com/JyutpingApp">
        <img src="images/badge-twitter.png" alt="X (formerly Twitter)" width="150"/>
</a>
<a href="https://jq.qq.com/?k=4PR17m3t">
        <img src="images/badge-qq.png" alt="QQ" width="150"/>
</a>
<br>
<br>

Android 粵語拼音輸入法。

採用 [香港語言學學會粵語拼音方案](https://jyutping.org/jyutping) ，兼容各種習慣拼寫串法。  
候選詞會標注對應的粵拼。  
支持簡、繁體漢字及各種字形標準。  
可以用倉頡、速成、筆畫、普通話拼音、拆字等反查粵語拼音。

另有 iOS、iPadOS 及 macOS 版: [yuetyam/jyutping](https://github.com/yuetyam/jyutping)

## 擷屏（Screenshots）
<img src="images/screenshot.png" alt="App Screenshot" width="300"/>

## 下載安裝（Download）
<a href="https://play.google.com/store/apps/details?id=org.jyutping.jyutping">
        <img src="images/badge-google-play-download.svg" alt="Google Play badge" width="150"/>
</a>
<br>
<a href="https://play.google.com/store/apps/details?id=org.jyutping.jyutping">
        <img src="images/qrcode-google-play.png" alt="Google Play QR Code" width="150"/>
</a>
<br>
<br>
更多下載方式請前往官網: https://jyutping.app/android

## 如何構建（How to build）
前置要求（Build requirements）：
- Android Studio 2025.1.1+

倉庫體積比較大，建議加 `--depth` 來 clone：
~~~bash
git clone --depth 1 https://github.com/yuetyam/jyutping-android.git
~~~
先構建數據庫 (Prepare databases)：
~~~bash
# cd path/to/jyutping-android
cd ./preparing/
./gradlew run
~~~

接着就可以用 Android Studio 開啓。

## 鳴謝（Credits）
- [Rime-Cantonese](https://github.com/rime/rime-cantonese) (Cantonese Lexicon)
- [OpenCC](https://github.com/BYVoid/OpenCC) (Traditional-Simplified Character Conversion)
- [JetBrains](https://www.jetbrains.com/) (Licenses for Open Source Development)

## 支持作者開發（Support this project）
官網: https://jyutping.app/donate

Patreon: https://patreon.com/bingzheung

愛發電: https://afdian.com/a/jyutping

Ko-fi: https://ko-fi.com/zheung

PayPal: https://paypal.me/bingzheung

Bitcoin: `bc1qx5tjmlvq8ydmfzxt5fru7vqq0khjkhf2savheh`

<img src="images/sponsor.jpg" alt="WeChat Sponsor" width="180"/>
