Bu proje, **Enuygun benzeri uçuş arama web uygulaması** üzerinde çalışan testleri otomatikleştirmek için hazırlanmıştır. Testler **Java + Selenium WebDriver + TestNG + Maven** kullanılarak yazılmıştır.

Proje Yapısı
-----------
En_Uygun_Odev/       (proje root)
├── .gitignore
├── pom.xml
├── README.md
├── testng.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       ├── driver
│   │   │       ├── models
│   │   │       ├── pages
│   │   │       │   ├── BasePage.java
│   │   │       │   ├── HomePage.java
│   │   │       │   └── FlightListingPage.java
│   │   │       └── utils
│   │   │           ├── ConfigReader.java
│   │   │           ├── CSVUtil.java
│   │   │           ├── ScreenshotUtil.java
│   │   │           └── GraphUtil.java
│   │   └── resources
│   │       ├── config.properties
│   │       └── log4j2.xml
│   └── test
│       ├── java
│       │   └── com
│       │       ├── analysis
│       │       │   └── FlightDataAnalysisTests.java
│       │       ├── api
│       │       │   └── PetStoreAPITests.java
│       │       ├── tests
│       │       │   └── BaseTest.java
│       │       └── ui
│       │           └── FlightSearchTests.java
│       └── resources
│           └── testng.xml
├── logs/                (IGNORE - local logs, gitignore içinde olmalı)
├── screenshots/         (optional, gitignore ile kontrol edilebilir)
└── target/              (IGNORE - build outputs)

| Teknoloji / Araç                  | Açıklama                            |
| --------------------------------- | ----------------------------------- |
| **Java 17+**                      | Ana yazılım dili                    |
| **Selenium WebDriver**            | Web otomasyon aracı                 |
| **TestNG**                        | Test koşucu & raporlama framework'ü |
| **Maven**                         | Bağımlılık yönetimi                 |
| **Log4j2**                        | Loglama                             |
| **Extent Reports** *(eklediysen)* | HTML test raporları                 |
| **Page Object Model (POM)**       | Test mimarisi                       |

Test Senaryoları
-----------------
Bu projede otomasyon yapılan ana senaryolar:

Uçuş Arama Testleri

Kalkış – varış şehirlerinin seçilmesi

Gidiş – dönüş tarihinin girilmesi

Arama butonunun çalışması

Liste açıldıktan sonra:

Fiyat filtreleri

Saat filtreleri

Sıralama seçenekleri (fiyat artan/azalan)

Tek yön / gidiş dönüş

Uçuş kartlarındaki bilgilerin doğrulanması

Veri Analizi Testleri
----------------------

Uçuş verilerinin çekilip raporlanması

CSV/JSON çıktı oluşturma

Teşekkürler,
İrem Uğur
