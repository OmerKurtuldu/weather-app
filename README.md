### Weather API

Bu proje, şehir adına göre mevcut hava durumu raporlarını sağlayan bir API sunar.

**Çalışma Mantığı:**
- **API İsteği:** `/v1/api/weather/{city}` URL'si üzerinden yapılır.
- **Şehir Doğrulaması:** Şehir değeri boş veya ondalık olamaz; aksi takdirde hata mesajı döner.
- **Veri Kaynağı:**
  - **Veritabanı:** Veriler, PostgreSQL veritabanından alınır ve eğer 30 dakikadan daha eski değilse veritabanından getirilir.
  - **WeatherStackAPI:** Veritabanındaki veri 30 dakikadan eskiyse veya mevcut değilse, yeni veri WeatherStackAPI'den alınır ve önbelleğe kaydedilir.
  - **Önbellek:** Önbellekte veri varsa, doğrudan buradan döner.

**Kurulum Talimatları:**
- **PostgreSQL Veritabanı:**
  - `weatherdb` adında bir veritabanı oluşturun.
  - Uygulama ayarları veya `.env` dosyasındaki gerekli PostgreSQL yapılandırmalarını güncelleyin (ör. `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`).

**Swagger Arayüzü:**
Swagger arayüzüne [buradan](http://localhost:8080/swagger-ui/index.html) ulaşabilirsiniz.

**Ortam Değişkenleri:**
- `.env` dosyasına `API_KEY` ekleyin.

### Teknolojiler

- Java 17
- Spring Boot 3.0
- Spring Data JPA
- PostgreSQL
- Restful API
- Maven
- Docker
- Docker Compose
- Github Actions
- Prometheus
- Grafana

### Gereksinimler

- Maven veya Docker

### Docker ile Çalıştırma

1. Proje dizinine gidin:
    ```bash
    $ cd weather
    ```
2. Docker Compose ile başlatın:
    ```bash
    $ docker-compose up -d
    ```

Swagger arayüzüne http://localhost:9595/swagger-ui.html üzerinden ulaşabilirsiniz.

### Maven ile Çalıştırma

1. Proje dizinine gidin:
    ```bash
    $ cd weather
    ```
2. Projeyi inşa edin:
    ```bash
    $ mvn clean install
    ```
3. Uygulamayı çalıştırın:
    ```bash
    $ mvn spring-boot:run
    ```

Swagger arayüzüne http://localhost:8080/swagger-ui.html üzerinden ulaşabilirsiniz.
