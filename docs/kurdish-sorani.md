# دعم الكردية السورانية في مەشخەڵ

## الموجود فعلًا

قاعدة `app/src/main/assets/quran.db` تحتوي على أعمدة ترجمة كردية منها `translation_puxta` و`translation_raman` و`translation_asan`. توجد أيضًا قواعد تفسير كردية مثل `tafseer_rebar.db` و`tafseeri_hawrami.db` و`tafseeri_hazhar.db`، وقواعد بادينية مثل `tafseeri_runahi_badini.db` و`tafseeri_zhian_badini.db`. توجد قاعدة `kurdish_transliteration.db` وملف PDF، وخطوط `nrt_regular.ttf`, `nrt_bold.ttf`, `sarchia_abc.ttf`, و`sarchia_muslim.ttf`.

## المطلوب من AI

عند اختيار لغة التطبيق `ckb` يجب عرض واجهة RTL بالكردية السورانية، واستخدام `translation_puxta` كخيار افتراضي للترجمة السورانية، وإتاحة `translation_raman` و`translation_asan` كمصادر بديلة. لا تعرض الترجمة العربية بدل السورانية بصمت.

في قائمة التفاسير، ميّز اللغة بوضوح: سوراني، عربي، باديني، أو إنجليزي. لا تسمِّ تفسيرًا بادينيًا «سورانيًا». اعرض اسم المصدر والمؤلف والإصدار كما هو في كتالوج المصدر.

استخدم خط NRT أو Sarchia للنص الكردي إذا اختاره المستخدم، وتحقق من الحروف الخاصة مثل پ، چ، ژ، ڵ، ڕ، ۆ، ێ، وە. كل النصوص الجديدة يجب أن تكون في `values-ckb/strings.xml`، ولا تُكتب داخل Composable كسلاسل ثابتة.

## الاختبار

اختبر تغيير اللغة من إعدادات Android، اتجاه RTL، البحث بالحروف الكردية، عرض الآية والترجمة والتفسير السوراني، الخطوط، المشاركة، والوضع الليلي. يجب أن يعمل المحتوى السوراني Offline.
