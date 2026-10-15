package com.example.data.model

import com.example.util.AppLanguage

data class MonthAtmosphere(
    val monthIndex: Int, // 1 to 12
    val nameIt: String,
    val nameEn: String,
    val nameEs: String,
    val moodTitleIt: String,
    val moodTitleEn: String,
    val moodTitleEs: String,
    val atmosphereIt: String,
    val atmosphereEn: String,
    val atmosphereEs: String,
    val survivalTipIt: String,
    val survivalTipEn: String,
    val survivalTipEs: String,
    val moodBadgeIt: String,
    val moodBadgeEn: String,
    val moodBadgeEs: String,
    val energyLevel: Int, // 1 to 5
    val stressLevel: Int, // 1 to 5
    val difficulty: Int = 5 // 1 to 10 scale
) {
    fun localizedName(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> nameEn
        AppLanguage.ITALIAN -> nameIt
        AppLanguage.SPANISH -> nameEs
    }

    fun localizedTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> moodTitleEn
        AppLanguage.ITALIAN -> moodTitleIt
        AppLanguage.SPANISH -> moodTitleEs
    }

    fun localizedAtmosphere(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> atmosphereEn
        AppLanguage.ITALIAN -> atmosphereIt
        AppLanguage.SPANISH -> atmosphereEs
    }

    fun localizedSurvivalTip(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> survivalTipEn
        AppLanguage.ITALIAN -> survivalTipIt
        AppLanguage.SPANISH -> survivalTipEs
    }

    fun localizedBadge(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> moodBadgeEn
        AppLanguage.ITALIAN -> moodBadgeIt
        AppLanguage.SPANISH -> moodBadgeEs
    }

    fun localizedDifficultyDescription(lang: AppLanguage): String = when (lang) {
        AppLanguage.ITALIAN -> when {
            difficulty <= 4 -> "Leggera"
            difficulty <= 6 -> "Media"
            difficulty <= 8 -> "Impegnativa"
            else -> "Critica 🔥"
        }
        AppLanguage.ENGLISH -> when {
            difficulty <= 4 -> "Manageable"
            difficulty <= 6 -> "Moderate"
            difficulty <= 8 -> "Challenging"
            else -> "Critical 🔥"
        }
        AppLanguage.SPANISH -> when {
            difficulty <= 4 -> "Llevadera"
            difficulty <= 6 -> "Moderada"
            difficulty <= 8 -> "Exigente"
            else -> "Crítica 🔥"
        }
    }
}

object SchoolMonthsData {
    val months = listOf(
        MonthAtmosphere(
            monthIndex = 9,
            nameIt = "Settembre",
            nameEn = "September",
            nameEs = "Septiembre",
            moodTitleIt = "Nuovo Inizio & Quaderni Puliti",
            moodTitleEn = "Fresh Start & Clean Notebooks",
            moodTitleEs = "Nuevo Comienzo & Cuadernos Limpios",
            atmosphereIt = "L'odore della carta nuova, l'incontro con i compagni e i buoni propositi. I ritmi si devono ancora assestare, ma la mente è ancora fresca dalle vacanze.",
            atmosphereEn = "The smell of new stationery, catching up with classmates, and high ambitions. Routines are resettling.",
            atmosphereEs = "El olor a papel nuevo, el reencuentro y los buenos propósitos. La rutina se está asentando.",
            survivalTipIt = "Stabilisci subito una routine del sonno realistica prima che arrivino le prime verifiche.",
            survivalTipEn = "Establish a steady sleep rhythm now before tests pile up.",
            survivalTipEs = "Establece un horario de sueño saludable antes de los primeros exámenes.",
            moodBadgeIt = "Speranza ✨",
            moodBadgeEn = "Hope ✨",
            moodBadgeEs = "Esperanza ✨",
            energyLevel = 4,
            stressLevel = 2,
            difficulty = 4
        ),
        MonthAtmosphere(
            monthIndex = 10,
            nameIt = "Ottobre",
            nameEn = "October",
            nameEs = "Octubre",
            moodTitleIt = "Si Entra nel Vivo",
            moodTitleEn = "Getting Down to Business",
            moodTitleEs = "En Plena Marcha",
            atmosphereIt = "Finita la fase di ripasso, iniziano le spiegazioni serie e i primi compiti in classe. Le giornate si accorciano e l'aria rinfresca.",
            atmosphereEn = "The review phase ends; serious lectures and the first graded tests begin as days get colder.",
            atmosphereEs = "Termina el repaso y comienzan las materias densas y los primeros controles.",
            survivalTipIt = "Non rimandare la sintesi degli appunti: 20 minuti al giorno ti risparmieranno notti in bianco.",
            survivalTipEn = "Summarize lecture notes daily: 20 minutes now saves panic later.",
            survivalTipEs = "Resume tus apuntes al día: 20 minutos te ahorrarán noches en vela.",
            moodBadgeIt = "Concentrazione 📚",
            moodBadgeEn = "Focus 📚",
            moodBadgeEs = "Concentración 📚",
            energyLevel = 3,
            stressLevel = 3,
            difficulty = 6
        ),
        MonthAtmosphere(
            monthIndex = 11,
            nameIt = "Novembre",
            nameEn = "November",
            nameEs = "Noviembre",
            moodTitleIt = "Il Mese della Grinta",
            moodTitleEn = "The Grit Month",
            moodTitleEs = "El Mes del Aguante",
            atmosphereIt = "Pieno autunno, nebbia e pioggia. È storicamente il mese con la più alta concentrazione di verifiche e interrogazioni del primo trimestre.",
            atmosphereEn = "Grey skies, rain, and the peak flurry of midterm exams and quizzes.",
            atmosphereEs = "Otoño pleno y la mayor concentración de evaluaciones del primer trimestre.",
            survivalTipIt = "Utilizza la tecnica Pomodoro (25/5). Quando l'ansia sale, fermati e fai 4 respiri profondi.",
            survivalTipEn = "Use the 25/5 Pomodoro rhythm. If overwhelmed, step back and take 4 slow breaths.",
            survivalTipEs = "Usa la técnica Pomodoro y respira hondo cuando sientas agobio.",
            moodBadgeIt = "Resistenza 🌧️",
            moodBadgeEn = "Grit 🌧️",
            moodBadgeEs = "Resistencia 🌧️",
            energyLevel = 2,
            stressLevel = 5,
            difficulty = 9
        ),
        MonthAtmosphere(
            monthIndex = 12,
            nameIt = "Dicembre",
            nameEn = "December",
            nameEs = "Diciembre",
            moodTitleIt = "Profumo di Vacanze & Rush Finale",
            moodTitleEn = "Holiday Spirit & Final Rush",
            moodTitleEs = "Ambiente Festivo & Esfuerzo Final",
            atmosphereIt = "L'ultima corsa disperata per rimediare all'insufficienza prima della chiusura del trimestre, con le luci di Natale a dare conforto.",
            atmosphereEn = "The sprint to fix borderline grades before break, sweetened by Christmas cheer.",
            atmosphereEs = "La carrera para recuperar asignaturas antes de las vacaciones navideñas.",
            survivalTipIt = "Pianifica le interrogazioni con i professori invece di evitarle: dimostrare proattività abbassa l'ansia.",
            survivalTipEn = "Schedule make-up tests proactively instead of dreading them.",
            survivalTipEs = "Planifica con los profesores las recuperaciones con anticipación.",
            moodBadgeIt = "Attesa 🎄",
            moodBadgeEn = "Anticipation 🎄",
            moodBadgeEs = "Expectación 🎄",
            energyLevel = 3,
            stressLevel = 4,
            difficulty = 8
        ),
        MonthAtmosphere(
            monthIndex = 1,
            nameIt = "Gennaio",
            nameEn = "January",
            nameEs = "Enero",
            moodTitleIt = "Giro di Boa & Pagelle",
            moodTitleEn = "Midpoint & Report Cards",
            moodTitleEs = "Ecuador del Curso & Notas",
            atmosphereIt = "Il rientro dal letargo natalizio, la chiusura ufficiale del primo periodo e le pagelle. È il momento del bilancio onesto.",
            atmosphereEn = "Waking up from the winter break, semester evaluations, and reality check.",
            atmosphereEs = "La vuelta tras las fiestas y la entrega de notas del primer trimestre.",
            survivalTipIt = "Un voto negativo a metà anno non definisce la tua fine. C'è tutto il tempo per recuperare passo dopo passo.",
            survivalTipEn = "A lower midterm grade is just diagnostic data, not your final outcome.",
            survivalTipEs = "Una mala nota a mitad de curso no es definitiva: hay tiempo de sobra.",
            moodBadgeIt = "Ripartenza ❄️",
            moodBadgeEn = "Restart ❄️",
            moodBadgeEs = "Reinicio ❄️",
            energyLevel = 3,
            stressLevel = 3,
            difficulty = 7
        ),
        MonthAtmosphere(
            monthIndex = 2,
            nameIt = "Febbraio",
            nameEn = "February",
            nameEs = "Febrero",
            moodTitleIt = "Breve ma Intenso",
            moodTitleEn = "Short but Intense",
            moodTitleEs = "Corto pero Intenso",
            atmosphereIt = "Carnevale, giornate ancora corte ma con un accenno di luce serale. Si avviano i corsi di recupero e i nuovi programmi.",
            atmosphereEn = "Carnival season, days getting slightly longer, second-semester acceleration.",
            atmosphereEs = "Días más luminosos y aceleración del temario del segundo periodo.",
            survivalTipIt = "Festeggia i piccoli traguardi. Concediti pause vere lontano dallo smartphone.",
            survivalTipEn = "Celebrate small micro-wins. Disconnect from screens during your actual breaks.",
            survivalTipEs = "Celebra pequeños logros y aléjate del móvil en los descansos.",
            moodBadgeIt = "Costanza 🎭",
            moodBadgeEn = "Patience 🎭",
            moodBadgeEs = "Constancia 🎭",
            energyLevel = 3,
            stressLevel = 3,
            difficulty = 7
        ),
        MonthAtmosphere(
            monthIndex = 3,
            nameIt = "Marzo",
            nameEn = "March",
            nameEs = "Marzo",
            moodTitleIt = "La Primavera alle Porte",
            moodTitleEn = "Spring Awakens",
            moodTitleEs = "Llega la Primavera",
            atmosphereIt = "Il cambio dell'ora e il primo sole portano una ventata di ossigeno, ma anche la stanchezza primaverile mentre i programmi avanzano rapidi.",
            atmosphereEn = "Longer days, warmer breezes, and seasonal fatigue as final units begin.",
            atmosphereEs = "Más horas de luz, buen tiempo y el inicio de la recta primaveral.",
            survivalTipIt = "Fai movimento all'aperto dopo la scuola: la luce solare stimola dopamina ed energia.",
            survivalTipEn = "Get 20 minutes of outdoor sunlight daily to shake off spring fatigue.",
            survivalTipEs = "Sal a dar un paseo al sol tras estudiar para renovar energías.",
            moodBadgeIt = "Rinascita 🌱",
            moodBadgeEn = "Renewal 🌱",
            moodBadgeEs = "Renacer 🌱",
            energyLevel = 4,
            stressLevel = 3,
            difficulty = 6
        ),
        MonthAtmosphere(
            monthIndex = 4,
            nameIt = "Aprile",
            nameEn = "April",
            nameEs = "Abril",
            moodTitleIt = "Vacanze di Pasqua & Gita Scolastica",
            moodTitleEn = "Easter Break & Field Trips",
            moodTitleEs = "Semana Santa & Viajes",
            atmosphereIt = "I ponti festivi, le gite d'istruzione e i primi sogni d'estate. L'ansia comincia a cedere il passo alla voglia di libertà.",
            atmosphereEn = "Short weeks, class trips, and sunny thoughts of the impending summer.",
            atmosphereEs = "Días festivos, excursiones escolares y ganas crecientes de verano.",
            survivalTipIt = "Ricarica davvero le batterie nelle vacanze pasquali: ti serviranno tutte a maggio!",
            survivalTipEn = "Truly unplug during the spring holiday; you will need reserve stamina for May.",
            survivalTipEs = "Descansa de verdad en vacaciones para afrontar mayo al 100%.",
            moodBadgeIt = "Sollievo 🌷",
            moodBadgeEn = "Relief 🌷",
            moodBadgeEs = "Alivio 🌷",
            energyLevel = 4,
            stressLevel = 2,
            difficulty = 5
        ),
        MonthAtmosphere(
            monthIndex = 5,
            nameIt = "Maggio",
            nameEn = "May",
            nameEs = "Mayo",
            moodTitleIt = "L'Ultimo Grande Sforzo",
            moodTitleEn = "The Final Sprint",
            moodTitleEs = "El Gran Esfuerzo Final",
            atmosphereIt = "Il mese più caldo ed elettrico: interrogazioni a catena, tesine, ansia da esame e il profumo d'estate che entra dalle finestre aperte.",
            atmosphereEn = "High adrenaline: back-to-back testing, projects, and summer breeze through open classroom windows.",
            atmosphereEs = "Máxima adrenalina: exámenes continuos, notas finales y calor primaveral.",
            survivalTipIt = "Visualizza il traguardo: mancano solo poche settimane. Pratica la respirazione Box Breathing prima di entrare in classe.",
            survivalTipEn = "Visualize the finish line. You are almost there. Practice grounding exercises before entering class.",
            survivalTipEs = "Visualiza la meta: faltan pocas semanas. Respira y confía en tu trabajo.",
            moodBadgeIt = "Adrenalina 🔥",
            moodBadgeEn = "Sprint 🔥",
            moodBadgeEs = "Adrenalina 🔥",
            energyLevel = 3,
            stressLevel = 5,
            difficulty = 10
        ),
        MonthAtmosphere(
            monthIndex = 6,
            nameIt = "Giugno",
            nameEn = "June",
            nameEs = "Junio",
            moodTitleIt = "Libertà & Traguardo Raggiunto!",
            moodTitleEn = "Freedom & Goal Achieved!",
            moodTitleEs = "¡Libertad & Meta Cumplida!",
            atmosphereIt = "L'ultima campanella, il lancio dei fogli, i saluti ai compagni e la sensazione impagabile di avercela fatta.",
            atmosphereEn = "The final school bell, throwing papers into the air, hugs, and the priceless joy of completion.",
            atmosphereEs = "La última campana, despedidas y la sensación inigualable de haberlo logrado.",
            survivalTipIt = "Sii fiero di ogni ostacolo che hai superato quest'anno. Ti meriti il miglior riposo del mondo!",
            survivalTipEn = "Be proud of every single hurdle you overcame this academic year. You earned this rest!",
            survivalTipEs = "Siéntete orgulloso de todo tu esfuerzo. ¡Te mereces unas vacaciones increíbles!",
            moodBadgeIt = "Vittoria ☀️",
            moodBadgeEn = "Victory ☀️",
            moodBadgeEs = "Victoria ☀️",
            energyLevel = 5,
            stressLevel = 1,
            difficulty = 4
        )
    )

    fun forMonth(month: Int): MonthAtmosphere {
        return months.find { it.monthIndex == month } ?: months.first()
    }
}
