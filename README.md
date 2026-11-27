# Лаб 11 — Mutation testing (PIT)

Бай: `ScholarshipCalculator` (Лаб 9-ийн засагдсан хувилбар). `src/main`-ийг энэ лабын турш өөрчлөхгүй, зөвхөн тест засна.

## Хэрэгслүүд ба лиценз

| Хэрэгсэл | Лиценз | Тайлбар |
|---|---|---|
| OpenJDK 17+, Maven | GPLv2+CE / Apache 2.0 | Өмнөх лабуудын орчин хэвээр |
| JUnit 5 (Jupiter) | EPL 2.0 — нээлттэй эх | Тестийн фрэймворк |
| JaCoCo | EPL 2.0 — нээлттэй эх | Хамралтын хэрэгсэл |
| PIT (pitest) | Apache 2.0 — нээлттэй эх | Mutation testing хэрэгсэл |
| git + GitHub | GPL / үнэгүй үйлчилгээ | Тайлангаа байршуулах |

Ашигласан орчин: OpenJDK 17.0.17, Apache Maven 3.9.16, PIT 1.16.1 (STRONGER mutators), JaCoCo 0.8.12, macOS.

## Алхам 1: Starter төсөл ба хоёр хэмжилт

`starter.zip`-ийг задалж, юу ч өөрчлөхөөс өмнө `git init` хийгээд эхний commit-ыг (`Starter`) хийсэн.

```bash
mkdir -p results
mvn clean verify 2>&1 | tee results/mvn-verify-start.txt
cp target/site/jacoco/jacoco.csv results/jacoco-start.csv

mvn test-compile org.pitest:pitest-maven:mutationCoverage 2>&1 | tee results/pit-start.txt
```

### JaCoCo — хамралт

![JaCoCo эхний тайлан](docs/jacoco-start.png)

`results/jacoco-start.csv`-ээс:

| Хэмжүүр | Хамрагдсан | Нийт | % |
|---|---|---|---|
| Line | 25 | 25 | **100%** |
| Branch | 38 | 41 | 92% (3 missed) |
| Instruction | 152 | 152 | 100% |
| Method | 4 | 4 | 100% |

→ **Line 100% (25/25), Branch 38/41** гэдэг нь батлагдлаа. 6 тест бүгд амжилттай (`Tests run: 6, Failures: 0`).

### PIT — mutation coverage

![PIT эхний тайлан](docs/pit-start.png)

`results/pit-start.txt`-ийн төгсгөлөөс:

```
>> Line Coverage (for mutated classes only): 25/25 (100%)
>> Generated 63 mutations Killed 17 (27%)
>> Mutations with no coverage 0. Test strength 27%
```

| Хэмжүүр | Утга |
|---|---|
| Line coverage | 25/25 (100%) |
| Үүссэн мутант | 63 |
| Устгасан (Killed) | **17** |
| Амьд үлдсэн (Survived) | 46 |
| Mutation score | **27%** |

### Эхний ажиглалт

Line coverage 100% боловч mutation score ердөө 27% (63-аас 17 мутант устсан) байна. Энэ нь тестүүд кодын мөр бүрийг *ажиллуулж* байгаа ч үр дүнг нь бараг *шалгахгүй* байгааг харуулж байна: `assertNotNull`, `0 ≤ x ≤ 100` гэх мэт сул assertion нь "B"-г "A" болгосон, `>=`-ийг `>` болгосон, буцаах утгыг 0 болгосон зэрэг өөрчлөлтийг ялгаж чаддаггүй тул 46 мутант амьд үлдсэн. Өөрөөр хэлбэл хамралт нь "аль код ажилласан"-ыг, mutation score нь "тест алдааг барьж чадах эсэх"-ийг хэмждэг тул хамралт өндөр байх нь тестийн чанар сайн гэсэн үг биш.

## Хэсэг A: Амьд мутантуудын ангилал

Starter багц дээр **46 мутант амьд үлдсэн** (63 − 17). Мутант бүрийн мөр, төрөл, төлөвийн жагсаалтыг
`results/pit-start-mutations.csv`-д хадгалсан. Үүнийг `mvn test-compile org.pitest:pitest-maven:mutationCoverage -DoutputFormats=HTML,CSV`
командаар starter тестүүд дээр гаргасан. Хүснэгтийн "Мөр" нь `ScholarshipCalculator.java`-ийн мөрийн дугаар.

**Ангиллын үндэслэл (нэг өгүүлбэр):**
- **Тест дутуу — зааг:** `changed conditional boundary` мутант нь яг зааг утга дээр (90, 3.8, 15 г.м.) өөр үр дүн буцаадаг тул бодит зан төлөвийг өөрчилдөг. Starter багцад зааг утгын тест байхгүй тул энэ нь тест дутуу.
- **Тест дутуу — assertion сул:** `removed conditional`, `replaced return value`, `Replaced double addition`, `Changed switch default` мутантууд спецификацид заасан буцаах утгыг өөрчилдөг тул бодит зан төлөвийг өөрчилдөг. Тест нь зөвхөн `assertNotNull` / `0 ≤ x ≤ 100` шалгадаг тул тэдгээрийг барьж чадахгүй, иймээс тест дутуу.
- **Equivalent:** байхгүй (0). 46 мутант бүрийн хувьд оригинал кодоос өөр гаралт өгөх тодорхой оролт байгаа ("Арга хэмжээ" баганад заасан), тиймээс зан төлөв нь ижил биш.

| Мөр | Мутаци (PIT-ийн тайлбар) | Тоо | Ангилал | Арга хэмжээ |
|---|---|---|---|---|
| 20 | changed conditional boundary (`score < 0` → `<= 0`, `score > 100` → `>= 100`) | 2 | Тест дутуу (зааг) | B2: `letterGrade(0)=="F"`, `letterGrade(100)=="A"` (мутант exception шиднэ) |
| 23 | changed conditional boundary (`>= 90` → `> 90`) | 1 | Тест дутуу (зааг) | B2: `letterGrade(90)=="A"`, `letterGrade(89.99)=="B"` |
| 23 | removed conditional – replaced comparison check with true / false; replaced return value with "" | 3 | Тест дутуу (assertion сул) | B1: `assertNotNull`-ийг `assertEquals("A", letterGrade(95))` болгоно (true үед 85 → "A", false үед 95 → "B" гарна) |
| 24 | changed conditional boundary (`>= 80` → `> 80`) | 1 | Тест дутуу (зааг) | B2: `letterGrade(80)=="B"`, `letterGrade(79.99)=="C"` |
| 24 | removed conditional – true / false; replaced return value with "" | 3 | Тест дутуу (assertion сул) | B1: `assertEquals("B", letterGrade(85))` |
| 25 | changed conditional boundary (`>= 70` → `> 70`) | 1 | Тест дутуу (зааг) | B2: `letterGrade(70)=="C"`, `letterGrade(69.99)=="D"` |
| 25 | removed conditional – true / false; replaced return value with "" | 3 | Тест дутуу (assertion сул) | B1: `assertEquals("C", letterGrade(75))` |
| 26 | changed conditional boundary (`>= 60` → `> 60`) | 1 | Тест дутуу (зааг) | B2: `letterGrade(60)=="D"`, `letterGrade(59.99)=="F"` |
| 26 | removed conditional – true / false; replaced return value with "" | 3 | Тест дутуу (assertion сул) | B1: `assertEquals("D", letterGrade(65))`, `assertEquals("F", letterGrade(30))` |
| 27 | replaced return value with "" | 1 | Тест дутуу (assertion сул) | B1: `assertEquals("F", letterGrade(30))`. `gpa()` дотор `""` нь `default` салаагаар `"F"`-тэй адил 0 оноо өгдөг тул зөвхөн `letterGrade`-ийг шууд шалгаж устгана |
| 36 | Changed switch default to be first case | 1 | Тест дутуу (assertion сул) | B1/B2: үсэг бүрийн голчийг яг шалгана, `gpa({95})==4.0`, `gpa({40})==0.0` (мутант A ба default-ийн оноог сольдог) |
| 38 | Replaced double addition with subtraction (`total += 3` → `-= 3`) | 1 | Тест дутуу (assertion сул) | B1: `assertEquals(3.5, gpa({95,85}))` (мутант 0.5 гаргана, мужид багтдаг тул одоогийн тест барихгүй) |
| 40 | Replaced double addition with subtraction (`total += 1` → `-= 1`) | 1 | Тест дутуу (assertion сул) | B1: `assertEquals(1.5, gpa({75,65}))` (мутант 0.5) |
| 44 | replaced double return with 0.0d | 1 | Тест дутуу (assertion сул) | B1: голчийг яг утгаар нь `assertEquals(..., 1e-9)` |
| 48 | changed conditional boundary (`gpa < 0` → `<= 0`, `credits < 0` → `<= 0`) | 2 | Тест дутуу (зааг) | B2: `scholarship(0.0, 20, false)==0`, `scholarship(3.0, 0, false)==50` (мутант exception шиднэ) |
| 48 | removed conditional – replaced comparison check with true (`gpa < 0` шалгалт арилна) | 1 | Тест дутуу | B2: `scholarship(-0.01, 10, false)` exception шидэх ёстой (сөрөг голчийн тест байхгүй) |
| 51 | removed conditional – replaced equality check with true / false (`hasViolation`) | 2 | Тест дутуу (assertion сул) | B1: `assertEquals(0, scholarship(4.0, 20, true))` ба `assertEquals(100, scholarship(3.9, 16, false))` |
| 52 | replaced int return with 0; removed conditional – true / false (×2, `gpa` ба `credits`) | 5 | Тест дутуу (assertion сул) | B1: `assertEquals(100, scholarship(3.9, 16, false))` + `scholarship(3.5, 16)==70`, `scholarship(3.9, 13)==70` |
| 52 | changed conditional boundary (`gpa >= 3.8` → `> 3.8`, `credits >= 15` → `> 15`) | 2 | Тест дутуу (зааг) | B2: `scholarship(3.8, 15, false)==100`, `scholarship(3.79, 15)==70`, `scholarship(3.8, 14)==70` |
| 53 | replaced int return with 0; removed conditional – true / false (×2, `gpa` ба `credits`) | 5 | Тест дутуу (assertion сул) | B1: `assertEquals(70, scholarship(3.5, 13, false))` + `scholarship(3.1, 13)==50`, `scholarship(3.5, 5)==50` |
| 53 | changed conditional boundary (`gpa >= 3.4` → `> 3.4`, `credits >= 12` → `> 12`) | 2 | Тест дутуу (зааг) | B2: `scholarship(3.4, 12, false)==70`, `scholarship(3.39, 12)==50`, `scholarship(3.4, 11)==50` |
| 54 | replaced int return with 0; removed conditional – true / false | 3 | Тест дутуу (assertion сул) | B1: `assertEquals(50, scholarship(3.1, 5, false))`, `assertEquals(0, scholarship(2.0, 1, false))` |
| 54 | changed conditional boundary (`gpa >= 3.0` → `> 3.0`) | 1 | Тест дутуу (зааг) | B2: `scholarship(3.0, 0, false)==50`, `scholarship(2.99, 20)==0` |
| | **Нийт** | **46** | Тест дутуу: 46 (зааг 13, assertion сул/бусад 33); Equivalent: 0 | |

**Ажиглалт:** Устсан 17 мутант нь ихэвчлэн exception шиддэг замуудад (мөр 20, 31, 48), эсвэл үр дүнг мужаас гаргадаг өөрчлөлтүүдэд (мөр 37, 39: `+= 4`/`+= 2` → `-=` нь сөрөг голч өгдөг) байсан. Харин мужийн дотор үлддэг аливаа буруу утгыг (жишээ нь "B"-ийн оронд "A", 70-ийн оронд 100) сул assertion-ууд огт барьдаггүй.
