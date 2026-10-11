# عقد البيانات الموحد

## مجموعة مەشخەڵ

توجد في `app/src/main/assets`: `quran.db`, `db_hadith.db`, `prayertimes.db`, قواعد `tafseeri_*.db`, قاعدة الكلمات والمعجم، الترجمة الكردية وملفات PDF والخطوط.

## مجموعة خيرة

توجد في `app/src/main/assets/khira`: `mushaf/ayahs.bin`, `mushaf/pages.bin`, `mushaf/tafsir.bin`, `data/athkar.json`, `data/duas.json`, `data/surahs.json`, `tafsir/catalog.json`, وقواعد SQLite الأربعة.

`ayahs.bin` و`tafsir.bin` عبارة عن JSON UTF-8 مضغوط zlib. بيانات المستخدم لا تُحفظ داخل assets؛ يجب وضعها في Room/DataStore بالجداول `bookmark`, `note`, `reading_progress`, `khatmah`, `athkar_progress`, `app_preferences`, `prayer_settings`, `downloaded_audio`, `backup_metadata`.
