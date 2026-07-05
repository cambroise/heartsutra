package cloud.ambroise.heartsutra.data

/**
 * The Heart Sutra (般若心経) as an ordered list of segments.
 *
 * Segment 2 is transcribed in its canonical form 観自在菩薩行深般若波羅蜜多時
 * (the source material dropped 若 and misspelled the reading as "than-nya");
 * every other segment reproduces the source text as supplied.
 */
object SutraData {

    val segments: List<Segment> = listOf(
        Segment(0, "摩訶般若波羅蜜多心経", "ma-ka-han-nya-ha-ra-mi-ta-shin-gyou", "The Great Heart of Wisdom Sutra"),
        Segment(1, "観自在菩薩行深般若波羅蜜多時", "kan-ji-zai-bo-satsu-gyou-jin-han-nya-ha-ra-mi-ta-ji", "Avalokiteshvara Bodhisattva while practice deep Prajna Paramita"),
        Segment(2, "照見五薀皆空度一切苦厄", "shou-ken-go-on-kai-kuu-do-is-sai-ku-yaku", "Perceived all five skandhas were empty and was saved from suffering and distress"),
        Segment(3, "舍利子色不異空", "sha-ri-shi-shiki-fu-i-kuu", "Shariputra, form is no different from emptiness"),
        Segment(4, "空不異色", "kuu-fu-i-shiki", "Emptiness is no different from form"),
        Segment(5, "色即是空", "shiki-soku-ze-kuu", "That which is form is emptiness"),
        Segment(6, "空即是色", "kuu-soku-ze-shiki", "That which is emptiness is form"),
        Segment(7, "受想行識亦復如是", "juu-sou-gyou-shiki-yaku-bu-nyo-ze", "Feelings, perceptions, impulses, consciousness, the same is true of these"),
        Segment(8, "舍利子是諸法空相", "sha-ri-shi-ze-sho-hou-kuu-sou", "Shariputra, all dharmas are marked with emptiness"),
        Segment(9, "不生不滅", "fu-shou-fu-metsu", "They do not appear or disappear"),
        Segment(10, "不垢不浄", "fu-ku-fu-jou", "are not tainted or pure"),
        Segment(11, "不増不減", "fu-zou-fu-gen", "do not increase or decrease"),
        Segment(12, "是故空中無色", "ze-ko-kuu-chuu-mu-shiki", "Therefore in emptiness no form,"),
        Segment(13, "無受想行識", "mu-juu-sou-gyou-shiki", "no feelings, perceptions, impulses, consciousness"),
        Segment(14, "無眼耳鼻舌身意", "mu-gen-ni-bi-ze-shin-i", "no eyes, ears, nose, tongue, body, mind"),
        Segment(15, "無色声香味触法", "mu-shiki-shou-kou-mi-soku-hou", "no color, sound, smell, taste, touch, object of mind"),
        Segment(16, "無限界乃至無意識界", "mu-gen-kai-nai-shi-mu-i-shiki-kai", "no realm of eyes and so forth until no realm of mind consciousness"),
        Segment(17, "無無明亦無無明尽", "mu-mu-myou-yaku-mu-mu-myou-jin", "no ignorance and also no extinction of ignorance"),
        Segment(18, "乃至無老死亦無老死尽", "nai-shi-mu-rou-shi-yaku-mu-rou-shi-jin", "and so forth until no old age and death and no extinction of old age and death"),
        Segment(19, "無苦集滅道", "mu-ku-shuu-metsu-dou", "no suffering, origination, stopping, path"),
        Segment(20, "無智亦無得", "mu-chi-yaku-mu-toku", "no cognition also no attainment"),
        Segment(21, "以無所得故", "i-mu-sho-to-ko", "with nothing to attain"),
        Segment(22, "菩提薩埵依般若波羅蜜多故", "bo-dai-sat-ta-e-han-nya-ha-ra-mi-ta-ko", "the Bodhisattva depends upon Prajna Paramita"),
        Segment(23, "心無罣礙", "shin-mu-ke-ge", "and his mind is no hindrance"),
        Segment(24, "無罣礙故無有恐怖", "mu-ke-ge-ko-mu-u-ku-fu", "without any hindrance no fear exists"),
        Segment(25, "遠離一切顛倒無想", "on-ri-is-sai-ten-dou-mu-sou", "far apart from every inverted view"),
        Segment(26, "究竟涅槃", "ku-kyou-ne-han", "he dwells in Nirvana"),
        Segment(27, "三世諸仏", "san-ze-shou-butsu", "All Buddhas in the Three Worlds"),
        Segment(28, "依般若波羅蜜多故", "e-han-nya-ha-ra-mi-ta-ko", "depend on Prajna Paramita"),
        Segment(29, "得阿耨多羅三藐三菩提", "toku-a-noku-ta-ra-san-myaku-san-bo-dai", "and attain complete unsurpassed enlightenment"),
        Segment(30, "故知般若波羅蜜多", "ko-chi-han-nya-ha-ra-mi-ta", "Therefore know the Prajna Paramita"),
        Segment(31, "是大神呪", "ze-dai-jin-shu", "is the great transcendent mantra"),
        Segment(32, "是大明呪", "ze-dai-myou-shu", "is the great bright mantra"),
        Segment(33, "是無上呪", "ze-mu-jou-shu", "is the utmost mantra"),
        Segment(34, "是無等等呪", "ze-mu-tou-dou-shu", "is the supreme mantra"),
        Segment(35, "能除一切苦真実不嘘", "nou-jo-is-sai-ku-shin-jitsu-fu-ko", "which is able to relieve all suffering and is true, not false"),
        Segment(36, "故説般若波羅蜜多呪", "ko-setsu-han-nya-ha-ra-mi-ta-shu", "so proclaim the Prajna Paramita mantra"),
        Segment(37, "即説呪曰", "soku-setsu-shu-watsu", "proclaim the mantra that says"),
        Segment(38, "揭諦揭諦波羅揭諦", "gya-te-gya-te-ha-ra-gya-te", "gone, gone, gone beyond"),
        Segment(39, "波羅僧揭諦菩提薩婆訶", "ha-ra-sou-gya-te-bo-ji-so-wa-ka", "gone all the way beyond, Bodhi Svaha!"),
        Segment(40, "般若心経", "han-nya-shin-gyou", "Heart Sutra"),
    )

    val size: Int get() = segments.size

    /**
     * The card for [centerIndex]: the segment itself plus up to two neighbours
     * on each side, clamped at the edges of the sutra.
     */
    fun windowAround(centerIndex: Int, radius: Int = 2): List<Segment> {
        val from = (centerIndex - radius).coerceAtLeast(0)
        val to = (centerIndex + radius).coerceAtMost(segments.lastIndex)
        return segments.subList(from, to + 1)
    }
}
