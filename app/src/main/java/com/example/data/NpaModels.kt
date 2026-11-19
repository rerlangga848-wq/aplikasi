package com.example.data

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.R
import com.example.ui.theme.*

data class QuoteItem(
    val text: String,
    val author: String
)

data class StatCardItem(
    val value: String,
    val label: String,
    val bg: Color,
    val icon: String
)

data class CompareModeData(
    val title: String,
    val subtitle: String,
    val items: List<String>,
    val chars: List<String>,
    val accent: Color,
    val chipBg: Color
)

data class TipItem(
    val icon: String,
    val title: String,
    val desc: String,
    val bg: Color
)

data class MythItem(
    val myth: String,
    val fact: String,
    val mythBg: Color,
    val factBg: Color
)

data class ScreenTimePoint(
    val year: String,
    val hours: Float,
    val displayLabel: String,
    val color: Color
)

data class ShockStatItem(
    val value: String,
    val label: String,
    val bg: Color
)

data class FakePostItem(
    val user: String,
    val content: String,
    val likes: String,
    val color: Color,
    val avatar: String
)

enum class NopiPose {
    IDLE, DROP, CROSSED, POINT, WIN, STICKER1, STICKER2
}

enum class CharExpression {
    RELIEVED, HAPPY, WARM, NEUTRAL, SLIGHTLY_HURT, DISAPPOINTED, SAD
}

data class CharacterVariant(
    val skin: Color,
    val hair: Color,
    val hairHighlight: Color,
    val eyeColor: Color,
    val shirt: Color
)

data class DoomResultTier(
    val minSec: Int,
    val maxSec: Int,
    val label: String,
    val desc: String,
    val nopiPose: NopiPose
)

data class ChallengeOption(
    val id: String,
    val label: String,
    val title: String,
    val desc: String,
    val bg: Color,
    val gradientColors: List<Color>,
    val nopiLine: String,
    val emoji: String,
    val tagline: String
)

data class StoryChoice(
    val label: String,
    val next: String
)

data class StoryNode(
    val key: String,
    val text: String,
    val choices: List<StoryChoice>
)

data class ConnectionChoice(
    val text: String,
    val present: Boolean,
    val reaction: String
)

data class ConnectionStoryStep(
    val narration: String,
    val choices: List<ConnectionChoice>
)

data class DetoxStage(
    val thresholdSec: Int,
    val emoji: String,
    val shortEmoji: String,
    val label: String,
    val desc: String
)

data class MomentItem(
    val icon: String,
    val title: String,
    val desc: String,
    val bg: Color,
    val quote: String
)

data class TeamMember(
    val name: String,
    val role: String,
    val bio: String,
    @DrawableRes val photoRes: Int?,
    val color: Color,
    val ig: String,
    val linkedin: String
)

data class MusicTrack(
    val title: String,
    val artist: String,
    val videoId: String,
    val color: Color,
    val baseFreqHz: Float
)

object NpaData {
    val quotes = listOf(
        QuoteItem("Hadir sepenuhnya adalah hadiah terbaik yang bisa kamu berikan ke orang di depanmu.", "— Bukan notifikasi, bukan feed."),
        QuoteItem("HP bisa ditaruh. Momen tidak bisa di-replay.", "— NPA Manifesto"),
        QuoteItem("Kamu tidak perlu dokumentasi untuk membuktikan bahwa kamu bahagia.", "— Gen Z yang lagi healing"),
        QuoteItem("Satu kontak mata lebih berharga dari seribu likes.", "— NPA Research 2024"),
        QuoteItem("Orang yang paling kamu sayangi ada di depanmu. Bukan di layar itu.", "— Reminder harian")
    )

    val heroCharVariants = listOf(
        CharacterVariant(Color(0xFFFFD8C2), Color(0xFF1A133B), Color(0xFF4D7CFF), Color(0xFF4D7CFF), Color(0xFF4D7CFF)),
        CharacterVariant(Color(0xFFFFDFCD), Color(0xFF3A1D4B), Color(0xFFFF5D8F), Color(0xFFFF5D8F), Color(0xFFFF5D8F)),
        CharacterVariant(Color(0xFFF6C6A8), Color(0xFF162C3B), Color(0xFF3DDC8A), Color(0xFF3DDC8A), Color(0xFF3DDC8A)),
        CharacterVariant(Color(0xFFFFDFD1), Color(0xFF442214), Color(0xFFFFB800), Color(0xFFFFB800), Color(0xFFFFB800)),
        CharacterVariant(Color(0xFFFCE2D6), Color(0xFF2B1B4A), Color(0xFFB8A6FF), Color(0xFF7C5CFF), Color(0xFFB8A6FF)),
        CharacterVariant(Color(0xFFFFD9EC), Color(0xFF120B2E), Color(0xFF22D3C7), Color(0xFF22D3C7), Color(0xFFFFB6D9))
    )

    val heroBeforeExpr = listOf(
        CharExpression.NEUTRAL,
        CharExpression.DISAPPOINTED,
        CharExpression.SLIGHTLY_HURT,
        CharExpression.NEUTRAL,
        CharExpression.SAD,
        CharExpression.DISAPPOINTED
    )

    val heroAfterExpr = listOf(
        CharExpression.HAPPY,
        CharExpression.RELIEVED,
        CharExpression.HAPPY,
        CharExpression.RELIEVED,
        CharExpression.HAPPY,
        CharExpression.RELIEVED
    )

    val stats = listOf(
        StatCardItem("4j 38m", "rata-rata orang Indonesia online lewat HP per hari (2025)", NpaYellow, "📱"),
        StatCardItem("414 M jam", "total jam orang Indonesia habis di aplikasi HP sepanjang 2025 — rank #2 dunia", NpaCoral, "🌏"),
        StatCardItem("88%", "anak muda 13–24 tahun mengaku doomscrolling", NpaMint, "🧠"),
        StatCardItem("27%", "waktu bersama pasangan yang habis sambil pegang HP", NpaBlue, "💔")
    )

    val compareScroll = CompareModeData(
        title = "1 Jam Scroll",
        subtitle = "yang kamu dapatkan:",
        items = listOf(
            "237 konten dikonsumsi",
            "0 percakapan bermakna",
            "Rasa lelah tanpa alasan jelas",
            "Perbandingan diri tanpa henti",
            "Doomscrolling tanpa sadar"
        ),
        chars = listOf("😶‍🌫️", "😑", "😮‍💨"),
        accent = NpaBlue,
        chipBg = NpaLavenderSoft
    )

    val compareNgobrol = CompareModeData(
        title = "1 Jam Ngobrol",
        subtitle = "yang kamu dapatkan:",
        items = listOf(
            "Tawa yang meledak bareng",
            "Pemahaman yang lebih dalam",
            "Rasa didengar & dimengerti",
            "Kenangan yang bertahan",
            "Koneksi yang nyata"
        ),
        chars = listOf("😄", "🤝", "✨"),
        accent = NpaMint,
        chipBg = NpaMint
    )

    val brainTabs = listOf("Reels", "Chat", "Notif", "Feed", "Story", "Shop")

    val momentStages = listOf(
        0 to "Semua pegang HP",
        40 to "Mulai taruh HP",
        70 to "Lebih fokus",
        88 to "Hadir sepenuhnya ✨"
    )

    val tips = listOf(
        TipItem("🍽️", "No Phone Meal", "HP masuk saku atau tas saat makan. Bukan silent, bukan face-down. Betul-betul tidak ada.", NpaYellow),
        TipItem("🌅", "First 30 Minutes", "Tiga puluh menit pertama setelah bangun — tanpa scroll. Mulai hari dengan otakmu sendiri.", NpaPink),
        TipItem("🚶", "Walk & Talk", "Jalan bareng orang yang kamu care tanpa earphone dan tanpa HP. Percakapan terbaik sering lahir dari sini.", NpaMint),
        TipItem("📦", "Phone Parking", "Satu tempat di rumah untuk 'parkir' HP saat kumpul. Laci, box kecil, luar ruangan — pilih tempatmu.", NpaBlue)
    )

    val myths = listOf(
        MythItem(
            myth = "Kalau ada HP di meja, berarti kamu tetap bisa fokus asal ga dipegang.",
            fact = "Penelitian menunjukkan kehadiran HP—bahkan yang terbalik—sudah cukup mengurangi kapasitas kognitif kamu hingga 10%.",
            mythBg = NpaYellow,
            factBg = NpaBlue
        ),
        MythItem(
            myth = "Cek notif cepat itu ga ganggu percakapan.",
            fact = "Rata-rata butuh 23 menit untuk kembali fokus penuh setelah distraksi digital—bahkan yang 'cuma sebentar'.",
            mythBg = NpaPink,
            factBg = NpaMint
        ),
        MythItem(
            myth = "Gen Z itu emang udah multitasking jadi HP bisa sambil ngobrol.",
            fact = "Tidak ada yang namanya multitasking — otak cuma task-switching. Setiap switch ada biaya konsentrasi yang dibayar.",
            mythBg = NpaLavender,
            factBg = NpaCoral
        ),
        MythItem(
            myth = "Kalau udah quality time, boleh dong sambil main HP sebentar.",
            fact = "Quality time yang terus-menerus tersela oleh HP sama seperti musik bagus yang diputus di tengah. Momennya ga pernah utuh.",
            mythBg = NpaMint,
            factBg = NpaYellow
        )
    )

    val screenTimeChart = listOf(
        ScreenTimePoint("2019", 3.6f, "3.6h", NpaBlue),
        ScreenTimePoint("2021", 4.0f, "4.0h", NpaMint),
        ScreenTimePoint("2023", 4.4f, "4.4h", NpaYellow),
        ScreenTimePoint("2024", 4.5f, "4.5h", NpaPink),
        ScreenTimePoint("2025", 4.63f, "4j 38m", NpaCoral)
    )

    val shockStats = listOf(
        ShockStatItem("414 M", "total jam orang Indonesia di aplikasi HP sepanjang 2025 — rank #2 dunia", NpaCoral),
        ShockStatItem("186×", "rata-rata orang mengecek HP-nya dalam sehari", NpaYellow),
        ShockStatItem("27%", "waktu bersama pasangan yang habis sambil pegang HP", NpaBlue),
        ShockStatItem("88%", "anak muda 13–24 tahun mengaku doomscrolling", NpaMint)
    )

    val fakePosts = listOf(
        FakePostItem("@si_tika", "Reminder bahwa kamu belum produktif hari ini 🙃", "4.2K", NpaCoral, "🧑‍🦱"),
        FakePostItem("@daily_doom", "7 hal yang akan menghancurkan hidupmu — no. 4 paling mengejutkan", "12K", NpaYellow, "😱"),
        FakePostItem("@health_bro", "Kamu makan itu tadi? Baca ini dulu sebelum menyesal", "8.7K", NpaBlue, "💪"),
        FakePostItem("@aesthetic.life", "POV: hidupmu masih jauh dari yang kamu inginkan", "31K", NpaLavender, "✨"),
        FakePostItem("@news_panic", "BREAKING: situasi semakin memburuk dan kamu tidak tahu apa-apa", "67K", NpaMint, "📰"),
        FakePostItem("@compare.core", "Teman lamamu baru aja beli mobil. Gimana denganmu?", "2.1K", NpaPink, "😮"),
        FakePostItem("@anxiety_feed", "Kalau kamu masih single di usia ini... thread 🧵", "44K", NpaCoral, "💔"),
        FakePostItem("@hustle.always", "Orang sukses ga punya waktu istirahat. Kalau kamu istirahat, kamu kalah.", "19K", NpaYellow, "🔥"),
        FakePostItem("@fomo.alert", "Semua orang udah cobain tren ini minggu lalu. Kamu ketinggalan?", "28K", NpaBlue, "⚡"),
        FakePostItem("@midnight.scroll", "Jam 1 pagi dan kamu masih baca ini? Satu post lagi deh...", "53K", NpaLavender, "🌙"),
        FakePostItem("@career.panic", "Umur 23 harusnya tabungan udah 9 digit, ini alasannya...", "15K", NpaMint, "💼"),
        FakePostItem("@drama.update", "Klarifikasi terbaru baru saja keluar — scroll ke bawah buat lihat reaksi netizen!", "91K", NpaPink, "🍿")
    )

    val simulatedNotifications = listOf(
        "🔔 3 balasan baru di komentarmu!",
        "❤️ @aesthetic.life menyukai postinganmu",
        "🔥 Sedang viral: 14rb orang melihat ini sekarang",
        "⚡ Jangan sampai terlewat! Konten baru untukmu",
        "📩 2 pesan DM belum kamu baca",
        "👀 9 temanmu sedang aktif di FeedApp™",
        "🔁 Postingan ini dibagikan 1.2K kali dalam 5 menit",
        "🎯 Algoritma menemukan topik yang bikin kamu penasaran"
    )

    val doomResults = listOf(
        DoomResultTier(0, 5, "Kamu cukup kebal doom.", "Skill langka banget sekarang ini. Nopi respect 🫡", NopiPose.WIN),
        DoomResultTier(5, 12, "Doomscroller pemula.", "Kamu sadar, tapi tetep ketarik. Wajar — algoritmanya memang dirancang untuk itu.", NopiPose.CROSSED),
        DoomResultTier(12, 25, "Udah level menengah.", "HP kamu tahu lebih banyak soal ketakutanmu daripada kamu sendiri. Mau break?", NopiPose.STICKER1),
        DoomResultTier(25, 9999, "Doomscroller sejati 💀", "Kamu bahkan tidak sadar kapan mulainya. Ini saatnya NPA.", NopiPose.DROP)
    )

    val challenges = listOf(
        ChallengeOption(
            id = "1d",
            label = "1 Hari",
            title = "NPA 24 Jam",
            desc = "Satu hari penuh — taruh HP saat kamu sama orang-orang yang kamu sayangi.",
            bg = NpaBlue,
            gradientColors = listOf(NpaBlue, NpaTeal),
            nopiLine = "Satu hari aja dulu. Kamu bisa.",
            emoji = "🌱",
            tagline = "Put the phone down. Pick the moment up."
        ),
        ChallengeOption(
            id = "3d",
            label = "3 Hari",
            title = "NPA Weekend",
            desc = "Tiga hari challenge — mulai dari momen makan, jalan, dan ngobrol.",
            bg = NpaCoral,
            gradientColors = listOf(NpaCoral, NpaYellow),
            nopiLine = "Tiga hari cukup untuk mulai ngerasain bedanya.",
            emoji = "🌿",
            tagline = "Less Screen, More Connection."
        ),
        ChallengeOption(
            id = "7d",
            label = "7 Hari",
            title = "NPA Week",
            desc = "Tujuh hari penuh — bangun habit baru dan rasakan perbedaan kualitas hidupmu.",
            bg = NpaMint,
            gradientColors = listOf(NpaMint, NpaBlue),
            nopiLine = "Satu minggu. Selamanya berubah.",
            emoji = "🌳",
            tagline = "Hadir sepenuhnya adalah bentuk kasih sayang."
        )
    )

    val storyNodes = mapOf(
        "start" to StoryNode(
            key = "start",
            text = "Kamu lagi makan malam sama teman lama. Pertama kali ketemu setelah 6 bulan. Dia lagi cerita sesuatu yang penting.",
            choices = listOf(
                StoryChoice("📱 Cek HP sebentar — ada notif masuk", "check"),
                StoryChoice("👀 Fokus ke dia, HP di saku", "focus")
            )
        ),
        "check" to StoryNode(
            key = "check",
            text = "Kamu buka HP — ternyata cuma promo belanja. Pas ngangkat muka, dia udah ganti topik. Senyumnya sedikit berbeda.",
            choices = listOf(
                StoryChoice("🔄 Coba ulang dari awal", "restart"),
                StoryChoice("💬 Minta dia cerita lagi", "apologize")
            )
        ),
        "focus" to StoryNode(
            key = "focus",
            text = "Kamu taruh HP face-down. Dia ngelihat itu dan senyum. Ceritanya terus mengalir — dan kamu dapet momen yang ga akan terlupakan.",
            choices = listOf(
                StoryChoice("✨ Lihat endingnya", "good_ending"),
                StoryChoice("🔄 Coba path lain", "restart")
            )
        ),
        "apologize" to StoryNode(
            key = "apologize",
            text = "Kamu minta maaf dan bilang fokus sekarang. Dia ngangguk, tapi energinya udah beda. Momen itu ga bisa di-rewind.",
            choices = listOf(
                StoryChoice("🔄 Coba lagi dari awal", "restart")
            )
        ),
        "good_ending" to StoryNode(
            key = "good_ending",
            text = "Dua jam berlalu tanpa sadar. HP tetap di saku. Waktu pulang, dia bilang: 'Makasih udah beneran hadir malam ini.' Kamu baru sadar betapa jarangnya itu.",
            choices = listOf(
                StoryChoice("🏠 Selesai — ulang dari awal", "restart")
            )
        )
    )

    val connectionStory = listOf(
        ConnectionStoryStep(
            narration = "Kamu baru duduk di meja makan. HP-mu bergetar — notifikasi grup kelas.",
            choices = listOf(
                ConnectionChoice("Taruh HP, lihat ke arah keluarga", true, "Mama tersenyum kecil, ngerasa diperhatikan."),
                ConnectionChoice("Cek notifikasi dulu, sebentar aja", false, "Mama diam. Obrolan yang tadi mau dimulai jadi hilang momennya.")
            )
        ),
        ConnectionStoryStep(
            narration = "Adikmu cerita soal hari sekolahnya, panjang dan penuh detail receh.",
            choices = listOf(
                ConnectionChoice("Dengerin sampai selesai, tanya balik", true, "Adikmu makin semangat cerita. Dia ngerasa didengar."),
                ConnectionChoice("Dengerin sambil scroll pelan-pelan", false, "Ceritanya jadi lebih pendek. Dia berhenti duluan.")
            )
        ),
        ConnectionStoryStep(
            narration = "Ayah nyeletuk nanya kabar kuliah/kerjaanmu — sedikit basa-basi, tapi tulus.",
            choices = listOf(
                ConnectionChoice("Jawab sambil natap balik, cerita sedikit lebih", true, "Obrolan ringan tapi hangat, jarang terjadi belakangan ini."),
                ConnectionChoice("Jawab singkat sambil balas chat", false, "Ayah cuma manggut-manggut, lanjut makan diam-diam.")
            )
        ),
        ConnectionStoryStep(
            narration = "HP bergetar lagi — kali ini telepon masuk, kelihatan penting.",
            choices = listOf(
                ConnectionChoice("Bilang 'nanti aku telepon balik', lanjut makan", true, "Keluarga notice kamu pilih mereka duluan."),
                ConnectionChoice("Angkat telepon di meja, ngobrol sambil makan", false, "Meja jadi sunyi. Semua nunggu kamu selesai telepon.")
            )
        ),
        ConnectionStoryStep(
            narration = "Makan malam hampir selesai. Ada momen hening sebentar di meja.",
            choices = listOf(
                ConnectionChoice("Ajak ngobrol topik ringan buat nutup malam", true, "Malam itu ditutup dengan tawa kecil bareng."),
                ConnectionChoice("Isi keheningan dengan buka HP", false, "Semua bubar diam-diam, tanpa banyak kata.")
            )
        )
    )

    val detoxStages = listOf(
        DetoxStage(0, "🌱", "🌱", "Mulai", "Taruh HP, tarik nafas."),
        DetoxStage(60, "🌿", "🌿", "Berkembang", "Sudah 1 menit. Teruskan."),
        DetoxStage(300, "🌳", "🌳", "Tumbuh", "5 menit! Kamu lagi hadir."),
        DetoxStage(900, "🌳🐦", "🐦", "Hidup", "15 menit bebas layar. Luar biasa."),
        DetoxStage(1800, "🧑‍🤝‍🧑", "🤝", "Terhubung", "Setengah jam. Koneksi nyata dimulai."),
        DetoxStage(3600, "☀️", "☀️", "NPA Mode", "Satu jam! Kamu legend.")
    )

    val moments = listOf(
        MomentItem("👁️", "Kontak mata yang bertahan", "Bukan yang cepat dialihkan ke layar. Yang beneran — yang terasa, yang diingat.", NpaBlue, "Ada momen di mana kamu tau seseorang beneran lihat kamu."),
        MomentItem("😂", "Tawa yang meledak bareng", "Bukan LOL di chat. Tawa yang bikin perut sakit dan mata berkaca — yang cuma bisa terjadi saat kamu beneran di situ.", NpaYellow, "Momen terbaik ga punya loading screen."),
        MomentItem("🤫", "Diam yang nyaman", "Saat kamu sama seseorang dan ga perlu bilang apa-apa. HP justru akan mengisi ruang yang baiknya dibiarkan.", NpaMint, "Kehadiran penuh bisa bersuara tanpa kata.")
    )

    val team = listOf(
        TeamMember("Erlangga Panca Pratama", "Web Developer", "Obsesi sama Notion dan deadline. Percaya bahwa hadir 100% bisa dipelajari.", R.drawable.team_rangga, NpaBlue, "@pncapratamaa", "ERLANGGA PANCA PRATAMA"),
        TeamMember("Cecilia Fransiska Tanuwijaya", "Web Developer", "Bikin visual yang bikin orang berhenti scroll — ironis, tapi intentional.", R.drawable.team_cecil, NpaCoral, "@ceeicilia", "Cecilia Fransiska Tanuwijaya"),
        TeamMember("Hanifa Sofia Az-zahra", "Research and Development", "Percaya bahwa desain yang baik adalah yang paling sedikit mengganggumu.", R.drawable.team_hansof, NpaMint, "@sofiabug._", "Hanifa Sofia Az-zahra"),
        TeamMember("Raffie Radityawan", "Web Developer", "Nulis kode sambil dengerin lofi. Hadir penuh — kecuali saat debugging.", R.drawable.team_rafli, NpaYellow, "@raffieradityawan_", "raffieradityawan"),
        TeamMember("Sabin Fredlina Aries", "Research & Development", "Suka riset, nulis, dan nanya 'tapi kenapa?' sampai ketemu jawaban yang jujur.", R.drawable.team_sabin, NpaLavender, "@fredllina", "Sabin Fredlina Aries"),
        TeamMember("Melody", "Motion Designer", "Animasi adalah bahasa. Dia lagi belajar ngomong lebih pelan.", R.drawable.team_ody, NpaPink, "@bimo.motion", "in/bimoaryanto"),
        TeamMember("Adinda Safina Nafiah", "Social Media Strategist", "Tau cara bikin konten yang bikin orang taruh HP — bukan buka HP.", R.drawable.team_fey, NpaTeal, "@nafeiah_", "Adinda Safinaa"),
        TeamMember("Miranda", "Social Media Strategist", "Tau cara bikin konten yang bikin orang taruh HP — bukan buka HP.", null, NpaLavenderSoft, "@adit.copy", "in/aditnugraha")
    )

    val tracks = listOf(
        MusicTrack("Shape of My Heart", "Backstreet Boys", "fMLDOTZ_rjE", NpaBlue, 261.63f),
        MusicTrack("Titik Nadir", "Kahitna", "QGZ1tR-xMLo", NpaCoral, 293.66f),
        MusicTrack("Jatuh Suka", "Tulus", "cQm2D_Mvjag", NpaMint, 329.63f),
        MusicTrack("On Bended Knee", "Boyz II Men", "iDsCUEegBfU", NpaLavender, 246.94f)
    )

    val footerSources = listOf(
        "We Are Social Digital Report 2024",
        "DataReportal Indonesia",
        "Ward & Wegner (2017) — Brain Drain Study",
        "American Psychological Association, Attention Research"
    )
}
