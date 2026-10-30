package com.example

import com.example.data.model.SchoolMonthsData
import com.example.data.model.SurvivalTipsData
import com.example.util.AppLanguage
import com.example.util.SchoolCalendarUtils
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class ExampleUnitTest {

    @Test
    fun testSchoolCalendarCountdownCalculation() {
        val startDate = "2026-09-15"
        val endDate = "2027-06-10"
        val today = LocalDate.of(2026, 10, 15)

        val metrics = SchoolCalendarUtils.calculateMetrics(startDate, endDate, saturdayFree = true, today = today)

        assertTrue("Days remaining should be greater than zero", metrics.daysRemaining > 0)
        assertTrue("Progress should be between 0 and 100", metrics.progressPercent in 0f..100f)
        assertTrue("Actual school days should be positive and less than total days", metrics.actualSchoolDaysRemaining in 1..metrics.daysRemaining)
        assertFalse("Academic year should not be finished yet", metrics.isFinished)
        assertFalse("Academic year has already started", metrics.isNotStartedYet)
    }

    @Test
    fun testSchoolCalendarYearCompleted() {
        val startDate = "2026-09-15"
        val endDate = "2027-06-10"
        val today = LocalDate.of(2027, 7, 1)

        val metrics = SchoolCalendarUtils.calculateMetrics(startDate, endDate, saturdayFree = true, today = today)

        assertEquals(0L, metrics.daysRemaining)
        assertEquals(100f, metrics.progressPercent, 0.01f)
        assertTrue(metrics.isFinished)
    }

    @Test
    fun testSaturdayFreeCalculation() {
        val startDate = "2026-09-15"
        val endDate = "2027-06-10"
        val today = LocalDate.of(2026, 10, 15)

        val metricsWithSatFree = SchoolCalendarUtils.calculateMetrics(startDate, endDate, saturdayFree = true, today = today)
        val metricsWithSatClasses = SchoolCalendarUtils.calculateMetrics(startDate, endDate, saturdayFree = false, today = today)

        assertTrue(
            "School days when Saturday has classes should be greater than when Saturday is free",
            metricsWithSatClasses.actualSchoolDaysRemaining > metricsWithSatFree.actualSchoolDaysRemaining
        )
    }

    @Test
    fun testSchoolMonthsDataCoverage() {
        val months = SchoolMonthsData.months
        assertEquals(10, months.size) // September through June

        val september = SchoolMonthsData.forMonth(9)
        assertEquals("Settembre", september.nameIt)
        assertEquals("September", september.localizedName(AppLanguage.ENGLISH))
        assertTrue(september.survivalTipIt.isNotBlank())
    }

    @Test
    fun testSurvivalTipsData() {
        val tips = SurvivalTipsData.tips
        assertTrue("Survival tips must not be empty", tips.isNotEmpty())
        val boxBreathing = tips.first { it.id == "box_breathing" }
        assertTrue(boxBreathing.actionStepsIt.size >= 4)
    }

    @Test
    fun testMonthDifficultyAndProgress() {
        val november = SchoolMonthsData.forMonth(11)
        assertEquals(9, november.difficulty)
        assertTrue(november.difficulty in 1..10)

        val may = SchoolMonthsData.forMonth(5)
        assertEquals(10, may.difficulty)

        // Test month progress calculation for middle of month
        val octProgress = SchoolCalendarUtils.calculateMonthProgress(
            monthIndex = 10,
            startDateStr = "2026-09-15",
            endDateStr = "2027-06-10",
            today = LocalDate.of(2026, 10, 15)
        )
        assertTrue("October progress should be calculated", octProgress.progressPercent in 1..99)
        assertEquals(com.example.util.MonthProgressStatus.IN_PROGRESS, octProgress.status)

        // Past month should be completed
        val septProgress = SchoolCalendarUtils.calculateMonthProgress(
            monthIndex = 9,
            startDateStr = "2026-09-15",
            endDateStr = "2027-06-10",
            today = LocalDate.of(2026, 11, 1)
        )
        assertEquals(100, septProgress.progressPercent)
        assertEquals(com.example.util.MonthProgressStatus.COMPLETED, septProgress.status)

        // Future month should be upcoming
        val aprilProgress = SchoolCalendarUtils.calculateMonthProgress(
            monthIndex = 4,
            startDateStr = "2026-09-15",
            endDateStr = "2027-06-10",
            today = LocalDate.of(2026, 11, 1)
        )
        assertEquals(0, aprilProgress.progressPercent)
        assertEquals(com.example.util.MonthProgressStatus.UPCOMING, aprilProgress.status)
    }

    @Test
    fun testInvertedDatesEdgeCase() {
        // If user enters end date before start date
        val metrics = SchoolCalendarUtils.calculateMetrics(
            startDateStr = "2027-06-10",
            endDateStr = "2026-09-15",
            today = LocalDate.of(2026, 10, 15)
        )
        assertTrue(metrics.daysRemaining >= 0)
        assertTrue(metrics.progressPercent in 0f..100f)
    }

    @Test
    fun testInvalidDateStringsFallback() {
        val metrics = SchoolCalendarUtils.calculateMetrics(
            startDateStr = "not-a-date",
            endDateStr = "",
            today = LocalDate.of(2026, 10, 15)
        )
        assertNotNull(metrics)
        assertTrue(metrics.progressPercent in 0f..100f)
    }

    @Test
    fun testTranslationSetsCompleteness() {
        for (lang in AppLanguage.values()) {
            val trans = com.example.util.getTranslations(lang)
            assertTrue(trans.appName.isNotBlank())
            assertTrue(trans.loginTitle.isNotBlank())
            assertTrue(trans.daysRemainingTitle.isNotBlank())
            assertTrue(trans.noteSavedSuccess.isNotBlank())
            assertTrue(trans.daysShort.isNotBlank())
            assertTrue(trans.aboutAppSection.isNotBlank())
            assertTrue(trans.aboutAppDesc.isNotBlank())
            assertTrue(trans.versionLabel.isNotBlank())
            assertTrue(trans.openSourceNotice.isNotBlank())
            assertTrue(trans.settingsSectionsSubtitle.isNotBlank())
        }
    }

    @Test
    fun testSettingsSectionsEnum() {
        val sections = com.example.ui.settings.SettingsSection.values()
        assertEquals(4, sections.size)
        assertTrue(sections.contains(com.example.ui.settings.SettingsSection.ACADEMIC_DATES))
        assertTrue(sections.contains(com.example.ui.settings.SettingsSection.LANGUAGE))
        assertTrue(sections.contains(com.example.ui.settings.SettingsSection.ACCOUNT))
        assertTrue(sections.contains(com.example.ui.settings.SettingsSection.ABOUT))
    }

    @Test
    fun testLanguageCodeResolution() {
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("en"))
        assertEquals(AppLanguage.ITALIAN, AppLanguage.fromCode("it"))
        assertEquals(AppLanguage.SPANISH, AppLanguage.fromCode("es"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode("invalid_code"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode(null))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromCode(""))

        val profile = com.example.data.model.UserProfile(
            userId = "user123",
            email = "student@test.com",
            language = "it"
        )
        assertEquals("it", profile.language)
        assertEquals(AppLanguage.ITALIAN, AppLanguage.fromCode(profile.language))
    }

    @Test
    fun testNoteDateFormatting() {
        val timestamp = com.google.firebase.Timestamp.now()
        val note = com.example.data.model.Note(
            id = "note_1",
            title = "Math homework",
            content = "Study chapter 3",
            createdAt = timestamp
        )
        val dateFormat = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.ENGLISH)
        val formatted = try {
            note.createdAt?.toDate()?.let { dateFormat.format(it) } ?: "Recent"
        } catch (_: Exception) {
            "Recent"
        }
        assertNotNull(formatted)
        assertFalse(formatted.isBlank())
    }
}
