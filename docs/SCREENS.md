# Pollen Club — Screen-by-Screen Documentation

Documented from live inspection of the original app (v6.3.6) on 2026-04-26.
Screenshots saved in `docs/screenshots/`.

---

## Navigation Structure

### Bottom Navigation Bar (5 tabs)

| # | Icon       | Screen              | Description                          |
|---|------------|---------------------|--------------------------------------|
| 1 | House      | Allergy Forecast    | Pollen levels + forecast charts      |
| 2 | Leaf/Plant | Phenology           | Community flowering stage reports     |
| 3 | Smiley     | Health Diary        | Symptom logging + medication tracker |
| 4 | Chat       | Social Feed         | News, community posts, media         |
| 5 | Map pin    | Risk Map            | Google Maps with pollen risk overlay |

### Hamburger Menu (Drawer)

Accessible from all screens via ≡ icon (top-left). Contains:
- **User ID** — "Код участника: {numeric_id}" with copy button
- **Contact link** — "Напишите нам" (Write to us)
- **User profile summary:**
  - Самочувствие (Well-being) — current mood/status
  - Симптомы (Symptoms) — "Не отмечено" if none logged today
  - Осн. аллерген (Main allergen) — e.g. "Берёза"
  - Регион мониторинга (Monitoring region) — e.g. "Москва"
- **Links:** Сайт (Website), Группа (Social group)
- **Настройки (Settings)**

Screenshot: `17_drawer_menu.png`

---

## Screen 1: Allergy Forecast (Home)

**Title bar:** "Аллерго прогноз"
**Sub-header:** Date (e.g. "26 апреля") + Location (e.g. "Москва")
**Top-right:** Heart icon → "Стать соучастником" (Become a contributor) CTA

### Allergen List

A vertically scrollable list of **10 allergens**, each row showing:
- **Botanical illustration** (line-art icon)
- **Name** (Russian)
- **Current pollen level** — color-coded text
- **Green checkmark** on the user's main allergen
- **">" toggle arrow** — expands/collapses an inline forecast chart

#### All 10 Allergens

| # | Russian        | English           | Type   |
|---|----------------|-------------------|--------|
| 1 | Берёза         | Birch             | Tree   |
| 2 | Дуб            | Oak               | Tree   |
| 3 | Ольха          | Alder             | Tree   |
| 4 | Полынь         | Mugwort           | Weed   |
| 5 | Орешник        | Hazel             | Tree   |
| 6 | Злаки          | Grasses           | Grass  |
| 7 | Маревые        | Chenopodiaceae    | Weed   |
| 8 | Амброзия       | Ragweed           | Weed   |
| 9 | Кладоспориум   | Cladosporium      | Mold   |
| 10| Альтернария    | Alternaria        | Mold   |

#### Pollen Level Scale (6 levels, bottom to top)

| Level            | Translation    | Color  |
|------------------|----------------|--------|
| Нулевой          | Zero/None      | White  |
| Низкий           | Low            | Green  |
| Средний          | Medium         | Yellow |
| Высокий          | High           | Red    |
| Очень высокий    | Very High      | Red    |
| Экстра           | Extra          | Red    |

### Inline Forecast Chart

When a row is expanded, a line chart appears showing:
- **X-axis:** Dates (about 7 days: ~3 past + today + ~3 future)
- **Y-axis:** The 6 pollen levels listed above
- **Historical data:** Filled red/pink area under the curve (past days)
- **Forecast data:** White line with dot markers (future days)
- **Today:** Vertical line marker, date bolded on x-axis

The chart is the full width of the screen and takes up most of the viewport when expanded. Tapping ">" again collapses it.

Screenshots: `05_home_allergy_forecast.png`, `06_home_allergy_forecast_scrolled.png`, `07_allergen_detail_birch.png`

---

## Screen 2: Phenology (Фенологи)

**Title bar:** "Фенологи"
**Body text:** "Отмечайте стадию цветения березы, чтобы другие пользователи имели более точную картину пыления, а также, чтобы смотреть динамику отметок за все время" (Mark birch flowering stages for community benefit and to track dynamics over time)
**FAB:** Globe with pointing hand → opens stage selection dialog

### Stage Selection Dialog

**Header:** "ЗАКРЫТЬ" (Close) / "ГОТОВО" (Done)
**6 flowering stages** with illustrated icons:

| # | Russian                | English               |
|---|------------------------|-----------------------|
| 1 | Начало сокодвижения    | Start of sap flow     |
| 2 | Набухание почек        | Bud swelling          |
| 3 | Распускание почек      | Bud opening           |
| 4 | Развертывание листьев  | Leaf unfolding        |
| 5 | Начало цветения        | Start of flowering    |
| 6 | Завершение цветения    | End of flowering      |

**Comment field:** "Написать комментарий к стадии" (Write a comment)
**Location:** Auto-detected GPS location shown at bottom

Screenshots: `08_phenology_screen.png`, `09_phenology_stages_dialog.png`

---

## Screen 3: Health Diary (Smiley icon)

Two tabs: **Симптомы** (Symptoms) and **Терапия** (Therapy)

### 3a. Symptoms Tab

**Layout:**
- **Top tabs:** Симптомы | Терапия
- **Week day selector:** Horizontal row of 7 days (Mon–Sun) with day number and abbreviation. Today is highlighted with a background. Days with data are highlighted differently.
- **Calendar icon** (top right) — presumably opens a full calendar picker
- **Body map illustration** — Full-screen female face/upper body with **clickable zones** marked by circles:
  - Forehead/head (brain/headache)
  - Eyes (left + right)
  - Nose
  - Mouth/throat
  - Chest/lungs
  - Lower body
  - Skin zone (right side)
- **Tooltip:** "Выберите проблемную зону" (Select problem zone)
- **Overall feeling selector** at bottom: 3 options
  - Хорошо (Good) — green smiley
  - Терпимо (Tolerable) — yellow neutral face
  - Плохо (Bad) — red frown face

### Symptom Selection (Bottom Sheet)

Tapping a body zone opens a bottom sheet titled "Отметьте состояния в области {zone}" with a list of symptoms specific to that zone. Each symptom has an illustrated icon and a checkbox.

**Nose zone symptoms (example):**
| Symptom                        | Translation              |
|--------------------------------|--------------------------|
| Зуд в носу                    | Itchy nose               |
| Заложенность носа              | Nasal congestion         |
| Ринорея                       | Rhinorrhea (runny nose)  |
| Чихание < 10 раз подряд       | Sneezing < 10 times     |
| Чихание > 10 раз подряд       | Sneezing > 10 times     |
| Носовые кровотечения           | Nosebleeds               |

Other zones (eyes, throat, lungs, skin, head) follow the same pattern with zone-specific symptoms.

Screenshots: `10_health_diary_symptoms.png`, `11_symptom_nose.png`

### 3b. Therapy Tab

**Header:** "Какие препараты принимаете?" (What medications are you taking?)
**Search icon** (magnifying glass) for medication search

**6 medication categories:**

| Category                          | Translation                    |
|-----------------------------------|--------------------------------|
| Системного действия               | Systemic medications           |
| Глаза                             | Eye medications                |
| Нос                               | Nasal medications              |
| Бронхи                            | Bronchial medications          |
| Кожа                              | Skin medications               |
| Другие средства терапии           | Other therapy methods          |

#### Systemic Medications Subcategories

| Subcategory                                        | Translation                          |
|----------------------------------------------------|--------------------------------------|
| Антигистаминные (перорально)                       | Antihistamines (oral)                |
| Стабилизаторы мембран тучных клеток (перорально)   | Mast cell stabilizers (oral)         |
| Антагонисты лейкотриенов                           | Leukotriene antagonists              |
| АСИТ                                               | Allergen-specific immunotherapy      |
| ГКС (инъекции, таблетки)                           | Corticosteroids (injections/tablets) |
| Комбо (перорально)                                 | Combo medications (oral)             |
| Иммуномодулятор (инъекции)                         | Immunomodulators (injections)        |
| Моноклональные антитела                            | Monoclonal antibodies                |

#### Medication Entry Form

Each medication entry collects:
- **Название препарата** (Drug name) — dropdown
- **Форма выпуска** (Dosage form) — dropdown
- **Доза** (Dose) — dropdown
- **Частота приема** (Frequency) — dropdown
- **Дата начала регулярного приёма** (Start date) — date picker
- **Действующее вещество** (Active ingredient) — text field
- **Info button** (ⓘ) — presumably shows drug information

Screenshots: `12_therapy_tab.png`, `13_therapy_systemic.png`, `14_therapy_medication_form.png`

---

## Screen 4: Social Feed (Chat icon)

**Top tabs:** Новости | Лента | Друзья | Медиа

### 4a. Новости (News)

Expert articles/analysis. Each post shows:
- **Author avatar** (circular)
- **Author name** (e.g. "Андрей Мнение эксперта")
- **Date** in teal color
- **Long-form text body** — detailed pollen situation reports by region

Screenshot: `02_feed_news_tab.png`

### 4b. Лента (Feed)

Community text reports grouped by date and city. Format:
- **Date header** (e.g. "25 апреля") in orange
- **City header** (e.g. "Москва") in magenta
- **User reports** as continuous text blocks, each starting with the user's name followed by their location and symptom description

No images, no avatars, no interaction buttons — just plain text entries.

Screenshot: `03_feed_lenta_tab.png`

### 4c. Друзья (Friends)

Shows posts from added friends. Empty if no friends added.

Screenshot: `04_feed_friends_tab.png`

### 4d. Медиа (Media)

User-submitted photo posts in a RecyclerView with pull-to-refresh (swipeRefresh). Each item:
- **Date header** (teal)
- **Text description** — user name, location, observation text
- **Photo** — full-width image

Screenshot: `01_feed_media_tab.png`

---

## Screen 5: Risk Map (Map pin icon)

**Title bar:** "{Allergen}. Карта рисков. {N} баллов" (e.g. "Берёза. Карта рисков. 7 баллов")
**Info button** (ⓘ) → opens legend dialog

### Map Content

- **Google Maps** base layer
- **Colored polygon overlays** showing pollen risk zones:
  - Orange/red = high risk
  - Yellow = medium risk
  - Green = low risk
- **Cluster markers** — numbered pins showing observation count in an area, colored by type:
  - Green/teal = low/good
  - Yellow = moderate
  - Orange/red = high/bad
- **Blue dot** — user's current GPS location
- **GPS crosshair button** (top right)
- **Ad banner** at bottom (e.g. Ballu air purifier)

### Filter Chips (Horizontal scrollable)

| Chip    | Full name                              | Description                         |
|---------|----------------------------------------|-------------------------------------|
| Друзья  | Friends                                | Markers of added friends            |
| АГ      | Антигистаминные (Antihistamines)       | Users taking antihistamines         |
| АСИТ    | Allergen-specific immunotherapy        | Users undergoing ASIT treatment     |
| Бризер  | Breezer (air purifier)                 | Users with air purification systems |
| Дети    | Children                               | Children with allergies             |
| Маски   | Masks                                  | Users wearing masks                 |
| Побег   | Escape                                 | Users who left the blooming zone    |
| СижуДома| Stay Home                              | Users in self-isolation             |

Screenshots: `15_map_screen.png`, `16_map_legend.png`

---

## Screen 6: Settings (Настройки)

Accessed via Drawer → Настройки.

### Основные (Main)

| Setting              | Description                              | Current value    |
|----------------------|------------------------------------------|------------------|
| Язык/Language        | App language (Russian/English)           | Русский          |
| Осн. аллерген       | Main allergen — single select from 10    | Берёза           |
| Регион мониторинга   | Monitoring region — single select        | Москва           |
| Друзья               | Friends management                       | —                |

### Available Monitoring Regions (8 cities)

1. Москва (Moscow)
2. Ростов-на-Дону (Rostov-on-Don)
3. Воронеж (Voronezh)
4. Краснодар (Krasnodar)
5. Волгоград (Volgograd)
6. Екатеринбург (Yekaterinburg)
7. Ставрополь (Stavropol)
8. Санкт Петербург (Saint Petersburg)

### Информация (Information)

| Item                          | Description                              |
|-------------------------------|------------------------------------------|
| Справочник аллергенов         | Allergen reference — 2-column grid of allergen cards with botanical illustrations and detailed info dialogs |
| Руководство по использованию  | User guide                               |

### Выход (Exit/Logout)

Screenshots: `18_settings.png`, `19_allergen_selector.png`, `20_region_selector.png`, `21_allergen_reference.png`, `22_allergen_detail_info.png`

---

## Allergen Reference Detail

Each allergen card in the reference grid shows:
- Botanical line-art illustration (large)
- Name label
- ⓘ info button → opens a dialog with:
  - Icon + name + "ЗАКРЫТЬ" (Close) button
  - Multi-paragraph educational text about the plant:
    - Latin name, family classification
    - Geographic distribution
    - Pollen season timing
    - Peak pollen concentration data
    - Botanical description

Screenshot: `22_allergen_detail_info.png`

---

## Cross-Cutting Features

### Identity
- No login/registration — server assigns a numeric `user_id` on first launch
- User ID shown as "Код участника: {id}" in the drawer with copy button

### Localization
- Two languages: Russian (Русский) and English
- Server sends `_rus`/`_eng` field pairs; app picks based on setting

### Ads
- Banner ad at bottom of map screen (server-driven)
- "Стать соучастником" (Become a contributor) CTA on forecast screen — likely premium/donation

### Data Flow
- Pull-to-refresh on feed screens (swipeRefresh)
- Week-based date selection in health diary
- GPS auto-detection for phenology observations and map centering
