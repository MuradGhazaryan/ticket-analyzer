# Ticket Analyzer

Программа для анализа авиабилетов между городами Владивосток и Тель-Авив.

## Функциональность

- Вычисление минимального времени полета для каждого авиаперевозчика
- Расчет разности между средней ценой и медианой для данного маршрута

## Требования

- Java 11 или выше
- Библиотеки Jackson для работы с JSON (включены в папку `lib/`)

## Структура проекта

```
TicketAnalyzer/
├── src/
│   └── TicketAnalyzer.java    # Основной класс программы
├── lib/                       # Внешние библиотеки
│   ├── jackson-core-2.15.2.jar
│   ├── jackson-databind-2.15.2.jar
│   └── jackson-annotations-2.15.2.jar
├── tickets.json               # Пример входных данных
└── README.md                  # Документация
```

## Сборка и запуск

### Linux/macOS
```bash
# Компиляция
javac -encoding UTF-8 -cp "lib/*" -d . src/TicketAnalyzer.java

# Запуск
java -cp ".:lib/*" TicketAnalyzer tickets.json
```

### Windows
```cmd
# Компиляция
javac -encoding UTF-8 -cp "lib/*" -d . src/TicketAnalyzer.java

# Запуск
java -cp ".;lib/*" TicketAnalyzer tickets.json
```

## Формат входных данных

Программа ожидает JSON файл следующего формата:

```json
{
  "tickets": [
    {
      "origin_name": "Владивосток",
      "destination_name": "Тель-Авив",
      "departure_date": "12.05.18",
      "departure_time": "16:20",
      "arrival_date": "12.05.18",
      "arrival_time": "22:10",
      "carrier": "TK",
      "price": 12400
    }
  ]
}
```

## Пример вывода

```
Минимальное время полета между городами Владивосток и Тель-Авив:
Авиакомпания S7: 14 ч 50 мин
Авиакомпания TK: 5 ч 50 мин

Разница между средней ценой и медианой: 66.67 руб
Средняя цена: 12333.33 руб
Медиана: 12400.00 руб
```

## Использование

```bash
java -cp ".:lib/*" TicketAnalyzer <путь_к_файлу_tickets.json>
```