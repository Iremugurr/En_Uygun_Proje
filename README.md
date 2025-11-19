Bu proje, **Enuygun benzeri uçuş arama web uygulaması** üzerinde çalışan testleri otomatikleştirmek için hazırlanmıştır. Testler **Java + Selenium WebDriver + TestNG + Maven** kullanılarak yazılmıştır.

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

Mimari: Page Object Model (POM)
-----------------------------

Her sayfa bir “Page Class” olarak tasarlanmıştır:

BasePage → Ortak methodlar

HomePage → Arama ekranı

FlightListingPage → Uçuş listesi ekranı

Test sınıfları → Sadece senaryoları içerir

Loglama
--------

Loglar resources/log4j2.xml üzerinden yönetilir.

Teşekkürler,
İrem Uğur

