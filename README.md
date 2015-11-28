# uz-checker
Application checks available trains on the http://booking.uz.gov.ua using Selenium Webdriver. 
Currently only Firefox driver implementation is supported.

### Usage:

``java -jar ./uz-checker-1.0-SNAPSHOT.jar FROM_STATION TO_STATION dd.MM.yyyy H``

Station names must be specified in Ukrainian. The last argument (H - starting hour) is optional.

### Example:
```
Yuriis-MacBook-Pro:libs yuriismyrnov$ java -jar ./uz-checker-1.0-SNAPSHOT.jar Івано-Франківськ Київ 13.12.2015
[
    {
        "number": "143 Л",
        "from": "Івано-Франківськ",
        "to": "Київ-Пасажирський",
        "departure": "неділя, 13.12.2015 17:01",
        "arrival": "понеділок, 14.12.2015 05:51",
        "duration": "12:50",
        "places": {
            "Плацкарт": "9"
        }
    },
    {
        "number": "269 Л",
        "from": "Івано-Франківськ",
        "to": "Київ-Пасажирський",
        "departure": "неділя, 13.12.2015 22:39",
        "arrival": "понеділок, 14.12.2015 11:55",
        "duration": "13:16",
        "places": {
            "Купе": "28",
            "Плацкарт": "26"
        }
    }
]
Yuriis-MacBook-Pro:libs yuriismyrnov$ java -jar ./uz-checker-1.0-SNAPSHOT.jar Івано-Франківськ Київ 13.12.2015 18
[
    {
        "number": "269 Л",
        "from": "Івано-Франківськ",
        "to": "Київ-Пасажирський",
        "departure": "неділя, 13.12.2015 22:39",
        "arrival": "понеділок, 14.12.2015 11:55",
        "duration": "13:16",
        "places": {
            "Купе": "28",
            "Плацкарт": "26"
        }
    }
]
```
