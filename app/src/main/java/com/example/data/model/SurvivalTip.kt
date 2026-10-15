package com.example.data.model

import com.example.util.AppLanguage

data class SurvivalTip(
    val id: String,
    val iconEmoji: String,
    val titleIt: String,
    val titleEn: String,
    val titleEs: String,
    val categoryIt: String,
    val categoryEn: String,
    val categoryEs: String,
    val contentIt: String,
    val contentEn: String,
    val contentEs: String,
    val actionStepsIt: List<String>,
    val actionStepsEn: List<String>,
    val actionStepsEs: List<String>
) {
    fun title(lang: AppLanguage) = when (lang) {
        AppLanguage.ITALIAN -> titleIt
        AppLanguage.ENGLISH -> titleEn
        AppLanguage.SPANISH -> titleEs
    }
    fun category(lang: AppLanguage) = when (lang) {
        AppLanguage.ITALIAN -> categoryIt
        AppLanguage.ENGLISH -> categoryEn
        AppLanguage.SPANISH -> categoryEs
    }
    fun content(lang: AppLanguage) = when (lang) {
        AppLanguage.ITALIAN -> contentIt
        AppLanguage.ENGLISH -> contentEn
        AppLanguage.SPANISH -> contentEs
    }
    fun steps(lang: AppLanguage) = when (lang) {
        AppLanguage.ITALIAN -> actionStepsIt
        AppLanguage.ENGLISH -> actionStepsEn
        AppLanguage.SPANISH -> actionStepsEs
    }
}

object SurvivalTipsData {
    val tips = listOf(
        SurvivalTip(
            id = "box_breathing",
            iconEmoji = "🧘",
            titleIt = "Respirazione Box Breathing (4-4-4-4)",
            titleEn = "Box Breathing (4-4-4-4)",
            titleEs = "Respiración Cuadrada (4-4-4-4)",
            categoryIt = "Ansia Acuta",
            categoryEn = "Acute Anxiety",
            categoryEs = "Ansiedad Aguda",
            contentIt = "Una tecnica scientificamente provata usata da soccorritori e atleti per riattivare il sistema nervoso parasimpatico e abbassare il cortisolo in pochi minuti.",
            contentEn = "Scientifically proven technique used to activate the parasympathetic nervous system and lower cortisol in minutes.",
            contentEs = "Técnica probada para activar el sistema parasimpático y reducir el cortisol en minutos.",
            actionStepsIt = listOf(
                "Inspira lentamente dal naso contando fino a 4",
                "Trattieni i polmoni pieni d'aria per 4 secondi",
                "Espira delicatamente dalla bocca per 4 secondi",
                "Trattieni i polmoni vuoti per altri 4 secondi",
                "Ripeti il ciclo per almeno 3-5 volte"
            ),
            actionStepsEn = listOf(
                "Inhale slowly through nose for 4 seconds",
                "Hold breath with full lungs for 4 seconds",
                "Exhale gently through mouth for 4 seconds",
                "Hold empty lungs for 4 seconds",
                "Repeat cycle 3-5 times"
            ),
            actionStepsEs = listOf(
                "Inhala lentamente por la nariz en 4 segundos",
                "Mantén el aire en los pulmones durante 4 segundos",
                "Exhala suavemente por la boca en 4 segundos",
                "Mantén los pulmones vacíos 4 segundos",
                "Repite el ciclo de 3 a 5 veces"
            )
        ),
        SurvivalTip(
            id = "grounding_54321",
            iconEmoji = "🌍",
            titleIt = "Tecnica di Radicamento 5-4-3-2-1",
            titleEn = "5-4-3-2-1 Grounding Technique",
            titleEs = "Técnica de Conexión 5-4-3-2-1",
            categoryIt = "Attacchi di Panico",
            categoryEn = "Panic Prevention",
            categoryEs = "Ataques de Pánico",
            contentIt = "Quando senti il cervello bloccarsi prima di una verifica, ancorati ai 5 sensi per spegnere la spirale di pensieri catastrofici.",
            contentEn = "When feeling mind block or panic before a test, anchor your 5 senses to break the spiral of catastrophic thoughts.",
            contentEs = "Si sientes bloqueo o pánico ante un examen, ancla tus sentidos para detener pensamientos negativos.",
            actionStepsIt = listOf(
                "Guarda 5 cose intorno a te (il banco, la penna, la finestra...)",
                "Tocca 4 oggetti con consistenza diversa (la cerniera, il foglio...)",
                "Ascolta 3 suoni nell'ambiente (il rumore delle auto, le voci...)",
                "Percepisci 2 odori nell'aria",
                "Di' a te stesso 1 verità rassicurante: 'Sono preparato, farò del mio meglio'"
            ),
            actionStepsEn = listOf(
                "Acknowledge 5 things you can see around you",
                "Touch 4 different textures (pen, desk, sweater)",
                "Listen for 3 distinct ambient sounds",
                "Notice 2 scents in the room",
                "Affirm 1 calming truth: 'I am capable and will do my best'"
            ),
            actionStepsEs = listOf(
                "Observa 5 cosas a tu alrededor",
                "Toca 4 objetos con diferentes texturas",
                "Escucha 3 sonidos del entorno",
                "Identifica 2 olores en el aire",
                "Repite 1 afirmación positiva: 'Estoy preparado y haré lo mejor que pueda'"
            )
        ),
        SurvivalTip(
            id = "pomodoro_study",
            iconEmoji = "⏱️",
            titleIt = "Metodo Pomodoro per lo Studio Senza Ansia",
            titleEn = "Pomodoro Method for Stress-Free Study",
            titleEs = "Método Pomodoro para Estudiar sin Estrés",
            categoryIt = "Metodo di Studio",
            categoryEn = "Study Method",
            categoryEs = "Método de Estudio",
            contentIt = "L'ansia spesso nasce dal vedere una montagna invalicabile di pagine. Dividere il lavoro in blocchi da 25 minuti rende il carico digeribile.",
            contentEn = "Anxiety often stems from seeing a mountain of unread pages. Segmenting into 25-minute sprints makes the load manageable.",
            contentEs = "El agobio suele surgir al ver un temario enorme. Dividir el trabajo en bloques de 25 minutos lo hace accesible.",
            actionStepsIt = listOf(
                "Scegli un solo argomento specifico o capitolo",
                "Metti il telefono in modalità Non Disturbare in un'altra stanza",
                "Studia per 25 minuti di pura concentrazione",
                "Prenditi 5 minuti di pausa reale (alzati, bevi acqua, muoviti)",
                "Dopo 4 cicli, regalati una pausa lunga di 20-30 minuti"
            ),
            actionStepsEn = listOf(
                "Pick one single specific topic or chapter",
                "Put phone on Do Not Disturb in another room",
                "Focus intensely for 25 minutes",
                "Take a true 5-minute break (stretch, drink water)",
                "After 4 intervals, take an extended 25-minute rest"
            ),
            actionStepsEs = listOf(
                "Elige un tema o capítulo específico",
                "Pon el teléfono en silencio fuera de tu vista",
                "Estudia 25 minutos con atención total",
                "Descansa 5 minutos reales (estírate, bebe agua)",
                "Cada 4 bloques, tómate una pausa de 20-30 minutos"
            )
        ),
        SurvivalTip(
            id = "bad_grade_coping",
            iconEmoji = "🛡️",
            titleIt = "Come Elaborare un Voto Negativo",
            titleEn = "How to Rebound From a Bad Grade",
            titleEs = "Cómo Superar una Mala Nota",
            categoryIt = "Resilienza",
            categoryEn = "Resilience",
            categoryEs = "Resiliencia",
            contentIt = "Un'insufficienza non è un giudizio sulla tua intelligenza né una condanna. È semplicemente un indicatore su cosa approfondire.",
            contentEn = "A poor grade is feedback on preparation strategy, never an indictment of your worth or intelligence.",
            contentEs = "Una mala nota evalúa una prueba puntual, nunca define tu inteligencia o tu valor personal.",
            actionStepsIt = listOf(
                "Non reagire a caldo: concediti qualche ora per far scendere l'adrenalina",
                "Riguarda la verifica con calma: analizza gli errori senza colpevolizzarti",
                "Chiedi al professore chiarimenti costruttivi sulle parti poco chiare",
                "Pianifica un ripasso mirato di 30 minuti al giorno",
                "Ricorda che l'anno è lungo e ogni insufficienza è recuperabile"
            ),
            actionStepsEn = listOf(
                "Do not react in anger: let adrenaline settle for a few hours",
                "Review the test objectively to understand error patterns",
                "Ask your teacher for constructive feedback on weak spots",
                "Set a focused 30-minute daily review schedule",
                "Remember: single grades don't dictate your future"
            ),
            actionStepsEs = listOf(
                "No reacciones en caliente; deja reposar la emoción",
                "Revisa la prueba con calma para entender los fallos",
                "Pide aclaraciones constructivas al profesor",
                "Dedica 30 minutos al día a repasar esos puntos",
                "Ten presente que hay tiempo y margen para recuperar"
            )
        )
    )
}
