package com.example.data.model

import java.io.Serializable

data class Verse(
    val number: Int,
    val textEn: String,
    val textHi: String,
    val textNe: String,
    val translationEn: String,
    val translationHi: String,
    val translationNe: String
) : Serializable

data class Chapter(
    val number: Int,
    val titleEn: String,
    val titleHi: String,
    val titleNe: String,
    val descriptionEn: String = "",
    val verses: List<Verse>
) : Serializable

data class Scripture(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val titleNe: String,
    val descriptionEn: String,
    val descriptionHi: String,
    val descriptionNe: String,
    val category: String, // Epic, Vedas, Upanishads, Purana, Hymn, Stories
    val chapters: List<Chapter>
) : Serializable

data class WisdomQuote(
    val textEn: String,
    val textHi: String,
    val textNe: String,
    val sourceEn: String,
    val sourceHi: String,
    val sourceNe: String,
    val explanationEn: String = ""
)

data class BuddhaThought(
    val quoteEn: String,
    val quoteHi: String,
    val quoteNe: String,
    val applicationEn: String,
    val applicationHi: String,
    val applicationNe: String
)

data class MoralStory(
    val titleEn: String,
    val titleHi: String,
    val titleNe: String,
    val contentEn: String,
    val contentHi: String,
    val contentNe: String,
    val moralEn: String,
    val moralHi: String,
    val moralNe: String
)

object StaticScriptureProvider {

    val scriptures = listOf(
        Scripture(
            id = "gita",
            titleEn = "Bhagavad Gita",
            titleHi = "भगवद गीता",
            titleNe = "भगवद गीता",
            descriptionEn = "The timeless philosophical dialogue between Lord Krishna and Arjuna on duty and righteousness.",
            descriptionHi = "कर्तव्य और धार्मिकता पर भगवान कृष्ण और अर्जुन के बीच कालातीत दार्शनिक संवाद।",
            descriptionNe = "कर्तव्य र धर्मको विषयमा भगवान कृष्ण र अर्जुन बीचको कालजयी दार्शनिक संवाद।",
            category = "Epic / Smriti",
            chapters = listOf(
                Chapter(
                    number = 1,
                    titleEn = "Yoga of Dejection of Arjuna",
                    titleHi = "अर्जुनविषादयोग",
                    titleNe = "अर्जुनविषादयोग",
                    descriptionEn = "Arjuna sees his relatives arrayed for battle and sinks into despair.",
                    verses = listOf(
                        Verse(
                            number = 1,
                            textEn = "Dharmakshetre Kurukshetre samaveta yuyutsavah\nMamakah pandavaschaiva kim akurvata Sanjaya",
                            textHi = "धर्मक्षेत्रे कुरुक्षेत्रे समवेता युयुत्सवः।\nमामकाः पाण्डवाश्चैव किमकुर्वत सञ्जय॥",
                            textNe = "धर्मक्षेत्रे कुरुक्षेत्रे समवेता युयुत्सवः।\nमामकाः पाण्डवाश्चैव किमकुर्वत सञ्जय॥",
                            translationEn = "Sanjaya, gathered on the holy land of Kurukshetra, desirous of fighting, what did my sons and the sons of Pandu do?",
                            translationHi = "धृतराष्ट्र ने पूछा: हे संजय! धर्मभूमि कुरुक्षेत्र में युद्ध की इच्छा से एकत्र हुए मेरे और पाण्डु के पुत्रों ने क्या किया?",
                            translationNe = "धृतराष्ट्रले सोधे: हे संजय! धर्मभूमि कुरुक्षेत्रमा युद्धको इच्छाले जम्मा भएका मेरा र पाण्डुका छोराहरूले के गरे?"
                        )
                    )
                ),
                Chapter(
                    number = 2,
                    titleEn = "Yoga of Knowledge (Sankhya)",
                    titleHi = "सांख्ययोग",
                    titleNe = "सांख्ययोग",
                    descriptionEn = "Krishna teaches about the deathless soul, selfless action (Karma Yoga), and stable intellect.",
                    verses = listOf(
                        Verse(
                            number = 20,
                            textEn = "Na jayate mriyate va kadachin nayam bhutva bhavita va na bhuyah\nAjo nityah shashvato 'yam purano na hanyate hanyamane sharire",
                            textHi = "न जायते म्रियते वा कदाचिन् नायं भूत्वा भविता वा न भूयः।\nअजो नित्यः शाश्वतोऽयं पुराणो न हन्यते हन्यमाने शरीरे॥",
                            textNe = "न जायते म्रियते वा कदाचिन् नायं भूत्वा भविता वा न भूयः।\nअजो नित्यः शाश्वतोऽयं पुराणो न हन्यते हन्यमाने शरीरे॥",
                            translationEn = "The soul is never born nor dies; nor does it exist and then cease to exist. It is unborn, eternal, everlasting, ancient; it is not killed when the body is killed.",
                            translationHi = "यह आत्मा किसी काल में न तो जन्मता है और न मरता है; और न यह उत्पन्न होकर फिर होने वाला ही है। यह अजन्मा, नित्य, सनातन और पुरातन है, शरीर के मारे जाने पर भी नहीं मारा जाता।",
                            translationNe = "यो आत्मा कुनै कालमा पनि जन्मँदैन र मर्दैन; र न यो उत्पन्न भएर फेरि समाप्त हुन्छ। यो अजन्मा, नित्य, सनातन र पुरानो छ, शरीर मारिएतापनि यो मारिँदैन।"
                        ),
                        Verse(
                            number = 47,
                            textEn = "Karmany evadhikaras te ma phaleshu kadachana\nMa karma-phala-hetur bhur ma te sango 'stv akarmani",
                            textHi = "कर्मण्येवाधिकारस्ते मा फलेषु कदाचन।\nमा कर्मफलहेतुर्भूर्मा ते सङ्गोऽस्त्वकर्मणि॥",
                            textNe = "कर्मण्येवाधिकारस्ते मा फलेषु कदाचन।\nमा कर्मफलहेतुर्भूर्मा ते सङ्गोऽस्त्वकर्मणि॥",
                            translationEn = "You have a right to perform your prescribed duties, but you are not entitled to the fruits of your actions. Never consider yourself to be the cause of the results, and never be attached to inaction.",
                            translationHi = "तुम्हारा अधिकार केवल कर्म करने में ही है, उसके फलों में कभी नहीं। अत: तुम कर्म फलों के हेतु मत बनो और न ही अकर्मण्यता में तुम्हारी आसक्ति हो।",
                            translationNe = "तिम्रो अधिकार केवल कर्म गर्नमा मात्र छ, त्यसको फलमा कहिल्यै पनि छैन। त्यसैले तिमी कर्म फलको कारण नबन र अकर्मण्यतामा पनि तिम्रो आशक्ति नहोस्।"
                        )
                    )
                ),
                Chapter(
                    number = 12,
                    titleEn = "Yoga of Devotion (Bhakti)",
                    titleHi = "भक्तियोग",
                    titleNe = "भक्तियोग",
                    descriptionEn = "Krishna outlines the characteristics of a dear devotee, emphasizing devotion and peace.",
                    verses = listOf(
                        Verse(
                            number = 13,
                            textEn = "Adveshta sarva-bhutanam maitrah karuna eva cha\nNirmamo nirahankarah sama-duhkha-sukhah kshami",
                            textHi = "अद्वेष्टा सर्वभूतानां मैत्रः करुण एव च।\nनिर्ममो निरहङ्कारः समदुःखसुखः क्षमी॥",
                            textNe = "अद्वेष्टा सर्वभूतानां मैत्रः करुण एव च।\nनिर्ममो निरहङ्कारः समदुःखसुखः क्षमी॥",
                            translationEn = "One who is free from malice toward any living entity, friendly and compassionate, free from attachment and ego, equal in distress and happiness, and forgiving.",
                            translationHi = "जो सभी जीवित प्राणियों से द्वेष न रखने वाला, मित्रवत् और दयालु है, ममता और अहंकार से रहित, सुख-दुःख में समान और क्षमाशील है, वह मुझे प्रिय है।",
                            translationNe = "जो सबै जीवित प्राणीहरूसँग द्वेष नराख्ने, मैत्रीपूर्ण र कृपालु छ, ममता र अहंकारबाट मुक्त, सुख-दुःखमा समान र क्षमाशील छ, त्यो मलाई प्रिय छ।"
                        )
                    )
                )
            )
        ),
        Scripture(
            id = "hanuman_chalisa",
            titleEn = "Hanuman Chalisa",
            titleHi = "हनुमान चालीसा",
            titleNe = "हनुमान चालीसा",
            descriptionEn = "The iconic forty-verse hymn composed by Goswami Tulsidas in praise of Lord Hanuman, the epitomie of devotion.",
            descriptionHi = "गोस्वामी तुलसीदास द्वारा रचित भगवान हनुमान की स्तुति में चालीस चौपाइयों का ऐतिहासिक पाठ।",
            descriptionNe = "गोस्वामी तुलसीदासद्वारा रचित भगवान हनुमानको स्तुतिमा चालीस चौपाइको ऐतिहासिक पाठ।",
            category = "Devotional Hymn",
            chapters = listOf(
                Chapter(
                    number = 1,
                    titleEn = "Opening Doha & Verses",
                    titleHi = "प्रारंभिक दोहा एवं चौपाइयाँ",
                    titleNe = "प्रारम्भिक दोहा र चौपाइहरू",
                    verses = listOf(
                        Verse(
                            number = 1,
                            textEn = "Shree Guru Charan Saroj Raj, Nij Man Mukut Sudhaar\nBarnau Raghuvar Bimal Jasu, Jo Dayaku Phal Chaar",
                            textHi = "श्रीगुरु चरन सरोज रज निज मनु मुकुरु सुधारि।\nबरनऊँ रघुबर बिमल जसु जो दायकु फल चारि॥",
                            textNe = "श्रीगुरु चरन सरोज रज निज मनु मुकुरु सुधारि।\nबरनऊँ रघुबर बिमल जसु जो दायकु फल चारि॥",
                            translationEn = "Cleansing the mirror of my mind with the dust from the lotus feet of the Divine Guru, I sing the pure glories of Lord Ram, which bestow the four-fold fruits of life.",
                            translationHi = "अपने गुरु के चरण कमलों की धूल से अपने मन के दर्पण को शुद्ध करके, मैं भगवान राम के निर्मल यश का वर्णन करता हूँ, जो चारों पुरुषार्थ (धर्म, अर्थ, काम, मोक्ष) प्रदान करता है।",
                            translationNe = "आफ्नो गुरुको चरण कमलको धुलोले आफ्नो मनको ऐनालाई शुद्ध पारेर, म भगवान रामको निर्मल यश वर्णन गर्दछु, जसले चारवटै पुरुषार्थ (धर्म, अर्थ, काम, मोक्ष) प्रदान गर्दछ।"
                        ),
                        Verse(
                            number = 2,
                            textEn = "Buddhiheen Tanu Janike, Sumirau Pawan Kumar\nBal Buddhi Vidya Dehu Mohi, Harahu Kalesh Bikaar",
                            textHi = "बुद्धिहीन तनु जानिके सुमिरौ पवन कुमार।\nबल बुधि बिद्या देहु मोहि हरहु कलेस बिकार॥",
                            textNe = "बुद्धिहीन तनु जानिके सुमिरौ पवन कुमार।\nबल बुधि बिद्या देहु मोहि हरहु कलेस बिकार॥",
                            translationEn = "Knowing myself to be devoid of wisdom, I invoke you, O Son of Wind! Grant me strength, wisdom, and knowledge, and remove my miseries and flaws.",
                            translationHi = "स्वयं को बुद्धिहीन जानकर, हे पवनपुत्र में आपका स्मरण करता हूँ। मुझे बल, बुद्धि और विद्या प्रदान करें और मेरे समस्त दुखों और विकारों को दूर करें।",
                            translationNe = "आफूलाई बुद्धिहीन सम्झेर, पवनपुत्र म हजुरको स्मरण गर्दछु। मलाई बल, बुद्धि र विद्या प्रदान गर्नुहोस् र मेरा समस्या र विकारहरू हटाउनुहोस्।"
                        ),
                        Verse(
                            number = 3,
                            textEn = "Jai Hanuman Gyan Guna Sagar\nJai Kapis Tihun Lok Ujagar",
                            textHi = "जय हनुमान ज्ञान गुन सागर।\nजय कपीस तिहुँ लोक उजागर॥",
                            textNe = "जय हनुमान ज्ञान गुन सागर।\nजय कपीस तिहुँ लोक उजागर॥",
                            translationEn = "Victory to Hanuman, ocean of wisdom and virtues! Victory to the chief of monkeys, who illuminates the three worlds!",
                            translationHi = "ज्ञान और गुणों के सागर श्री हनुमान जी की जय हो! तीनों लोकों में कीर्ति फैलाने वाले कपिराज की जय हो!",
                            translationNe = "ज्ञान र गुणका सागर श्री हनुमान जीको जय होस्! तीनै लोकमा कीर्ति फैलाउने कपिराजको जय होस्!"
                        )
                    )
                )
            )
        ),
        Scripture(
            id = "vedas_upanishads",
            titleEn = "Vedic Hymns & Upanishads",
            titleHi = "वैदिक सूक्त एवं उपनिषद",
            titleNe = "वैदिक सूक्त र उपनिषद",
            descriptionEn = "Core collection of eternal declarations of unity and spiritual insight from the Vedas and Upanishads.",
            descriptionHi = "वेदों और उपनिषदों से एकता और आध्यात्मिक अंतर्दृष्टि की शाश्वत घोषणाओं का मुख्य संग्रह।",
            descriptionNe = "वेद र उपनिषदबाट एकता र आध्यात्मिक अन्तर्दृष्टिका शाश्वत घोषणाहरूको मुख्य संग्रह।",
            category = "Vedas",
            chapters = listOf(
                Chapter(
                    number = 1,
                    titleEn = "Gayatri & Unity Mantras",
                    titleHi = "गायत्री एवं एकता मंत्र",
                    titleNe = "गायत्री र एकता मन्त्र",
                    verses = listOf(
                        Verse(
                            number = 1,
                            textEn = "Om Bhur Bhuvah Svah Tat Savitur Varenyam\nBhargo Devasya Dheemahi Dhiyo Yo Nah Prachodayat",
                            textHi = "ॐ भूर्भुवः स्वः तत्सवितुर्वरेण्यं।\nभर्गो देवस्य धीमहि धियो यो नः प्रचोदयात्॥",
                            textNe = "ॐ भूर्भुवः स्वः तत्सवितुर्वरेण्यं।\nभर्गो देवस्य धीमहि धियो यो नः प्रचोदयात्॥",
                            translationEn = "We meditate on the supreme splendor of the Divine Sun, who illuminates everything. May He inspire and direct our intellect toward the path of righteousness.",
                            translationHi = "हम उस दिव्य, सर्वोत्तम सूर्य देव के तेज का ध्यान करते हैं जो सब कुछ प्रकाशित करते हैं। वे हमारी बुद्धि को सत्य मार्ग पर चलने के लिए प्रेरित करें।",
                            translationNe = "हामी त्यस दिव्य, सर्वोत्तम सूर्य देवको तेजको ध्यान गर्दछौं जसले सबैलाई उज्यालो दिन्छ। उनले हाम्रो बुद्धिलाई सत् मार्गमा हिँड्न प्रेरणा दिउन्।"
                        ),
                        Verse(
                            number = 2,
                            textEn = "Sangachhadhwam samvadadhwam sam vo manamsi janatam\nDeva bhagam yatha purve samjanana upasate",
                            textHi = "सङ्गच्छध्वं संवदध्वं सं वो मनांसि जानताम्।\nदेवा भागं यथा पूर्वे संजानाना उपासते॥",
                            textNe = "सङ्गच्छध्वं संवदध्वं सं वो मनांसि जानताम्।\nदेवा भागं यथा पूर्वे संजानाना उपासते॥",
                            translationEn = "Walk together, speak together, and match your minds in harmony, just as the ancient divine forces performed their sacred duties in perfect accord.",
                            translationHi = "साथ चलें, साथ बोलें और अपने मन को मिलकर एक होने दें, जैसे प्राचीन काल में दिव्य सिद्ध पुरुषों ने अपने कर्तव्यों का पालन किया था।",
                            translationNe = "सँगै हिडौं, सँगै बोलौं र आफ्नो मनलाई मिलेर एक हुन दिऔं, जसरी प्राचीन कालमा दिव्य सिद्ध महापुरुषहरूले आफ्नो कर्तव्य पालन गरेका थिए।"
                        )
                    )
                )
            )
        )
    )

    val dailyWisdomQuotes = listOf(
        WisdomQuote(
            textEn = "A person is made by their belief. As they believe, so they are.",
            textHi = "मनुष्य अपने विश्वास से निर्मित होता है। जैसा वह विश्वास करता है, वैसा ही वह बन जाता है।",
            textNe = "मानिस आफ्नो विश्वासले बन्दछ। जस्तो उसले विश्वास गर्दछ, उस्तै उ बन्दछ।",
            sourceEn = "Bhagavad Gita 17.3",
            sourceHi = "भगवद गीता १७.३",
            sourceNe = "भगवद गीता १७.३"
        ),
        WisdomQuote(
            textEn = "Peace starts when expectation ends.",
            textHi = "शांति तब शुरू होती है जब अपेक्षाएं समाप्त होती हैं।",
            textNe = "अपेक्षा र चाहना समाप्त हुँदा नै शान्ति सुरु हुन्छ।",
            sourceEn = "Upanishads",
            sourceHi = "उपनिषद",
            sourceNe = "उपनिषद"
        ),
        WisdomQuote(
            textEn = "Dharma (Righteousness) protects those who protect it.",
            textHi = "धर्मो रक्षति रक्षितः - जो धर्म की रक्षा करते हैं, धर्म उनकी रक्षा करता है।",
            textNe = "धर्मो रक्षति रक्षितः - जसले धर्मको रक्षा गर्दछ, धर्मले उसको रक्षा गर्दछ।",
            sourceEn = "Manusmriti",
            sourceHi = "मनुस्मृति",
            sourceNe = "मनुस्मृति"
        )
    )

    val buddhaThoughts = listOf(
        BuddhaThought(
            quoteEn = "The mind is everything. What you think you become.",
            quoteHi = "मन ही सब कुछ है। तुम जैसा सोचते हो, वैसे ही बन जाते हो।",
            quoteNe = "मन नै सबै कुरा हो। तिमी जस्तो सोच्दछौ, त्यस्तै बन्दछौ।",
            applicationEn = "Avoid dwelling on negative, self-defeating thoughts. Nurture peace, discipline, and success.",
            applicationHi = "नकारात्मक विचारों से बचें। मन में शांति, अनुशासन और सकारात्मकता का संचार करें।",
            applicationNe = "नकारात्मक विचारहरूबाट टाढा रहनुहोस्। मनमा शान्ति र अनुशासनको विकास गर्नुहोस्।"
        ),
        BuddhaThought(
            quoteEn = "Peace comes from within. Do not seek it without.",
            quoteHi = "शांति भीतर से आती है। इसे बाहर मत ढूंढो।",
            quoteNe = "शान्ति भित्रबाट आउँछ। यसलाई बाहिर नखोज।",
            applicationEn = "Instead of relying on material achievements to be happy, practice mindfulness and content stability.",
            applicationHi = "खुशी के लिए बाहरी सुखों पर निर्भर रहने के बजाय एकाग्रता और संतोष का अभ्यास करें।",
            applicationNe = "खुशीको लागि बाहिरी भौतिक सुखमा निर्भर हुनुको सट्टा संतोष र ध्यानको अभ्यास गरौँ।"
        )
    )

    val moralStories = listOf(
        MoralStory(
            titleEn = "The River of Sand",
            titleHi = "रेत की नदी",
            titleNe = "बालुवाको नदी",
            contentEn = "A weary traveler complained to a sage about a river full of dry, blistering sand that blocked his path. The sage smiled and told him, 'The sand is only a river when you try to fight it. Walk over it as dry terrain, and it becomes your pathway.'",
            contentHi = "एक थके हुए यात्री ने साधु से शिकायत की कि रास्ते में रेत की उफनती नदी है जो रास्ता रोक रही है। साधु मुस्कुराए और बोले, 'रेत केवल नदी है जब तुम उससे लड़ने की कोशिश करते हो। उसे सुखी जमीन समझकर उसपर चलो, और वह तुम्हारा रास्ता बन जाएगी।'",
            contentNe = "एक थकित यात्रीले साधुसँग गुनासो गरे कि बाटोमा बालुवाले भरिएको तातो नदी छ जसले अघि बढ्न दिएन। साधुले मुस्कुराउँदै भने, 'तिमीले बालुवासँग लड्न खोज्दा मात्र त्यो नदी जस्तो लाग्छ। यसलाई सुख्खा जमिन सम्झेर हिड र यो नै तिम्रो मार्ग बन्नेछ।' ",
            moralEn = "Reframing challenges as pathways removes the illusion of obstacles.",
            moralHi = "चुनौतियों को मार्ग के रूप में देखने से बाधाओं का भ्रम दूर हो जाता है।",
            moralNe = "चुनौतीहरूलाई अवसरका रूपमा लिँदा समस्याहरू निर्मूल हुन्छन्।"
        )
    )
}
