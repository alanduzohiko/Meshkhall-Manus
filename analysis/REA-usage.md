# تحليل التطبيق الأصلي باستخدام REA

ملف APK المرجعي حلله REA/JADX بشكل ثابت. الدليل المحفوظ في `analysis/rea/` يصف الحزمة والصفوف والموارد والبيان.

لإعادة تشغيل التحليل خارج هذا المشروع:

```bash
export REA_JADX_MCP_JAR=/absolute/path/jadx-headless-mcp-0.7.1-all.jar
npx -y rea-agents@latest inspect-android-package /path/to/tafseerinoor.apk --format json --full-output
npx -y rea-agents@latest search-android-classes /path/to/tafseerinoor.apk com.dev.hazhanjalal.tafseerinoor.ui --format json --full-output
```

التحليل الثابت لا يشغّل التطبيق. أصول المحتوى الفعلية التي نُقلت إلى `app/src/main/assets/` هي ما يستخدمه `AssetSqliteContentSource` في النسخة الجديدة.
