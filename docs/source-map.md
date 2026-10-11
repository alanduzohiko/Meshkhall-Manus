# خريطة مصادر مەشخەڵ الموحد

| المسار | المجموعة | الاستخدام |
|---|---|---|
| `app/src/main/assets/quran.db` | مەشخەڵ | القرآن والترجمات والصوتيات وروابطها |
| `app/src/main/assets/tafseeri_*.db` | مەشخەڵ | تفاسير عربية/كردية متعددة |
| `app/src/main/assets/db_hadith.db` | مەشخەڵ | الأحاديث والكتب والأبواب |
| `app/src/main/assets/prayertimes.db` | مەشخەڵ | المدن والأذكار والتسبيح والصلاة |
| `app/src/main/assets/khira/mushaf/ayahs.bin` | خيرة | مصحف نصي حقيقي 6236 آية |
| `app/src/main/assets/khira/mushaf/pages.bin` | خيرة | صفحات مصحف 604 صفحة |
| `app/src/main/assets/khira/tafsir/*.sqlite` | خيرة | ابن كثير والميسر والسعدي والطبري |
| `app/src/main/assets/khira/data/*.json` | خيرة | السور والأذكار والأدعية والودجت |
| `analysis/rea-meshkhal` | التحليل الأول | أدلة REA للمشروع الأول |
| `analysis/rea-khira` | التحليل الثاني | أدلة REA لخيرة |

القارئ الموحد: `MeshkhalRepository`، ويستخدم `AssetSqliteContentSource` و`KhiraContentSource`.
